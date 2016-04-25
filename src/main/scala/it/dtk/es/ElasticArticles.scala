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

import scala.concurrent.{ ExecutionContext, Future }

/**
 * Created by fabiofumarola on 16/03/16.
 */
class ElasticArticles(hosts: String, searchHost: String, indexPath: String, clusterName: String) {
  implicit val formats = Serialization.formats(NoTypeHints) ++ JodaTimeSerializers.all

  val client = ESUtil.elasticClient(hosts, clusterName)

  def indexExists(implicit ex: ExecutionContext): Boolean = client.execute {
    index exists indexPath
  }.await.isExists

  def rawQuery(data: JValue)(implicit ex: ExecutionContext): Future[JValue] = {
    val url = s"http://$searchHost/$indexPath/_search"
    val json = Json.parse(pretty(render(data)))
    HttpDownloader.ws.url(url).post(json).map { res =>
      parse(res.body, false)
    }
  }

}

