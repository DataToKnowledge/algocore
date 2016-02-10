package it.dtk.nlp

import it.dtk.HttpDownloader
import it.dtk.model._
import org.json4s.JsonAST.{JArray, JString, JValue}
import org.json4s.jackson.JsonMethods._


class DBpediaSpotter(baseUrl: String, lang: String) {

  case class DbPediaTag(`@URI`: String,
                        `@support`: String,
                        `@types`: String,
                        `@surfaceForm`: String,
                        `@offset`: String,
                        `@similarityScore`: String,
                        `@percentageOfSecondRank`: String
                       )

  val serviceUrl = s"$baseUrl/rest/annotate"

  val http = HttpDownloader

  val headers = Map("Accept" -> "application/json")

  val wikipediaBase = lang match {

    case "it" => "http://it.wikipedia.org/wiki/"

    case _ => "https://en.wikipedia.org/wiki/"
  }

  implicit val formats = org.json4s.DefaultFormats ++ org.json4s.ext.JodaTimeSerializers.all

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

    http.wPost(serviceUrl, headers, parameters).map { res =>
      val json = parse(res.body)
      (json \ "Resources").extract[List[DbPediaTag]]
    }.getOrElse(List.empty[DbPediaTag])
  }

  /**
    *
    * @param text
    * @param minConf
    * @return return text annotated as Seq[Annotation]
    */
  def annotateText(text: String, minConf: Float = 0.2F): Seq[Annotation] = {
    tagText(text, minConf).map { tag =>

      Annotation(
        surfaceForm = tag.`@surfaceForm`,
        dbpediaUrl = tag.`@URI`,
        wikipediUrl = wikipediaBase + tag.`@surfaceForm`,
        `types` = DBpediaUtils.filter(tag.`@types`),
        offset = tag.`@offset`.toInt,
        support = tag.`@support`.toInt
      )
    }
  }
}

object DBpediaUtils {

  val jsonld = "?output=application%2Fld%2Bjson"

  val http = HttpDownloader

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

  def getResource(url: String): Option[JValue] = {
    http.wget(url + jsonld).map(r => parse(r.body))
  }

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

  def geoPoint(json: JValue): Option[Point] = {
    val raw = (json \ "@graph" \ "georss:point").values

    if (raw != None) {
      raw.toString.split(" ") match {
        case Array(lat, lon) =>
          Some(Point(lat.toString.toDouble, lon.toString.toDouble))

        case _ => None
      }
    } else None

  }
}