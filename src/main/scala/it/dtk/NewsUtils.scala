package it.dtk

import java.io.ByteArrayInputStream
import java.net.URL
import java.nio.charset.Charset
import java.util.Locale

import com.intenthq.gander.Gander
import com.rometools.rome.io.{ SyndFeedInput, XmlReader }
import it.dtk.model.Feed
import it.dtk.nlp.StopWords
import it.dtk.protobuf._
import org.apache.tika.language.LanguageIdentifier
import org.apache.tika.metadata.Metadata
import org.apache.tika.parser.html.HtmlParser
import org.apache.tika.parser.pdf.PDFParser
import org.apache.tika.parser.txt.TXTParser
import org.apache.tika.parser.{ ParseContext, Parser }
import org.apache.tika.sax.BodyContentHandler
import org.jsoup.Jsoup

import scala.collection.JavaConversions._
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Try

object NewsUtils {

  import com.github.nscala_time.time.Imports._
  import scala.concurrent.ExecutionContext.Implicits.global

  /**
   *
   * @param url
   * @param publisher
   * @return a seq the articles parsed using Rome
   */
  def parseFeed(url: String, publisher: String): Try[Seq[Article]] = Try {
    val input = new SyndFeedInput()
    val reader = input.build(new XmlReader(new URL(url)))

    reader.getEntries.map { e =>

      val description = extractText(e.getDescription.getValue)

      val index = if (e.getUri.lastIndexOf("http://") != -1)
        e.getUri.lastIndexOf("http://")
      else e.getUri.lastIndexOf("https://")

      val uri = e.getUri.substring(index, e.getUri.length)
      val keywords = urlKeywords(uri)
        .filterNot(w => StopWords.isStopWord(w))

      Article(
        uri = uri,
        title = extractText(e.getTitle),
        description = extractText(description),
        keywords = keywords,
        publisher = publisher,
        categories = e.getCategories.map(_.getName).toList,
        imageUrl = e.getEnclosures.map(_.getUrl).mkString(""),
        date = new DateTime(e.getPublishedDate).getMillis,
        cleanedText = description
      )
    }.toList
  }

  case class ContentLang(content: String, lang: String)

  def contentAndLangExtractor(html: String, contentType: String): ContentLang = {
    val in = new ByteArrayInputStream(html.getBytes(Charset.forName("UTF-8")))
    val metadata = new Metadata
    val bodyHandler = new BodyContentHandler()
    val context = new ParseContext
    val parser = getParser(contentType)

    try {
      parser.parse(in, bodyHandler, metadata, context)
    } catch {
      case e: Throwable =>
      //if there is an error we don't care
    } finally {
      in.close()
    }

    val body = bodyHandler.toString
    ContentLang(body, new LanguageIdentifier(body).getLanguage)
  }

  private def getParser(contentType: String): Parser = contentType match {
    case value if value contains "html" => new HtmlParser
    case value if value contains "plain" => new TXTParser
    case value if value contains "pdf" => new PDFParser
    case _ => new HtmlParser
  }

  def mainContent(art: Article): Future[Article] = {
    HttpDownloader.doGet(art.uri)
      .map(ws => Try(ws.body))
      .filter(_.isSuccess)
      .map(_.get)
      .map(body => Gander.extract(body))
      .map {
        case Some(page) =>
          val description = if (page.metaDescription.nonEmpty)
            page.metaDescription
          else art.description

          val date = page.publishDate
            .map(d => d.getTime).getOrElse(art.date)

          val metaKeywords = page.metaKeywords.split("[,\\s]+").filter(_.length > 0)
            .filterNot(w => StopWords.isStopWord(w))

          art.copy(
            description = description,
            keywords = art.keywords ++ metaKeywords,
            date = date,
            lang = page.lang.getOrElse(""),
            cleanedText = page.cleanedText.getOrElse("")
          )

        case None => art

      }
  }

  def extractText(html: String): String = Jsoup.parse(html).text()

  def host(url: String): String = {
    val uri = new URL(url)
    s"${uri.getProtocol}://${uri.getHost}"
  }

  def siteName(url: String): Future[String] = {
    HttpDownloader.doGet(url)
      .map(res => Jsoup.parse(res.body).title())
  }

