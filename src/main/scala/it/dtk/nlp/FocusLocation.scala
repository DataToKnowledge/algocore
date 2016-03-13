package it.dtk.nlp

import com.typesafe.config.Config
import it.dtk.es.GeoFoss
import it.dtk.model._

import scala.util.Try

/**
  * Created by fabiofumarola on 11/02/16.
  */
class FocusLocation(elasticHosts: String, docPath: String, clusterName: String) {

  val gfoss = new GeoFoss(elasticHosts, docPath, clusterName)
  val locationConsts = List("PopulatedPlace", "Place", "Location")

  def isLocation(a: Annotation): Boolean = {
    a.`types`
      .exists(t => locationConsts.exists(_.contains(t.value)))
  }

  def findLocations(article: Article): Map[String, (List[Annotation], Option[Location])] = {

    val candidateLocations = article.annotations
      .filter(isLocation)
      .groupBy(_.surfaceForm)
      .filter(_._1.charAt(0).isUpper)

    candidateLocations.map {
      case (name, annotations) =>
        name ->(annotations, gfoss.findLocation(name).headOption)
    }.filter(_._2._2.isDefined)
      .filter {
        case (name, (annotations, Some(loc))) =>
          name == loc.cityName
      }
  }

  def findMainLocation(article: Article): Option[Location] = {

    val candidates = findLocations(article)

    val textSize = article.cleanedText.length + article.title.length + article.description.length

    val locationsScore = candidates.map {
      case (name, (annotations, Some(location))) =>
        val score = annotations
          .map(_.offset).sum.toDouble / textSize
        name ->(location, score)
    }.toList

    val sortedByScore = locationsScore.sortBy(_._2._2)

    sortedByScore.headOption.map(_._2._1)
  }


  def close(): Unit = {
    gfoss.close()
  }

}
