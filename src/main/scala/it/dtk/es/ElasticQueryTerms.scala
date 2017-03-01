package it.dtk.es

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.Indexable
import com.sksamuel.elastic4s.bulk.RichBulkResponse
import com.sksamuel.elastic4s.index.RichIndexResponse
import com.sksamuel.elastic4s.searches.SearchDefinition
import it.dtk.model._
import org.elasticsearch.search.sort.SortOrder
import org.json4s.{NoTypeHints, _}
import org.json4s.ext.JodaTimeSerializers
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write

import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by fabiofumarola on 27/02/16.
 */
class ElasticQueryTerms(hosts: String, indexType: String, docType: String, clusterName: String) {
  implicit val formats = Serialization.formats(NoTypeHints) ++ JodaTimeSerializers.all

  val client = ESUtil.elasticClient(hosts, clusterName)


  implicit object QueryTermIndexable extends Indexable[QueryTerm] {
    override def json(t: QueryTerm): String = write(t)
  }

  def createOrUpdate(q: QueryTerm)(implicit ex: ExecutionContext): Future[RichIndexResponse] = {
    val req = indexInto(indexType, docType) id q.terms.mkString("_") source q
    client.execute(req)
  }

  def bulkCreateUpdate(seq: Seq[QueryTerm])(implicit ex: ExecutionContext): Future[RichBulkResponse] = {
    val req = seq.map(q => indexInto(indexType, docType) id q.terms.mkString("_") source q)
    client.execute(bulk(req))
  }

  def queryTermsSortedDesc(): SearchDefinition = {
    search(indexType,docType) query matchAllQuery sort {
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
  def listQueryTerms(fromIndex: Int = 0, sizeRes: Int = 10)(implicit ex: ExecutionContext): Future[Seq[QueryTerm]] = {
    val req = search(indexType,docType) query matchAllQuery from fromIndex size sizeRes
    client.execute(req).map(_.hits.map(h => parse(h.sourceAsString).extract[QueryTerm]))
  }

  def close(): Unit = {
    client.close()
  }
}
