package it.dtk.kafka

import java.util.Properties
import java.util.concurrent.Future

import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecords, KafkaConsumer}
import org.apache.kafka.clients.producer.{RecordMetadata, ProducerRecord, KafkaProducer}
import org.apache.kafka.common.TopicPartition
import collection.JavaConversions._
import scala.collection.mutable

/**
  *
  * @param brokers     This list should be in the form host1:port1,host2:port2
  * @param topic       name of the topic for the quee
  * @param clientId
  * @param keySer      Serializer class for key that implements the Serializer interface.
  *                    org.apache.kafka.common.serialization.StringSerializer or org.apache.kafka.common.serialization.ByteArraySerializer
  * @param valueSer    Serializer class for value that implements the Serializer interface.
  *                    org.apache.kafka.common.serialization.StringSerializer or org.apache.kafka.common.serialization.B
  * @param ack         "0" -> will not wait, "1" -> wait for one partition ack, "all" wait for all
  * @param compression none, gzip, snappy
  * @param retries
  */
case class ProducerProperties(
                               brokers: String,
                               topic: String,
                               clientId: String,
                               keySer: String = "org.apache.kafka.common.serialization.ByteArraySerializer",
                               valueSer: String = "org.apache.kafka.common.serialization.ByteArraySerializer",
                               ack: String = "1",
                               compression: String = "None",
                               retries: Int = 1,
                               batchSize: Int = 16384
                             ) {
  def props(): Properties = {
    val props = new Properties()
    props.put("bootstrap.servers", brokers)
    props.put("client.id", clientId + "Prod")
    props.put("key.serializer", keySer)
    props.put("value.serializer", valueSer)
    //    props.put("linger.ms", 1.toString)
    props.put("compression.type", "snappy")
    //    props.put("retries", retries.toString)
    //    props.put("batch.size", batchSize.toString)
    props
  }
}

/**
  * Created by fabiofumarola on 25/02/16.
  */
class KafkaWriter[K, V](val prodPros: ProducerProperties) {
  val producer = new KafkaProducer[K, V](prodPros.props())

  def send(key: K, value: V): Future[RecordMetadata] = {
    val msg = new ProducerRecord[K, V](prodPros.topic, key, value)
    producer.send(msg)
  }

  def close(): Unit = {
    producer.close()
  }
}

object KafkaWriter {

  private val pool = mutable.Map.empty[ProducerProperties, KafkaWriter[Array[Byte], Array[Byte]]]

  def getConnection(props: ProducerProperties) = {
    pool.getOrElseUpdate(props, new KafkaWriter[Array[Byte], Array[Byte]](props))
  }

  override def finalize(): Unit = {
    pool.values.foreach(_.close())
  }
}

/**
  *
  * @param brokers   This list should be in the form host1:port1,host2:port2,
  * @param topics    list of topics comma separated
  * @param groupName A unique string that identifies the Connect cluster group this worker belongs to.
  */
case class ConsumerProperties(
                               brokers: String,
                               topics: String,
                               groupName: String,
                               keyDes: String = "org.apache.kafka.common.serialization.ByteArrayDeserializer",
                               valueDes: String = "org.apache.kafka.common.serialization.ByteArrayDeserializer"
                             ) {
  def props(): Properties = {
    val props = new Properties()
    props.put("bootstrap.servers", brokers)
    props.put("group.id", groupName + "Cons")
    props.put("enable.auto.commit", "true")
    props.put("auto.commit.interval.ms", "1000")
    props.put("session.timeout.ms", "30000")
    props.put("key.deserializer", keyDes)
    props.put("value.deserializer", valueDes)
    props
  }
}

class KafkaReader[K, V](val consProps: ConsumerProperties) {
  val consumer = new KafkaConsumer[K, V](consProps.props())
  consumer.subscribe(consProps.topics.split(",").toList)

  /**
    *
    * @return
    */
  def consume(timeout: Long = 100): ConsumerRecords[K, V] = {
    consumer.poll(timeout)
  }

  override def finalize(): Unit = {
    consumer.close()
  }
}


object Main extends App {

  //  ConsumerConfig.main(Array.empty)

  val consProps = ConsumerProperties(
    brokers = "192.168.99.100:9092",
    topics = "feed_items",
    groupName = "feed_items"
  )

  val reader = new KafkaReader[Array[Byte], Array[Byte]](consProps)
  val cons = reader.consumer

  val ass = cons.assignment()

  println(ass)

  val topics = cons.listTopics()

  println(topics)

  println(cons.assignment())


  //  val res = cons.poll(1)

  //  if (res.count() == 0){
//  cons.seekToBeginning(new TopicPartition(consProps.topics, 0))
  //  }

  while (true) {
    val res = reader.consume()
    res.foreach{ r =>
      println(new String(r.key()))
      print(new String(r.value()))
    }
  }

  reader.consumer.close()
}