  def urlKeywords(url: String): List[String] = {
    Try {
      val uri = new URL(url)
      uri.getPath.split("/|-|_").filter(_.length > 0).filterNot(_.contains(".")).toList
    } getOrElse List.empty[String]
  }

  def extractRss(url: String): Future[List[Feed]] = {
    val uri = host(url)
    HttpDownloader.doGet(uri)
      .map(_.body)
      .map { body =>
        val doc = Jsoup.parse(body)
        val title = doc.title()

        val elements = doc
          .select("link[type]")
          .filter(e => e.attr("type") == "application/rss+xml").toList

        elements.map(u => Feed(
          url = u.attr("abs:href"),
          publisher = title,
          parsedUrls = List.empty,
          lastTime = Some(DateTime.now())
        ))
      }
  }

  def extractFromGoogleNews(terms: List[String], lang: String): Try[Seq[Article]] = {
    val url = s"https://news.google.it/news?cf=all&hl=it&pz=1&ned=$lang&output=rss&q=${terms.mkString("+")}"
    parseFeed(url, "Google News")
  }

  def extractFromGoogleNews(terms: String, lang: String): Try[Seq[Article]] =
    extractFromGoogleNews(terms.split(" ").toList, lang)

}

/**
 * Used to find news based on term queries
 */
object QueryTermsSearch {

  import java.text.SimpleDateFormat

  import com.github.nscala_time.time.Imports._
  import org.json4s._
  import org.json4s.jackson.JsonMethods._
  import scala.concurrent.ExecutionContext.Implicits.global

  val dateFormatter = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss Z", Locale.US)

  implicit val formats = org.json4s.DefaultFormats ++ org.json4s.ext.JodaTimeSerializers.all

  case class Image(
    url: String,
    tbUrl: String,
    originalContextUrl: String,
    publisher: String,
    tbWidth: Int,
    tbHeight: Int
  )

  case class SearchResult(
    content: String,
    unescapedUrl: String,
    url: String,
    titleNoFormatting: String,
    publisher: String,
    publishedDate: String,
    language: String,
    image: Option[Image]
  )

  def decode(url: String): String =
    java.net.URLDecoder.decode(url, "UTF-8")

  //  val example = "https://ajax.googleapis.com/ajax/services/search/news?v=1.0&q=furti%20puglia&hl=it&rsz=8&scoring=d&start=0"

  /**
   *
   * @param query
   * @param lang
   * @param ipAddress
   * @return a List of generate urls with the given term queries
   */
  def generateUrls(query: List[String], lang: String, ipAddress: String): Seq[String] = {
    val urlQuery = query.mkString("%20")
    val starts = (0 until 8).map(_ * 8)

    val baseUrl = s"https://ajax.googleapis.com/ajax/services/search/news?v=1.0" +
      s"&q=$urlQuery&hl=$lang&rsz=8&scoring=d&userip=$ipAddress&start="
    starts.map(start => baseUrl + start)
  }

  def generateUrl(query: List[String], lang: String, ipAddress: String): String = {
    val urlQuery = query.mkString("%20")
    val baseUrl = s"https://ajax.googleapis.com/ajax/services/search/news?v=1.0" +
      s"&q=$urlQuery&hl=$lang&rsz=8&scoring=d&start=0" //&userip=$ipAddress
    baseUrl
  }

  /**
   *
   * @param url
   * @return return a list of search Results
   */
  def getResults(url: String): Future[List[SearchResult]] = {
    HttpDownloader.doGet(url).map { res =>
      val json = parse(res.body)
      (json \ "responseData" \ "results").extract[List[SearchResult]]
    }
  }

  def getResultsAsArticles(url: String): Future[List[Article]] = {
    getResults(url).map { list =>
      list.map { res =>
        val date = Try(dateFormatter.parse(res.publishedDate))
          .map(d => new DateTime(d))
          .getOrElse(DateTime.now())

        Article(
          uri = decode(res.unescapedUrl),
          title = res.titleNoFormatting,
          description = res.content,
          categories = List.empty,
          keywords = NewsUtils.urlKeywords(res.unescapedUrl),
          imageUrl = res.image.map(_.url).getOrElse(""),
          publisher = res.publisher,
          date = date.getMillis,
          lang = res.language,
          cleanedText = res.content
        )
      }
    }
  }
}

