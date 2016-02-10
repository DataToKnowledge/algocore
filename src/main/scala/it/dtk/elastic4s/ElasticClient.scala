package it.dtk.elastic4s

import java.net.InetSocketAddress

import org.elasticsearch.action.ActionRequest
import org.elasticsearch.action.index.{IndexRequestBuilder, IndexRequest}
import org.elasticsearch.action.support.replication.ReplicationRequest
import org.elasticsearch.client.Client
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import scala.util.Try


object ElasticClient {

  def transport(settings: Settings, uri: ElasticUri): ElasticClient = {
    val client = TransportClient.builder().settings(settings).build()

    uri.hosts.foreach {
      case (host, port) => client.addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(host, port)))
    }

    new ElasticClient(client)
  }
}


class ElasticClient(val client: Client) {


  def bulk(reqs: List[IndexRequestBuilder]) = {
    val bulk = client.prepareBulk()
    reqs.foreach(r => bulk.add(r))
    bulk.get()
  }


  def index(index: String, `type`: String, json: String): IndexRequestBuilder =
    client.prepareIndex(index, `type`).setSource(json)


  def close(): Unit = {
    Try(client.close())
  }

  override def finalize(): Unit = {
    close()
  }
}
