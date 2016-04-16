package it.dtk.es

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s._
import it.dtk.HttpDownloader
import org.elasticsearch.common.settings.Settings
import org.json4s.JsonAST.JValue
import org.json4s._
import org.json4s.ext.JodaTimeSerializers
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by fabiofumarola on 16/03/16.
  */
class ElasticArticles(hosts: String, searchHost: String, indexPath: String, clusterName: String) {
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
      .map(r => parse(r.original.toString, false))
  }

  //  def rawQuery(jsonReq: JValue)(implicit ex: ExecutionContext): Future[JValue] = {
  //    val parsed = ParsedQuery(jsonReq)
  //    val query = search.in(indexPath).rawQuery(parsed.query)
  //
  //    parsed.sources.foreach(s => query.sourceInclude(s: _*))
  //    parsed.from.foreach(query.from)
  //    parsed.size.foreach(query.size)
  //
  //    val sorted = parsed.sort.map(e => fieldSort(e.field).order(e.sort))
  //    query.sort(sorted: _*)
  //
  //    client.execute(query)
  //      .map(r => parse(r.original.toString, false))
  //  }

  def rawQuery(data: JValue)(implicit ex: ExecutionContext): Future[JValue] = {
    val url = s"http://$searchHost/wtl/articles/_search"
    val json = Json.parse(pretty(render(data)))
    HttpDownloader.ws.url(url).post(json).map { res =>
      parse(res.body, false)
    }
  }

}

