package it.dtk.es

import com.sksamuel.elastic4s.source.Indexable
import com.sksamuel.elastic4s._
import org.elasticsearch.common.settings.Settings
import org.json4s.NoTypeHints
import it.dtk.model._
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write
import com.sksamuel.elastic4s.ElasticDsl._
import scala.util._
import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by fabiofumarola on 27/02/16.
  */
class ElasticQueryTerms(hosts: String, indexPath: String, clusterName: String) {
  implicit val formats = Serialization.formats(NoTypeHints)

  private val settings = Settings.settingsBuilder()
    .put("cluster.name", clusterName).build()

  val client = ElasticClient.transport(settings, ElasticsearchClientUri(s"elasticsearch://$hosts"))

  implicit object QueryTermsHitAs extends HitAs[QueryTerm] {
    override def as(hit: RichSearchHit): QueryTerm = {
      parse(hit.getSourceAsString).extract[QueryTerm]
    }
  }

  implicit object QueryTermIndexable extends Indexable[QueryTerm] {
    override def json(t: QueryTerm): String = write(t)
  }

  def createOrUpdate(q: QueryTerm)(implicit ex: ExecutionContext): Future[IndexResult] = {
    val req = index into indexPath id q.terms.mkString("_") source q
    client.execute(req)
  }

  def bulkCreateUpdate(seq: Seq[QueryTerm])(implicit ex: ExecutionContext): Future[BulkResult] = {
    val req = seq.map(q => index into indexPath id q.terms.mkString("_") source q)
    client.execute(bulk(req))
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
    val req = search in indexPath query matchAllQuery from fromIndex size sizeRes
    client.execute(req).map(_.as[QueryTerm])
  }

  def close(): Unit = {
    client.close()
  }
}

object ElasticQueryTerms {

  private var pool: Option[ElasticQueryTerms] = None

  def connection(hosts: String, indexPath: String, clusterName: String): ElasticQueryTerms = {
    if (pool.isEmpty) {
      pool = Some(new ElasticQueryTerms(hosts, indexPath, clusterName))
    }
    pool.get
  }

  override def finalize(): Unit = {
    pool.foreach(_.close())
  }
}