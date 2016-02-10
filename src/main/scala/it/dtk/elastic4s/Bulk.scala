package it.dtk.elastic4s

import org.elasticsearch.action.bulk.{BulkResponse, BulkRequest, BulkProcessor}
import org.elasticsearch.action.bulk.BulkProcessor.Listener
import org.elasticsearch.common.unit.{TimeValue, ByteSizeValue}

/**
  * Created by fabiofumarola on 10/02/16.
  */
object Bulk {

//  def apply(client: ElasticClient,
//            listener: Listener,
//            bulkActions: Int,
//            byteSize: ByteSizeValue,
//            flushInterval: TimeValue,
//            concurrentReqs: Int = 1) = BulkProcessor
//    .builder(client.client, listener)
//    .setBulkActions(bulkActions)
//    .setBulkSize(size)
//    .setFlushInterval()

}
