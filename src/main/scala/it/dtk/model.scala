package it.dtk

import org.joda.time.DateTime

import scala.concurrent.duration.{ FiniteDuration, _ }
import scala.language.postfixOps

/**
 * Here there should be only share data model!!!
 */
object model {

  case class SchedulerData(
    nextRange: FiniteDuration = 30 minutes,
    deltaRange: FiniteDuration = 5 minutes,
    time: DateTime = DateTime.now
  )

  /**
   * helper companion object to setup the next schedule
   */
  object SchedulerData {
    def next(scheduler: SchedulerData, extractedUrls: Int) = {
      val now = DateTime.now
      extractedUrls match {
        case 0 =>
          val nextRange = scheduler.nextRange + scheduler.deltaRange
          val time = now plus nextRange.toMillis
          scheduler.copy(nextRange = nextRange, time = time)

        case x if x < 5 =>
          scheduler.copy(time = now plus scheduler.nextRange.toMillis)

        case x if x > 5 =>
          val nextRange = if (scheduler.nextRange > 5.minutes) {
            scheduler.nextRange - scheduler.deltaRange
          } else scheduler.nextRange
          scheduler.copy(nextRange = nextRange, time = now plus nextRange.toMillis)
      }
    }
  }

  /**
   *
   * @param url is the id for the feed
   * @param publisher
   * @param parsedUrls
   * @param lastTime
   * @param count
   * @param schedulerData
   */
  case class Feed(
    url: String,
    publisher: String,
    parsedUrls: List[String],
    lastTime: Option[DateTime],
    count: Long = 0,
    schedulerData: SchedulerData = SchedulerData()
  )

  case class Follower(screenName: String, twitterUserId: String)

  case class QueryTerm(
    terms: List[String],
    lang: String = "it",
    timestamp: Option[DateTime] = Some(DateTime.now().minusMinutes(10))
  )

  case class Article(
    uri: String,
    title: String,
    description: String,
    categories: List[String],
    keywords: Seq[String] = List.empty,
    imageUrl: String,
    publisher: String,
    date: Long, //mapped to DateTime
    lang: String = "",
    cleanedText: String = "",
    annotations: List[Annotation] = List.empty,
    focusLocation: Option[Location] = None
  )

  object DocumentSection extends Enumeration {
    type DocumentSection = Value
    val Title, Summary, Corpus, KeyWords, NotSet = Value
  }

  import DocumentSection._

  case class Annotation(
    surfaceForm: String,
    dbpediaUrl: String,
    wikipediUrl: String,
    `types`: Seq[AnnotationType],
    offset: Int,
    support: Int,
    pin: Option[Pin] = None,
    section: String = DocumentSection.NotSet.toString
  )

  /**
   *
   * Given a raw annotation of the type "DBpedia:Location,Schema:Place,DBpedia:Place,Wikidata:Q486972,DBpedia:PopulatedPlace"
   *
   * @param src   the source of the annotation type, examples: DBPedia, Schema, Wikidata, DUL
   * @param value the value of the annotation type, examples: Location, Place, PopulatedPlace
   */
  case class AnnotationType(src: String, value: String)

  case class Location(
    id: Int,
    cityName: String,
    provinceId: Int,
    provinceName: String,
    regionId: Int,
    regionName: String,
    population: Int,
    pin: Pin
  )

  case class Pin(lat: Double, long: Double)

  case class Tweet(
    id: String
  )

}