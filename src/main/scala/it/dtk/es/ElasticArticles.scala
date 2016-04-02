package it.dtk.es

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s._
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.search.sort.SortOrder
import org.json4s.JsonAST.JValue
import org.json4s._
import org.json4s.ext.JodaTimeSerializers
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by fabiofumarola on 16/03/16.
  */
class ElasticArticles(hosts: String, indexPath: String, clusterName: String) {
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

  def rawQuery(jsonReq: JValue)(implicit ex: ExecutionContext): Future[JValue] = {
    val q = jsonReq \ "query"
    val src = (jsonReq \ "_source").extractOpt[List[String]]
    val from = (jsonReq \ "from").extractOpt[Int]
    val size = (jsonReq \ "size").extractOpt[Int]
    val sort = jsonReq \ "sort"
    val aggs = jsonReq \ "aggs"

    var query = search.in(indexPath).rawQuery(compact(render(q)))

    src.foreach(s => query.sourceInclude(s: _*))
    from.foreach(query.from)
    size.foreach(query.size)

    client.execute(query)
      .map(r => parse(r.original.toString, false))
  }

  def rawQuery2(jsonReq: JValue)(implicit ex: ExecutionContext): Future[JValue] = {
    val q = jsonReq \ "query"
    val src = (jsonReq \ "_source").extractOpt[List[String]]
    val from = (jsonReq \ "from").extractOpt[Int]
    val size = (jsonReq \ "size").extractOpt[Int]
    val sort = jsonReq \ "sort"
    val aggs = jsonReq \ "aggs"


    var query = search.in(indexPath).rawQuery(compact(render(q)))

    src.foreach(s => query.sourceInclude(s: _*))
    from.foreach(query.from)
    size.foreach(query.size)
    query.sort(fieldSort("").order(SortOrder.valueOf("asc")))

    ???

    //    val r: WrapperQueryBuilder = new WrapperQueryBuilder(compact(render(jsonReq)))
    //
    //
    //
    //    val query = search in indexPath rawQuery compact(render(jsonReq))
    //    client.execute(query)
    //      .map(r => parse(r.original.toString, false))
  }

  def addSorts(jArray: JArray) = {

  }
}
