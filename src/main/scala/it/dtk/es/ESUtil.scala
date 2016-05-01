package it.dtk.es

import com.sksamuel.elastic4s._
import org.elasticsearch.common.settings.Settings

/**
 * Created by fabiofumarola on 25/04/16.
 */
object ESUtil {
  def elasticClient(hosts: String, clusterName: String): ElasticClient = {
    val settings: Settings = Settings.settingsBuilder()
      .put("cluster.name", clusterName)
      .put("client.transport.sniff", true).build()

    ElasticClient.transport(settings, ElasticsearchClientUri(s"elasticsearch://$hosts"))
  }
}
