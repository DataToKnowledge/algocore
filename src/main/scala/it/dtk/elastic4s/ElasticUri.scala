package it.dtk.elastic4s

/**
  * Created by fabiofumarola on 10/02/16.
  */
object ElasticUri {

  private val prefix = "elasticsearch://"

  def apply(str: String): ElasticUri = {
    require(str.trim.nonEmpty && !str.startsWith("elasticsearch://"),
      "Invalid uri, must be in format host:port,host:port,...")

    val hosts = str.split(",")

    val list = hosts
      .map(_.split(":"))
      .filter(_.size == 2)
      .map(a => (a(0), a(1).toInt)).toList

    if (list.isEmpty)
      throw new IllegalArgumentException("Invalid uri, must be in format host:port,host:port,...")

    ElasticUri(str, list)
  }
}

case class ElasticUri(uri: String, hosts: List[(String, Int)])
