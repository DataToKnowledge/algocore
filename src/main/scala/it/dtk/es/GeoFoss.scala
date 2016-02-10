package it.dtk.es

import com.typesafe.config.{Config, ConfigFactory}
import net.ceedubs.ficus.Ficus._


/**
  * Created by fabiofumarola on 08/02/16.
  */
class GeoFoss(config: Config) {

  private val conf = config.getConfig("algocore.elasticsearch")

  private val esHost = conf.as[String]("host")
  private val esPort = conf.as[Int]("port")
  private val docPath = conf.as[String]("wheretolive.addresses")
  private val cluster = conf.as[String]("clusterName")

  def loadInitialData(): Unit = {

  }


}
