package it.dtk.nlp

import com.typesafe.config.Config
import it.dtk.es.GeoFoss
import it.dtk.model._

import scala.util.Try


object FocusLocation {

  private var pool = Option.empty[FocusLocation]

  def getConnection(hosts: String, docPath: String, clusterName: String): FocusLocation = {
    if (pool.isEmpty)
      pool = Some(new FocusLocation(hosts, docPath, clusterName))
    pool.get
  }

  def close(): Unit = {
    pool.foreach(_.close())
  }
}


/**
  * Created by fabiofumarola on 11/02/16.
  */
class FocusLocation(hosts: String, docPath: String, clusterName: String) {

  val gfoss = new GeoFoss(hosts, docPath, clusterName)
  val locationConsts = List("PopulatedPlace", "Place", "Location")

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
    try {
      //all the locations which first letter start with a Uppercase Char
      val locations = article.annotations
        .filter(isLocation(_))
        .groupBy(_.surfaceForm).filter(_._1.charAt(0).isUpper)

      //get the most frequent province and filter the annotation which are not in this province
      val geoLocs = locations.map {
        case (loc, _) =>
          val l = gfoss.findLocation(loc)
          loc -> l.headOption
      }.filter(_._2.isDefined)
        .map(loc => loc._1 -> loc._2.get)
        .filterNot(e => e._1 == e._2.regionName)

      if (geoLocs.isEmpty) None
      else if (geoLocs.size == 1) {
        geoLocs.values.headOption
      } else {

        //get the frequent values in the frequent regions
        val regions = frequentValues(geoLocs.values.toSeq).map(_._1).toSet
        val frequentLocations = geoLocs.filter(v => regions.contains(v._2.regionName))
          .keySet

        val textLength = (article.title.length +
          article.description.length +
          article.cleanedText.length).toDouble

        //select the place that is most frequent and occurs with the lowest offset
        val scores = locations
          .filter(k => frequentLocations.contains(k._1))
          .map {
            case (loc, list) =>
              val posScore = list
                .map(_.offset.toDouble).sum

              loc -> posScore / textLength
          }

        val lowestValue = scores.toSeq.sortBy(_._2).head._1

        geoLocs.get(lowestValue)
      }
    } catch {
      case e: Exception =>
        e.printStackTrace()
        None
    }
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

  def close(): Unit = {
    gfoss.close()
  }

}
