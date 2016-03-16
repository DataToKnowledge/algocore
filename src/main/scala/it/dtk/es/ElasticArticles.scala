package it.dtk.es

import com.sksamuel.elastic4s.source.Indexable
import com.sksamuel.elastic4s._
import it.dtk.model.Feed
import org.elasticsearch.common.settings.Settings
import org.json4s._
import org.json4s.ext.JodaTimeSerializers
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write
import com.sksamuel.elastic4s.ElasticDsl._


/**
  * Created by fabiofumarola on 16/03/16.
  */
class ElasticArticles(hosts: String, indexPath: String, clusterName: String) {
  implicit val formats = Serialization.formats(NoTypeHints) ++ JodaTimeSerializers.all

  private val settings = Settings.settingsBuilder()
    .put("cluster.name", clusterName).build()

  val client = ElasticClient.transport(settings, ElasticsearchClientUri(s"elasticsearch://$hosts"))


  def createIndex(): Unit = {
    val index = create index "wtl" mappings(
        mapping("articles").fields(
          stringField("uri")
        )
      )

  }
}
