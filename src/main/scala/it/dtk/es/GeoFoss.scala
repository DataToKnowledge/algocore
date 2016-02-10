package it.dtk.es

import com.sksamuel.elastic4s.source.Indexable
import com.sksamuel.elastic4s.{ElasticClient, ElasticsearchClientUri}
import com.typesafe.config.{ConfigFactory, Config}
import it.dtk.es.GeoFoss._
import net.ceedubs.ficus.Ficus._
import org.elasticsearch.common.settings.Settings
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write
import com.sksamuel.elastic4s.ElasticDsl._

import scala.io.Source

object GeoFoss {

  case class Location(id: Int,
                      cityName: String,
                      provinceId: Int,
                      provinceName: String,
                      regionId: Int,
                      regionName: String,
                      population: Int,
                      sourceId: Int,
                      pin: Pin
                     )

  case class Pin(lat: Double, long: Double)

}

/**
  * Created by fabiofumarola on 08/02/16.
  */
class GeoFoss(config: Config) {

  implicit val formats = Serialization.formats(NoTypeHints)

  implicit object LocationIndexable extends Indexable[Location] {
    override def json(l: Location): String = write(l)
  }

  private val conf = config.getConfig("algocore.elasticsearch")
  private val hosts = conf.as[String]("hosts")
  private val indexType = conf.as[String]("docs.location")
  private val clusterName = conf.as[String]("clusterName")

  val settings = Settings.settingsBuilder()
    .put("cluster.name", clusterName)
    .build()

  val client = ElasticClient.transport(settings,
    ElasticsearchClientUri(s"elasticsearch://$hosts"))

  def loadInitialData(): Unit = {

    val indexReq = loadCsv()
      .map(l => index into indexType id l.id source l)

    indexReq
      .grouped(200)
      .foreach { seq =>
        client.execute(bulk(seq)).await
      }
  }


  private def loadCsv(): Iterator[Location] = {
    val path = GeoFoss.getClass.getResource("/gfossdata.csv")

    val locations = Source.fromFile(path.getFile)
      .getLines()
      .map(_.split("\\|"))
      .filter(_.length == 12)
      .map { split =>
        split match {
          case Array(id, istatId, cityName, provId, provName, regId, regName,
          pop, srcId, wikiUrl, geoUrl, geoLoc) =>
            Location(
              id = id.toInt,
              cityName = cityName,
              provinceId = provId.toInt,
              provinceName = provName,
              regionId = regId.toInt,
              regionName = regName,
              population = pop.toInt,
              sourceId = srcId.toInt,
              pin = geoLoc.split(",").map(l => Pin(l(0), l(1))).head
            )
        }
      }
    locations
  }
}

object GFossIndexerMain extends App {
  val config = ConfigFactory.load("mac_dev.conf")
  val gfoss = new GeoFoss(config)
  gfoss.loadInitialData()
}