package stream

import akka.Done
import akka.actor.{ActorRef, ActorSystem}
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import com.google.inject.{Inject, Singleton}
import com.google.inject.name.Named
import models._
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.StringDeserializer
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class OrderSupplyStreamProcessor @Inject()(system: ActorSystem, @Named("orderAssignmentCoordinatorActor")
val orderAssignmentCoordinatorActor: ActorRef, implicit val ec: ExecutionContext)(implicit val materializer: Materializer) {

  import JsonFormats._

  // Kafka consumer settings
  val consumerSettings: ConsumerSettings[String, String] =
    ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers("localhost:9092")
      .withGroupId("order-supply-group")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  // Kafka consumer source for Order topic
  val orderSource: Source[ConsumerRecord[String, String], Consumer.Control] = Consumer.plainSource(consumerSettings, Subscriptions.topics("Order"))

  // Kafka consumer source for Supply topic
  val supplySource: Source[ConsumerRecord[String, String], Consumer.Control] = Consumer.plainSource(consumerSettings, Subscriptions.topics("Supply"))

  // Define stream processing pipeline for Order topic with non-blocking send (tell)
  val orderPipeline: Future[Done] =
    orderSource
      .map { record =>
        val jsonString = record.value()
        Json.parse(jsonString).as[OrderDetails]
      }
      .runForeach(order => orderAssignmentCoordinatorActor ! order)

  // Define stream processing pipeline for Supply topic with non-blocking send (tell)
  val supplyPipeline: Future[Done] =
    supplySource
      .map { record =>
        val jsonString = record.value()
        Json.parse(jsonString).as[SupplierDetails]
      }
      .runForeach(supplier => orderAssignmentCoordinatorActor ! supplier)

  // Add a method to start processing the streams (optional)
  def startProcessing(): Unit = {
    orderPipeline.onComplete { _ => println("Order pipeline completed") }
    supplyPipeline.onComplete { _ => println("Supply pipeline completed") }
  }

}
