package it.dtk.es

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.source.Indexable
import com.sksamuel.elastic4s.{ ElasticClient, ElasticsearchClientUri }
import com.typesafe.config.ConfigFactory
import it.dtk.protobuf._
import net.ceedubs.ficus.Ficus._
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.index.query.MatchQueryBuilder
import org.json4s._
import org.json4s.ext.JodaTimeSerializers
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write
import scala.concurrent.duration._
import scala.io.Source

/**
 * Created by fabiofumarola on 08/02/16.
 */
class GeoFoss(elasticHosts: String, docPath: String, clusterName: String) {

  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val formats = Serialization.formats(NoTypeHints) ++ JodaTimeSerializers.all

  implicit object LocationIndexable extends Indexable[Location] {
    override def json(l: Location): String = write(l)
  }

  private val settings = Settings.settingsBuilder().put("cluster.name", clusterName).build()

  val client = ElasticClient.transport(settings, ElasticsearchClientUri(s"elasticsearch://$elasticHosts"))

  def loadInitialData(): Unit = {

    val indexReq = loadCsv()
      .map(l => index into docPath id l.id source l)

    implicit val duration: Duration = 360.seconds

    indexReq
      .grouped(200)
      .foreach { seq =>
        client.execute(bulk(seq)).await
      }
  }

  /**
   *
   * @param name
   * @param maxCount
   * @return a location by name
   */
  def findLocation(name: String, maxCount: Int = 1): Seq[Location] = {
    val query = client.execute {
      search in docPath limit maxCount query {
        matchQuery("cityName", name).operator(MatchQueryBuilder.Operator.AND)
      }
    }

    query.map { r =>
      r.getHits.hits()
        .map(s => parse(s.getSourceAsString).extract[Location])
    }.await
  }

  private def loadCsv(): Iterator[Location] = {

    val path = this.getClass.getResource("/gfossdata.csv")

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
              //              provinceId = provId.toInt,
              provinceName = provName,
              //              regionId = regId.toInt,
              regionName = regName,
              //              population = pop.toInt,
              pin = geoLoc.split(",").map(l => Pin(l(0), l(1))).head
            )
        }
      }
    locations
  }

  def close(): Unit = {
    client.close()
  }
}

//object GFossIndexerMainDev extends App {
//  val config = ConfigFactory.load("mac_dev.conf")
//  private val conf = config.getConfig("algocore.elasticsearch")
//  private val hosts = conf.as[String]("hosts")
//  private val docPath = conf.as[String]("docs.location")
//  private val clusterName = conf.as[String]("clusterName")
//
//  val gfoss = new GeoFoss(hosts, docPath, clusterName)
//  gfoss.loadInitialData()
//  gfoss.close()
//}
//
//object GFossIndexerMainProd extends App {
//  val config = ConfigFactory.load("linux_prod.conf")
//  private val conf = config.getConfig("algocore.elasticsearch")
//  private val hosts = conf.as[String]("hosts")
//  private val docPath = conf.as[String]("docs.location")
//  private val clusterName = conf.as[String]("clusterName")
//
//  val gfoss = new GeoFoss(hosts, docPath, clusterName)
//  gfoss.loadInitialData()
//  gfoss.close()
//}