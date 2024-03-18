package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json._
import java.nio.file.Files
import java.nio.file.Paths
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, ProducerConfig}
import java.util.Properties

@Singleton
class OrderController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  // Kafka producer configuration
  private val props = new Properties()
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
  private val producer = new KafkaProducer[String, String](props)

  def uploadJson = Action(parse.multipartFormData) { request =>
    val orderFileOption = request.body.file("orderJsonFile")
    val supplierFileOption = request.body.file("supplierJsonFile")

    (orderFileOption, supplierFileOption) match {
      case (Some(orderFile), Some(supplierFile)) =>
        val orderFilename = orderFile.filename
        val supplierFilename = supplierFile.filename

        val orderFileBytes = Files.readAllBytes(Paths.get(orderFile.ref.path.toAbsolutePath.toString))
        val supplierFileBytes = Files.readAllBytes(Paths.get(supplierFile.ref.path.toAbsolutePath.toString))

        val orderJsonString = new String(orderFileBytes)
        val supplierJsonString = new String(supplierFileBytes)

        // Send data to Kafka topics
        sendToKafka("Order", orderJsonString)
        sendToKafka("Supply", supplierJsonString)

        Ok("JSON files uploaded successfully")

      case _ =>
        BadRequest("Missing JSON file(s)")
    }
  }

  private def sendToKafka(topic: String, data: String): Unit = {
    val record = new ProducerRecord[String, String](topic, data)
    producer.send(record)
  }
}


