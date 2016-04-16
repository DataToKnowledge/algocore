package it.dtk

import it.dtk.protobuf._
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
        case 0 ⇒
          val nextRange = scheduler.nextRange + scheduler.deltaRange
          val time = now plus nextRange.toMillis
          scheduler.copy(nextRange = nextRange, time = time)

        case x if x < 5 ⇒
          scheduler.copy(time = now plus scheduler.nextRange.toMillis)

        case x if x > 5 ⇒
          val nextRange = if (scheduler.nextRange > 5.minutes) {
            scheduler.nextRange - scheduler.deltaRange
          } else scheduler.nextRange
          scheduler.copy(nextRange = nextRange, time = now plus nextRange.toMillis)
      }
    }
  }

  /**
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

  case class QueryTerm(
    terms: List[String],
    lang: String = "it",
    timestamp: Option[DateTime] = Some(DateTime.now().minusMinutes(10))
  )

  case class FlattenedNews(
    uri: String,
    title: String,
    description: String,
    categories: Seq[String],
    keywords: Seq[String] = List.empty,
    imageUrl: String,
    publisher: String,
    date: DateTime,
    lang: String = "",
    text: String = "",
    cityName: String = "",
    provinceName: String = "",
    regionName: String = "",
    crimes: Seq[String],
    locations: Seq[String],
    persons: Seq[String],
    semanticNames: Seq[String],
    semanticTags: Seq[String],
    annotations: Seq[SemanticTag],
    pin: Option[it.dtk.protobuf.Pin]
  )

  case class SemanticTag(
    name: String,
    wikipediaUrl: String,
    tags: Set[String],
    pin: Option[it.dtk.protobuf.Pin],
    support: Long
  )

  case class Pin(lat: Double, lon: Double)

  case class Tweet(
    id: String
  )

  case class Follower(screenName: String, twitterUserId: String)

}