import actors._
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.kafka.ConsumerSettings
import akka.stream.Materializer
import com.google.inject.name.{Named, Names}
import com.google.inject.{AbstractModule, Inject, Scopes}
import controllers._
import models._
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import play.api.libs.json._
import slick.jdbc.{JdbcProfile, PostgresProfile}
import stream.OrderSupplyStreamProcessor

import scala.annotation.unused
@unused
class AppModule @Inject()(system: ActorSystem, materializer: Materializer, db: DatabaseProvider) extends AbstractModule {

  override def configure(): Unit = {
    // Bind core dependencies
    bind(classOf[ActorSystem]).asEagerSingleton()
    bind(classOf[Materializer]).asEagerSingleton()
    bind(classOf[DatabaseProvider]).asEagerSingleton()


    val jdbcProfile = PostgresProfile
    bind(classOf[JdbcProfile]).toInstance(jdbcProfile)


    // Bind controllers
    bind(classOf[HomeController]).asEagerSingleton()
    bind(classOf[OrderController]).asEagerSingleton()
    bind(classOf[StreamStarter]).asEagerSingleton()
    bind(classOf[KafkaController]).asEagerSingleton()

    // Bind services
    bind(classOf[OrderSupplyStreamProcessor]).asEagerSingleton()
    // Bind Kafka producer actor as a singleton
      bind(classOf[KafkaProducerActor]).asEagerSingleton()
    bind(classOf[String]).annotatedWith(Names.named("deliveryDetailsTopic")).toInstance("deliveryDetailsTopic")
    // Bind actors with injected dependencies

    // Bind OrderAssignmentCoordinatorActor with injected dependencies
    bind(classOf[OrderAssignmentCoordinatorActor])
      .toProvider(classOf[OrderAssignmentCoordinatorActorProvider])

    import akka.stream.Materializer

    import javax.inject.{Inject, Provider}

    class OrderAssignmentCoordinatorActorProvider @Inject()(
                                                             implicit val materializer: Materializer,
                                                             db: DatabaseProvider // Assuming DatabaseProvider is the correct type
                                                           ) extends Provider[OrderAssignmentCoordinatorActor] {
      override def get(): OrderAssignmentCoordinatorActor = {
        new OrderAssignmentCoordinatorActor()
      }
    }


    class DeliveryActorProvider @Inject()(val materializer: Materializer, val system: ActorRef, val deliveryDetailsTopic: String @Named("deliveryDetailsTopic")) extends Provider[DeliveryActor]  {
      override def get(): DeliveryActor = {
        new DeliveryActor(materializer, system, deliveryDetailsTopic)
      }
    }
    bind(classOf[DeliveryActor]).toProvider(classOf[DeliveryActorProvider])


    // Bind Kafka producer actor with named configuration
    bind(classOf[ActorRef]).annotatedWith(Names.named("kafkaProducer"))
      .toProvider(classOf[KafkaProducerActorProvider])
      .in(Scopes.SINGLETON)
    class KafkaProducerActorProvider @Inject()(system: ActorSystem) extends Provider[ActorRef] {
      override def get(): ActorRef = {
        system.actorOf(Props(classOf[KafkaProducerActor], "deliveryDetailsTopic"))
      }
    }
    // Bind Kafka consumer configuration (replace with actual values if needed)
    val bootstrapServers = "localhost:9092"
    val groupId = "kafka-consumer-group"
    val autoOffsetReset = "earliest"
    val topic = "deliveryDetailsTopic"

    val consumerSettings: ConsumerSettings[String, String] = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers(bootstrapServers)
      .withGroupId(groupId)
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset)


    bind(classOf[ConsumerSettings[String, String]])
      .annotatedWith(Names.named("deliveryDetailsConsumerSettings"))
      .toInstance(consumerSettings)

    // Case class bindings for JSON parsing
    implicit val orderDetailsFormat: Format[OrderDetails] = Json.format[OrderDetails]
    implicit val supplierDetailsFormat: Format[SupplierDetails] = Json.format[SupplierDetails]
    implicit val finalDataFormat: Format[FinalData] = Json.format[FinalData]

    // Bind delivery details topic name for injection
    bind(classOf[String]).annotatedWith(Names.named("deliveryDetailsTopic")).toInstance("deliveryDetailsTopic")
  }
}
