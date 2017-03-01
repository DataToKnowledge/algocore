package it.dtk.es

import it.dtk.HttpDownloader
import play.api.libs.ws.WSResponse
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.io.Source

/**
 * Created by fabiofumarola on 17/03/16.
 */
class ElasticAdmin(adminHost: String, esHosts: String,
    gFossIndexType: String, gFossDocType: String, clusterName: String) {

  val urlWtl = s"http://$adminHost:9200/wtl1"
  val urlArticles = s"http://$adminHost:9200/news1"

  def deleteIndex(): Boolean = {
    val res = Await.result(HttpDownloader.doDelete(urlArticles), 10.seconds)
    isError(res)
  }

  def existIndex(url: String): Boolean = {
    HttpDownloader.doGetOption(url).forall(res => !res.body.contains("404"))
  }

  def createWtlIndex(): Boolean = {
    val in = this.getClass.getResourceAsStream("/wtl_mappings.json")
    val body = Source.fromInputStream(in).mkString
    val res = Await.result(HttpDownloader.doPut(urlWtl, body), 10.seconds)
    in.close()
    isError(res)
  }

  def createNewsIndex(): Boolean = {
    val in = this.getClass.getResourceAsStream("/articles_mappings.json")
    val body = Source.fromInputStream(in).mkString
    val res = Await.result(HttpDownloader.doPut(urlArticles, body), 10.seconds)
    println(res.body)
    in.close()
    isError(res)
  }

  def loadGFossData(): Int = {
    val gfoss = new GeoFoss(esHosts, gFossIndexType, gFossDocType, clusterName)
    val res = gfoss.loadInitialData()
    gfoss.close()
    res
  }

  private def isError(res: WSResponse) = !res.body.contains("error")

  def initWhereToLive(): Unit = {

    if (!existIndex(urlWtl)) {
      if (createWtlIndex()) {
        println("Successfully created the main index")
        println(s"load ${loadGFossData()} locations")
      }
      if (createNewsIndex()) {
        println("Successfully created the articles index")
      }
    } else {
      println("the index exists!!! delete it before")
    }
    println("finish init WhereTolive Index")
  }
}
