package it.dtk.nlp

import com.typesafe.config.Config
import it.dtk.es.GeoFoss
import it.dtk.model._

import scala.util.Try

/**
  * Created by fabiofumarola on 11/02/16.
  */
class FocusLocation(config: Config) {

  val gfoss = new GeoFoss(config)
  val locationConsts = List("PopulatedPlace", "Place")

  def isLocation(a: Annotation): Boolean = {
    a.`types`
      .exists(t => locationConsts.exists(_.contains(t.value)))
  }

  private case class Score(location: String,
                           posScore: Double = 0,
                           geoScore: Double = 0)


  /**
    *
    * @param article
    * @return computes the focus location
    *
    */
  def extract(article: Article): Option[Location] = {

    val locations = article.annotations
      .filter(isLocation(_))
      .groupBy(_.surfaceForm)

    //get the most frequent province and filter the annotation which are not in this province
    val geoLocations = locations.map {
      case (loc, _) =>
        val gfossLoc = Try(gfoss.findLocation(loc).headOption).getOrElse(None)
        loc -> gfossLoc
    }.filter(_._2.isDefined)
      .map(locOpt => locOpt._1 -> locOpt._2.get)


    //get the frequent values in the frequent regions
    val frequentRegions = frequentValues(geoLocations.values.toSeq).map(_._1).toSet
    val frequentLocations = geoLocations.filter(v => frequentRegions.contains(v._2.regionName))
      .keySet

    val textLength = (article.title.length +
      article.description.length +
      article.cleanedText.length).toDouble

    //select the place that is most frequent and occours with the lowest offset
    val scores = locations
      .filter(k => frequentLocations.contains(k._1))
      .map {
        case (loc, list) =>
          val posScore = list.reduce((l1, l2) =>
            l1.offset.toDouble + l2.offset) / textLength
          loc -> posScore
      }

    val lowestValue = scores.toSeq.sortBy(_._2).head._1

    geoLocations.get(lowestValue)
  }

  private def frequentValues(locations: Seq[Location], minFreq: Double = 0.5D): Seq[(String, Double)] = {
    val frequentList = locations
      .groupBy(_.regionName)
      .map {
        case (prov, list) => prov -> list.size.toDouble / locations.length
      }
      .toList
      .sortBy(_._2).reverse

    frequentList.filter(_._2 >= minFreq)
  }


}
