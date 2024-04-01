package actors

import akka.actor.{Actor, ActorRef, Props}
import akka.stream.Materializer
import com.google.inject.Inject
import models.FinalData

class DeliveryActor @Inject()(val materializer: Materializer, val system: ActorRef, val deliveryDetailsTopic: String) extends Actor {
  // Kafka producer setup
  val kafkaProducer = context.actorOf(Props(classOf[KafkaProducerActor]), "kafkaProducer")

  def receive: Receive = {
    case data: FinalData =>
      // Send final data to Kafka topic "Delivery_Details"
      kafkaProducer ! data
  }
}
