package controllers

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import play.api.i18n.Lang.logger
import play.api.libs.json._
import play.api.mvc._

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class KafkaController @Inject()(cc: ControllerComponents)(implicit system: ActorSystem, ec: ExecutionContext)
  extends AbstractController(cc) {

  // Configuration (load from config file or environment variables)
  private val bootstrapServers = "localhost:9092"
  private val groupId = "kafka-consumer-group"
  private val autoOffsetReset = "earliest"
  private val topic = "deliveryDetailsTopic"

  // Kafka consumer settings
  private val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers(bootstrapServers)
    .withGroupId(groupId)
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset)

  // Kafka consumer source for the specified topic
  private val deliveryDetailsSource = Consumer.plainSource(consumerSettings, Subscriptions.topics(topic))

  // Flag to indicate if the consumer is running
  private var consumerRunning: Boolean = false

  // Start the Kafka consumer (can be called externally)
  def startConsumer(): Unit = {
    if (!consumerRunning) {
      consumerRunning = true
      // Combine messages into a single JsObject for efficiency
      val kafkaMessagesFuture: Future[JsValue] = deliveryDetailsSource
        .map { record => Json.parse(record.value()) }
        .runFold(JsArray()) { (acc, json) => acc.as[JsArray] :+ json }
        .map { jsonArray => Json.toJson(jsonArray) }
        .andThen { case _ => consumerRunning = false } // Mark consumer as stopped on completion
    }
  }

  // Action to fetch data from Kafka and render it on the landing page
  def index: Action[AnyContent] = Action.async { implicit request =>
    // Fetch the future once
    val kafkaMessagesFuture = deliveryDetailsSource
      .map { record => Json.parse(record.value()) }
      .runFold(JsArray()) { (acc, json) => acc.as[JsArray] :+ json }
      .map { jsonArray => Json.toJson(jsonArray) }

    if (consumerRunning) {
      // Consumer is already running, use the existing future
      kafkaMessagesFuture.map { kafkaMessages =>
        Ok(views.html.index()) // Render the view
      }
    } else {
      // Start the consumer and return the future for rendering
      startConsumer()
      kafkaMessagesFuture.map { kafkaMessages =>
        Ok(views.html.index()) // Render the view
      } recover {
        case ex: Throwable =>
          // Handle errors gracefully (log the error, return error message)
          logger.error("Error fetching messages from Kafka", ex)
          InternalServerError("Error retrieving data")
      }
    }
  }
}
