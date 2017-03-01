package it.dtk.es

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.Indexable
import it.dtk.protobuf._
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
class GeoFoss(hosts: String, indexType: String, docType: String, clusterName: String) {

  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val formats = Serialization.formats(NoTypeHints) ++ JodaTimeSerializers.all

  implicit object LocationIndexable extends Indexable[Location] {
    override def json(l: Location): String = write(l)
  }

  val client = ESUtil.elasticClient(hosts, clusterName)

  def loadInitialData(): Int = {

    val indexReq = loadCsv()
      .map(l => indexInto(indexType, docType) id l.id source l)

    implicit val duration: Duration = 60.seconds

    indexReq
      .grouped(200)
      .map(seq => client.execute(bulk(seq)).await)
      .map(_.successes.length)
      .sum
  }

  /**
   *
   * @param name
   * @param maxCount
   * @return a location by name
   */
  def findLocation(name: String, maxCount: Int = 1): Seq[Location] = {
    val query = client.execute {
      search(indexType, docType) limit maxCount query {
        matchQuery("cityName", name).operator(org.elasticsearch.index.query.Operator.AND)
      }
    }
    query.map { r =>
      r.hits
        .map(s => parse(s.sourceAsString).extract[Location])
    }.await
  }

  def indexExists(): Boolean = client.execute {
    index exists indexType + "/" + docType
  }.await.isExists

  private def loadCsv(): Iterator[Location] = {
    val in = this.getClass.getResourceAsStream("/gfossdata.csv")

    val locations = Source.fromInputStream(in)
      .getLines()
      .map(_.split("\\|"))
      .filter(_.length == 12)
      .map {
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
            pin = geoLoc.split(",") match {
              case Array(lat, lon) =>
                Pin(lat.toDouble, lon.toDouble)
            }
          )
      }
    locations
  }

  def close(): Unit = {
    client.close()
  }
}