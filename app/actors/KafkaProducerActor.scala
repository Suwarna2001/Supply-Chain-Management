package actors

import akka.actor.{Actor, ActorRef}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import play.api.i18n.Lang.logger
import play.api.libs.json.JsValue

import java.util.Properties

class KafkaProducerActor(val deliveryDetailsTopic: String)(implicit val system: ActorRef) extends Actor {

  private val producerConfig = new Properties()
  producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
  producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
  private val kafkaProducer = new KafkaProducer[String, String](producerConfig)

  def receive: Receive = {
    case data: JsValue =>
      try {
        val message = data.toString() // Convert JsValue to String
        val record = new ProducerRecord[String, String](deliveryDetailsTopic, message)
        kafkaProducer.send(record)
        println(s"Data sent to Kafka: $message")
      }catch{
        case ex: Throwable => logger.error("Error sending message to Kafka", ex)
      }
  }

  override def postStop(): Unit = {
    super.postStop()
    kafkaProducer.close() // Close producer on actor stop
  }
}
