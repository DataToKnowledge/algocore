package it.dtk.es

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.Indexable
import com.sksamuel.elastic4s.bulk.RichBulkResponse
import com.sksamuel.elastic4s.index.RichIndexResponse
import com.sksamuel.elastic4s.searches.SearchDefinition
import it.dtk.model.GoogleNews
import org.elasticsearch.search.sort.SortOrder
import org.json4s.NoTypeHints
import org.json4s.ext.JodaTimeSerializers
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._

import scala.concurrent.{ ExecutionContext, Future }

/**
 * Created by fabiofumarola on 30/04/16.
 */
class ElasticGoogleNews(hosts: String, indexType: String, docType: String, clusterName: String) {
  implicit val formats = Serialization.formats(NoTypeHints) ++ JodaTimeSerializers.all

  val client = ESUtil.elasticClient(hosts, clusterName)

  //  implicit object GoogleNewsHitAs extends HitAs[GoogleNews] {
  //    override def as(hit: RichSearchHit): GoogleNews = {
  //      parse(hit.getSourceAsString).extract[GoogleNews]
  //    }
  //  }

  implicit object GoogleNewsIndexable extends Indexable[GoogleNews] {
    override def json(t: GoogleNews): String = write(t)
  }

  def createOrUpdate(q: GoogleNews)(implicit ex: ExecutionContext): Future[RichIndexResponse] = {
    val lower = q.search.toLowerCase
    val req = indexInto(indexType, docType) id lower.replace(" ", "_") source q
    client.execute(req)
  }

  def bulkCreateUpdate(seq: Seq[GoogleNews])(implicit ex: ExecutionContext): Future[RichBulkResponse] = {
    val req = seq.map(q => indexInto(indexType, docType) id q.search.toLowerCase.replace(" ", "_") source q)
    client.execute(bulk(req))
  }

  def googleNewsSortedDesc(): SearchDefinition = {
    search(indexType, docType) query matchAllQuery sort {
      fieldSort("timestamp") order SortOrder.DESC
    }
  }

  /**
   *
   * @param fromIndex
   * @param sizeRes
   * @param ex
   * @return a future of query term
   *         call listQueryTerm(...).await to get results
   */
  def listGoogleNews(fromIndex: Int = 0, sizeRes: Int = 10)(implicit ex: ExecutionContext): Future[Seq[GoogleNews]] = {
    val req = search(indexType, docType) query matchAllQuery from fromIndex size sizeRes
    client.execute(req).map(_.hits.map(e => parse(e.sourceAsString).extract[GoogleNews]))
  }

  def close(): Unit = {
    client.close()
  }
}
