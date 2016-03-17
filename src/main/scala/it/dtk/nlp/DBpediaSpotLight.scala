package it.dtk.nlp

import it.dtk.HttpDownloader
import it.dtk.protobuf._
import org.json4s.JsonAST.JValue
import org.json4s.jackson.JsonMethods._
import play.api.libs.json._
import scala.collection.mutable
import scala.util.Try
import it.dtk.protobuf.Annotation.DocumentSection

case class DbPediaTag(
                       `@URI`: String,
                       `@support`: String,
                       `@types`: String,
                       `@surfaceForm`: String,
                       `@offset`: String,
                       `@similarityScore`: String,
                       `@percentageOfSecondRank`: String
                     )

/**
  * Query a dbpedia spotlight service
  *
  * @param baseUrl
  * @param lang
  */
class DBpediaSpotLight(val baseUrl: String, val lang: String) {

  val serviceUrl = s"$baseUrl/rest/annotate"

  val http = HttpDownloader

  val headers = Map("Accept" -> "application/json")

  val wikipediaBase = lang match {

    case "it" => "http://it.wikipedia.org/wiki/"

    case _ => "http://en.wikipedia.org/wiki/"
  }

  implicit val formats = org.json4s.DefaultFormats

  /**
    *
    * @param text
    * @param minConf
    * @return the text annotated with the dbpedia spotlight reources
    */
  def tagText(text: String, minConf: Float = 0.15F): Seq[DbPediaTag] = {
    val parameters = Map(
      "text" -> Seq(text),
      "confidence" -> Seq(minConf.toString)
    )
    http.wPost(serviceUrl, headers, parameters).flatMap { res =>
      Try {
        val json = parse(res.body)
        (json \ "Resources").extract[List[DbPediaTag]]
      }.recover {
        case ex: Exception =>
          println(res.body)
          println(text)

          List.empty[DbPediaTag]
      }.toOption
    }.getOrElse(List.empty[DbPediaTag])
  }

  def playTagText(text: String, minConf: Float = 0.15F): Seq[DbPediaTag] = {

    val parameters = Map(
      "text" -> Seq(text),
      "confidence" -> Seq(minConf.toString)
    )

    implicit val tagReads = Json.reads[DbPediaTag]

    http.wPost(serviceUrl, headers, parameters).map { res =>
      (res.json \ "Resources").validate[List[DbPediaTag]].get
    }.getOrElse(List.empty[DbPediaTag])
  }

  /**
    *
    * @param text
    * @param minConf
    * @return return text annotated as Seq[Annotation]
    */
  def annotateText(text: String, section: DocumentSection.EnumVal, minConf: Float = 0.15F): Seq[Annotation] = {
    val annotations = tagText(text, minConf).map { tag =>
      Annotation(
        surfaceForm = tag.`@surfaceForm`,
        dbpediaUrl = tag.`@URI`,
        wikipediaUrl = wikipediaBase + tag.`@surfaceForm`,
        `types` = DBpedia.filter(tag.`@types`).distinct,
        offset = tag.`@offset`.toInt,
        support = tag.`@support`.toInt,
        section = section
      )
    }
    annotations
      .filterNot(a => StopWords.isStopWord(a.surfaceForm))
      .map(enrichAnnotation)
  }

  /**
    * @param a annotation
    * @return other annotation extracted using DBpedia website
    */
  def enrichAnnotation(a: Annotation): Annotation = {
    val t = Try {
      val optJson = DBpedia.getResource(a.dbpediaUrl)

      if (optJson.nonEmpty) {
        val json = optJson.get

        val extractedTypes = a.`types` ++ DBpedia.getTypes(json) ++
          DBpedia.getOntologyClasses(json) ++
          DBpedia.getCategory(json)

        val pin = DBpedia.geoPoint(json)

        a.copy(`types` = extractedTypes.distinct, pin = pin)
      } else a

    }

    //    if (t.isFailure) {
    //      t.failed.get.printStackTrace()
    //    }

    t.getOrElse(a)
  }

  def close(): Unit = {
    http.close()
  }
}

/**
  * Query dbpedia to extract additional annotations
  * example of resource queried http://it.dbpedia.org/resource/Capurso/html?output=application%2Fld%2Bjson
  */
object DBpedia {

  private val pool = mutable.Map.empty[String, DBpediaSpotLight]

  def getConnection(baseUrl: String, lang: String): DBpediaSpotLight = {
    pool.getOrElseUpdate(baseUrl, new DBpediaSpotLight(baseUrl, lang))
  }

  //  def closePool(): Unit = {
  //    pool.values.foreach(_.close())
  //  }

  val jsonld = "?output=application%2Fld%2Bjson"

  val http = HttpDownloader

  /**
    *
    * @param dbpediaUrl
    * @return
    */
  def getResource(dbpediaUrl: String): Option[JValue] = {
    http.wget(dbpediaUrl + jsonld).map(r => parse(r.body))
  }

  val filters = "http" :: "DUL" :: "gml" :: Nil

  def filter(raw: String): List[AnnotationType] =
    filter(raw.split(",").toList)

  def filter(list: List[String]): List[AnnotationType] = {
    list.filterNot { e =>
      filters.exists(p => e.contains(p))
    }.map(_.split(":"))
      .filter(_.length == 2)
      .map(array => AnnotationType(array(0), array(1)))
  }

  /**
    *
    * @param json
    * @return the types extract from the dbpedia result
    */
  def getTypes(json: JValue): List[AnnotationType] = {
    val cand = (json \ "@graph" \ "@type").values match {
      case list: List[String] => list
      case str: String => List(str)
    }
    filter(cand)
  }

  def getOntologyClasses(json: JValue): List[AnnotationType] = {
    val cand = (json \ "@graph" \ "").values match {
      case list: List[String] => list
      case str: String => List(str)
    }
    filter(cand)
  }

  def getAbstract(json: JValue): String =
    (json \ "@graph" \ "abstract" \ "@value").values.toString

  def getThumbnail(json: JValue): String =
    (json \ "@graph" \ "thumbnail").values.toString

  def getCategory(json: JValue): List[AnnotationType] = {
    val cand = (json \ "@graph" \ "subject").values.toString
    filter(cand)
  }

  def geoPoint(json: JValue): Option[Pin] = {
    val raw = (json \ "@graph" \ "georss:point").values

    val latLonRegex = "^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?),\\s*[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)$"

    if (raw != None) {
      raw.toString.split(" ") match {
        case Array(lat, lon) =>
          if (s"$lat, $lon".matches(latLonRegex)) {
            val latV = lat.split("E").head
            val lonV = lon.split("E").head
            Some(Pin(latV.toDouble, lonV.toDouble))
          } else None

        case _ => None
      }
    } else None
  }
}