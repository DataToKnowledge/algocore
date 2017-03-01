package it.dtk.es

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.Indexable
import com.sksamuel.elastic4s.index.RichIndexResponse
import it.dtk.model.Feed
import org.json4s._
import org.json4s.ext.JodaTimeSerializers
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write

import scala.concurrent.{ ExecutionContext, Future }

/**
 * Created by fabiofumarola on 27/02/16.
 */
class ElasticFeeds(hosts: String, indexType: String, docType: String, clusterName: String) {
  implicit val formats = Serialization.formats(NoTypeHints) ++ JodaTimeSerializers.all

  val client = ESUtil.elasticClient(hosts, clusterName)

  implicit object FeedIndexable extends Indexable[Feed] {
    override def json(t: Feed): String = write(t)
  }

  def listFeedsFuture(fromIndex: Int = 0, sizeRes: Int = 10)(implicit ex: ExecutionContext): Future[Seq[Feed]] = {
    val req = search(indexType, docType) query matchAllQuery from fromIndex size sizeRes
    client.execute(req).map(_.hits.map(r => parse(r.sourceAsString).extract[Feed]))
  }

  def listFeeds(fromIndex: Int = 0, sizeRes: Int = 10)(implicit ex: ExecutionContext): Seq[Feed] =
    listFeedsFuture(fromIndex, sizeRes).await

  def createOrUpdate(feed: Feed)(implicit ex: ExecutionContext): Future[RichIndexResponse] = {
    client.execute(
      indexInto(indexType, docType) id feed.url source feed
    )
  }

  def close(): Unit = {
    client.close()
  }
}
