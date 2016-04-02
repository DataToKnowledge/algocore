package it.dtk.es

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s._
import org.elasticsearch.common.settings.Settings
import org.json4s.JsonAST.JValue
import org.json4s._
import org.json4s.ext.JodaTimeSerializers
import org.json4s.jackson.Serialization
import org.json4s.jackson.JsonMethods._

import scala.concurrent.{ ExecutionContext, Future }

/**
 * Created by fabiofumarola on 16/03/16.
 */
class ElasticArticles(hosts: String, indexPath: String, clusterName: String) {
  implicit val formats = Serialization.formats(NoTypeHints) ++ JodaTimeSerializers.all

  private val settings = Settings.settingsBuilder()
    .put("cluster.name", clusterName).build()

  val client = ElasticClient.transport(settings, ElasticsearchClientUri(s"elasticsearch://$hosts"))

  def indexExists(implicit ex: ExecutionContext): Boolean = client.execute {
    index exists indexPath
  }.await.isExists

  def rawQuery(rawQuery: String)(implicit ex: ExecutionContext): Future[JValue] = {
    val query = search in indexPath rawQuery rawQuery

    client.execute(query)
      .map(r => parse(r.original.toString))
  }

  def rawQuery(jsonQuery: JValue)(implicit ex: ExecutionContext): Future[JValue] = {
    val query = search in indexPath rawQuery compact(render(jsonQuery))

    client.execute(query)
      .map(r => parse(r.original.toString))
  }
}
