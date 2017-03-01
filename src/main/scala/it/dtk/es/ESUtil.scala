package it.dtk.es

import com.sksamuel.elastic4s.{ ElasticsearchClientUri, TcpClient }
import org.elasticsearch.common.settings.Settings

/**
 * Created by fabiofumarola on 25/04/16.
 */
object ESUtil {
  def elasticClient(hosts: String, clusterName: String) = {
    val settings: Settings = Settings.builder()
      .put("cluster.name", clusterName).build()
    println(s"connecting to $hosts")
    TcpClient.transport(settings, ElasticsearchClientUri(s"elasticsearch://$hosts"))
  }
}
