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

import scala.concurrent.{ ExecutionContext, Future }

/**
 * Created by fabiofumarola on 27/02/16.
 */
class ElasticFeeds(hosts: String, indexPath: String, clusterName: String) {
  implicit val formats = Serialization.formats(NoTypeHints) ++ JodaTimeSerializers.all

  private val settings = Settings.settingsBuilder()
    .put("cluster.name", clusterName).build()

  val client = ElasticClient.transport(settings, ElasticsearchClientUri(s"elasticsearch://$hosts"))

  implicit object FeedHitAs extends HitAs[Feed] {
    override def as(hit: RichSearchHit): Feed = {
      parse(hit.getSourceAsString).extract[Feed]
    }
  }

  implicit object FeedIndexable extends Indexable[Feed] {
    override def json(t: Feed): String = write(t)
  }

  def listFeedsFuture(fromIndex: Int = 0, sizeRes: Int = 10)(implicit ex: ExecutionContext): Future[Seq[Feed]] = {
    val req = search in indexPath query matchAllQuery from fromIndex size sizeRes
    client.execute(req).map(_.as[Feed])
  }

  def listFeeds(fromIndex: Int = 0, sizeRes: Int = 10)(implicit ex: ExecutionContext): Seq[Feed] =
    listFeedsFuture(fromIndex, sizeRes).await

  def createOrUpdate(feed: Feed)(implicit ex: ExecutionContext): Future[IndexResult] = {
    val req = index into indexPath id feed.url source feed
    client.execute(req)
  }

  def close(): Unit = {
    client.close()
  }
}
