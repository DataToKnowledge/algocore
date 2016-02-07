package it.dtk.nlp

import it.dtk.HttpDownloader
import it.dtk.model._
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

      val types = tag.`@types`
        .split(",")
        .map(_.split(":"))
        .filter(_.length == 2)
        .map(array => AnnotationType(array(0), array(1)))

      Annotation(
        surfaceForm = tag.`@surfaceForm`,
        dbpediaUrl = tag.`@URI`,
        wikipediUrl = wikipediaBase + tag.`@surfaceForm`,
        `types` = types.toSeq,
        offset = tag.`@offset`.toInt,
        support = tag.`@support`.toInt
      )
    }
  }

}