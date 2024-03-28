package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json._
import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class KafkaController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  implicit val system: ActorSystem = ActorSystem("KafkaConsumerSystem")

  // Kafka consumer settings
  val consumerSettings =
    ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers("localhost:9092")
      .withGroupId("kafka-consumer-group")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  // Kafka consumer source for Delivery_Details topic
  val deliveryDetailsSource = Consumer.plainSource(consumerSettings, Subscriptions.topics("Delivery_Details"))

  // Action to fetch data from Kafka and render it on the landing page
  def index = Action.async { implicit request =>
    // Fetch messages from Kafka
    val kafkaMessagesFuture: Future[String] = deliveryDetailsSource
      .map { record =>
        val jsonString = record.value()
        Json.parse(jsonString).toString // Convert JsValue to String
      }
      .runFold("")((acc, str) => if (acc.isEmpty) str else acc + "\n" + str) // Join all Strings with newline characters

    kafkaMessagesFuture.map { kafkaMessages =>
      // Pass Kafka messages to the view
      Ok(views.html.index(kafkaMessages))
    }
  }
}

