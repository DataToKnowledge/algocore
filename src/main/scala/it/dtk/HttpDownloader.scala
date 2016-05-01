package it.dtk

import java.io.File

import com.ning.http.client.AsyncHttpClientConfig.Builder
import play.api.libs.ws.WSResponse
import play.api.libs.ws.ning.NingWSClient

import scala.concurrent.duration.{ FiniteDuration, _ }
import scala.concurrent.{ Await, Future }
import scala.language.postfixOps
import scala.util.Try

/**
 * Created by fabiofumarola on 31/01/16.
 */
object HttpDownloader {
  //check for configurations https://www.playframework.com/documentation/2.4.x/ScalaWS
  private val builder = new Builder().
    setFollowRedirect(true).
    setUserAgent("www.wheretolive.it").
    setAcceptAnyCertificate(true)

  val ws = new NingWSClient(builder.build())

  implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext

  def doGet(url: String): Future[WSResponse] = {
    //work around for issue https://github.com/DataToKnowledge/spark-jobs/issues/16
    val u = url.replace("https", "http")
    ws.url(u).withFollowRedirects(true).get()
  }

  /**
   *
   * @param url the url to retrieve
   * @return get a future to the url using Play WS
   */
  def doGetOption(url: String, timeout: FiniteDuration = 10.seconds): Option[WSResponse] =
    try {
      Some(Await.result(doGet(url), timeout))
    } catch {
      case e: Exception =>
        println(s"got error ${e.getMessage} requesting url $url")
        None
    }

  def doPost(url: String, headers: Map[String, String],
    parameters: Map[String, Seq[String]]): Future[WSResponse] = {
    ws.url(url)
      .withFollowRedirects(true)
      .withHeaders(headers.toSeq: _*)
      .post(parameters)
  }

  def wPost(url: String, headers: Map[String, String], parameters: Map[String, Seq[String]],
    timeout: FiniteDuration = 10.seconds): Option[WSResponse] =
    try {
      Some(Await.result(doPost(url, headers, parameters), timeout))
    } catch {
      case e: Exception =>
        e.printStackTrace()
        None
    }

  def doPut(url: String, body: String): Future[WSResponse] = {
    ws.url(url).put(body)
  }

  def doDelete(url: String): Future[WSResponse] = {
    ws.url(url).delete()
  }

  def close() = ws.close()
}
