package it.dtk.es

import it.dtk.HttpDownloader
import play.api.libs.ws.WSResponse
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
 * Created by fabiofumarola on 17/03/16.
 */
class ElasticAdmin(adminHost: String, esHosts: String,
    gFossPath: String, clusterName: String) {

  val url = s"http://$adminHost:9200/wtl"

  def deleteIndex(): Boolean = {
    val res = Await.result(HttpDownloader.wDelete(url), 10.seconds)
    isError(res)
  }

  def existIndex(): Boolean = {
    HttpDownloader.wget(url)
      .map(res => !res.body.contains("404"))
      .getOrElse(true)
  }

  def createIndex(): Boolean = {
    val path = this.getClass.getResource("/elastic_mappings.json").getFile
    val res = Await.result(HttpDownloader.wPut(url, path), 10.seconds)
    isError(res)
  }

  def loadGFossData(): Int = {
    val gfoss = new GeoFoss(esHosts, gFossPath, clusterName)
    val res = gfoss.loadInitialData()
    gfoss.close()
    res
  }

  private def isError(res: WSResponse) = !res.body.contains("404")

  def initWhereToLive(): Unit = {

    if (!existIndex()) {
      if (createIndex()) {
        println("Successfully create the main index")
        println(s"load $loadGFossData locations")
        println("finish init WhereTolive Index")
      }
    } else {
      println("the index exists!!! delete it before")
    }
  }
}
