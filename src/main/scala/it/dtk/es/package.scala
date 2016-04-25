package it.dtk

import com.sksamuel.elastic4s.{ElasticsearchClientUri, ElasticClient}
import org.elasticsearch.common.settings.Settings

/**
  * Created by fabiofumarola on 24/04/16.
  */
package object es {

  def elasticClient(hosts: String, clusterName: String) = {
    val settings = Settings.settingsBuilder()
      .put("cluster.name", clusterName).build()
    ElasticClient.transport(settings, ElasticsearchClientUri(s"elasticsearch://$hosts"))
  }
}
