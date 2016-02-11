package it.dtk

import org.joda.time.DateTime
import scala.concurrent.duration.{FiniteDuration, _}
import scala.language.postfixOps

/**
  * Here there should be only share data model!!!
  */
object model {

  case class SchedulerParameters(
                                  time: FiniteDuration = 10 minutes,
                                  delta: FiniteDuration = 2 minutes)

  case class Feed(url: String,
                  publisher: String,
                  parsedUrls: List[String],
                  lastTime: Option[DateTime],
                  count: Long = 0,
                  schedulerParams: SchedulerParameters = SchedulerParameters())

  case class Article(uri: String,
                     title: String,
                     description: String,
                     categories: List[String],
                     keywords: Seq[String] = List.empty,
                     imageUrl: String,
                     publisher: String,
                     date: DateTime,
                     lang: String = "",
                     cleanedText: String = "",
                     annotations: List[Annotation] = List.empty,
                     focusLocation: Option[Location] = None
                    )

  case class GoogleNewsTerms(list: List[String])


  case class Annotation(surfaceForm: String,
                        dbpediaUrl: String,
                        wikipediUrl: String,
                        `types`: Seq[AnnotationType],
                        offset: Int,
                        support: Int,
                        pin: Option[Pin] = None
                       )

  /**
    *
    * Given a raw annotation of the type "DBpedia:Location,Schema:Place,DBpedia:Place,Wikidata:Q486972,DBpedia:PopulatedPlace"
    *
    * @param src   the source of the annotation type, examples: DBPedia, Schema, Wikidata, DUL
    * @param value the value of the annotation type, examples: Location, Place, PopulatedPlace
    */
  case class AnnotationType(src: String, value: String)

  case class Location(id: Int,
                      cityName: String,
                      provinceId: Int,
                      provinceName: String,
                      regionId: Int,
                      regionName: String,
                      population: Int,
                      pin: Pin
                     )

  case class Pin(lat: Double, long: Double)

}
