package it.dtk.es

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s._
import it.dtk.HttpDownloader
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest
import org.elasticsearch.common.io.stream
import org.elasticsearch.common.settings.Settings
import org.json4s._
import org.json4s.ext.JodaTimeSerializers
import org.json4s.jackson.Serialization

import scala.concurrent.ExecutionContext

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

}
