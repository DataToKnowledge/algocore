package it.dtk.nlp

import it.dtk.HttpDownloader
import it.dtk.model._
import org.json4s.JsonAST.JValue
import org.json4s.jackson.JsonMethods._

import scala.util.Try

/**
  * Query a dbpedia spotlight service
  *
  * @param baseUrl
  * @param lang
  */
class BBpediaSpotLight(baseUrl: String, lang: String) {

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
    val annotations = tagText(text, minConf).map { tag =>
      Annotation(
        surfaceForm = tag.`@surfaceForm`,
        dbpediaUrl = tag.`@URI`,
        wikipediUrl = wikipediaBase + tag.`@surfaceForm`,
        `types` = DBpedia.filter(tag.`@types`),
        offset = tag.`@offset`.toInt,
        support = tag.`@support`.toInt
      )
    }
    annotations.map(enrichAnnotation)
  }

  /**
    * @param a annotation
    * @return other annotation extracted using DBpedia
    */
  def enrichAnnotation(a: Annotation): Annotation = {
    Try {
      val optJson = DBpedia.getResource(a.dbpediaUrl)

      if (optJson.nonEmpty) {
        val json = optJson.get

        val extractedTypes = a.`types` ++ DBpedia.getTypes(json) ++
          DBpedia.getOntologyClasses(json) ++
          DBpedia.getCategory(json)

        val pin = DBpedia.geoPoint(json)

        a.copy(`types` = extractedTypes, pin = pin)
      } else a

    }.getOrElse(a)
  }

}

/**
  * Query dbpedia to extract additional annotations
  * example of resource queried http://it.dbpedia.org/resource/Capurso/html?output=application%2Fld%2Bjson
  */
object DBpedia {

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

    if (raw != None) {
      raw.toString.split(" ") match {
        case Array(lat, lon) =>
          Some(Pin(lat.toString.toDouble, lon.toString.toDouble))

        case _ => None
      }
    } else None
  }
}