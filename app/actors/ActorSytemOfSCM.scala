import akka.actor._
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.Materializer
import com.google.inject.AbstractModule
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import play.api._
import play.api.inject.guice.{GuiceApplicationBuilder, GuiceApplicationLoader}
import play.api.libs.json._
import play.api.mvc._
import slick.jdbc.PostgresProfile.api._

import javax.inject._

// Define case classes for order and supplier details
case class OrderDetails(orderId: String, productId: String, amount: Double)
case class SupplierDetails(productId: String, inventory: Int, productCost: Double, deliveryCharge: Double)
// Define case class for the final processed data
case class FinalData(orderId: String, productId: String, orderState: String, totalAmount: Double)

object JsonFormats {
  implicit val orderDetailsFormat: Reads[OrderDetails] = Json.format[OrderDetails]
  implicit val supplierDetailsFormat: Reads[SupplierDetails] = Json.format[SupplierDetails]
}

// Define Slick model for the final data table
class FinalDataTable(tag: Tag) extends Table[FinalData](tag, "final_data") {
  def orderId = column[String]("order_id", O.PrimaryKey)
  def productId = column[String]("product_id")
  def orderState = column[String]("order_state")
  def totalAmount = column[Double]("total_amount")

  def * = (orderId, productId, orderState, totalAmount) <> (FinalData.tupled, FinalData.unapply)
}

object ActorSytemOfSCM extends App {

  import JsonFormats._

  implicit val system: ActorSystem = ActorSystem("OrderSupplySystem")
  implicit val materializer: Materializer = Materializer(system)
  //--just trying
  class MyModule extends AbstractModule {
    override def configure(): Unit = {
      bind(classOf[ActorSystem]).toInstance(system) // No need to create here, use implicit system
      bind(classOf[Materializer]).toInstance(materializer) // No need to create here, use implicit materializer
    }
  }
  class MyApplicationLoader extends GuiceApplicationLoader {
    override def builder(context: ApplicationLoader.Context): GuiceApplicationBuilder = {
      initialBuilder
        .in(context.environment)
        .loadConfig(context.initialConfiguration)
        .overrides(overrides(context): _*)
        .bindings(new MyModule) // No arguments needed, use implicit values
    }
  }

  object MyApplicationLoader {
    def main(args: Array[String]): Unit = {
      val loader = new MyApplicationLoader()
      val context = ApplicationLoader.Context.create(new Environment(new java.io.File("."), ApplicationLoader.getClass.getClassLoader, play.api.Mode.Dev))

      val app = loader.load(context)
      //ApplicationLoader.createContext(app)
    }
  }
  //--
  // Create ManufactureActor
  val manufactureActor = system.actorOf(Props(classOf[ManufactureActor]), "manufactureActor")

  // Create AssignmentActor
  val assignmentActor = system.actorOf(Props(classOf[AssignmentActor]), "assignmentActor")

  // Create OrderAssignmentCoordinatorActor
  val orderAssignmentCoordinatorActor = system.actorOf(Props(classOf[OrderAssignmentCoordinatorActor]), "orderAssignmentCoordinatorActor")

  // Create DeliveryActor
  val deliveryActor = system.actorOf(Props(classOf[DeliveryActor]), "deliveryActor")

  // Kafka consumer settings
  val consumerSettings =
    ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers("localhost:9092")
      .withGroupId("order-supply-group")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  import akka.actor.ActorSystem
  import akka.stream.{ActorMaterializer, Materializer}


  // Kafka consumer source for Order topic
  val orderSource = Consumer.plainSource(consumerSettings, Subscriptions.topics("Order"))

  // Kafka consumer source for Supply topic
  val supplySource = Consumer.plainSource(consumerSettings, Subscriptions.topics("Supply"))

  //implicit val materializer: Materializer = ActorMaterializer() defined earlier so no need
  // Define stream processing pipeline for Order topic
  val orderPipeline =
    orderSource
      .map { record =>
        val jsonString = record.value()
        Json.parse(jsonString).as[OrderDetails]
      }
      .runForeach(order => manufactureActor ! order)

  // Define stream processing pipeline for Supply topic
  val supplyPipeline =
    supplySource
      .map { record =>
        val jsonString = record.value()
        Json.parse(jsonString).as[SupplierDetails]
      }
      .runForeach(supplier => assignmentActor ! supplier)



  // Initialize Slick database
  val db = Database.forURL("jdbc:postgresql://localhost:5432/mydatabase", "myuser",
    "mypassword", driver = "org.postgresql.Driver")

  // Define Slick table query for final data
  val finalDataQuery = TableQuery[FinalDataTable]

  // Create table if not exists
  val setupAction = finalDataQuery.schema.createIfNotExists

  // Execute the setup action
  db.run(setupAction)

  // Shutdown hook to close the database connection
  sys.addShutdownHook {
    db.close()
  }
}

class ManufactureActor @Inject()(implicit val materializer: Materializer) extends Actor {
  def receive: Receive = {
    case order: OrderDetails =>
      // Forward unprocessed order data to OrderAssignmentCoordinatorActor
      context.actorSelection("/user/orderAssignmentCoordinatorActor") ! order
  }
}

class AssignmentActor @Inject()(implicit val materializer: Materializer) extends Actor {
  def receive: Receive = {
    case supplier: SupplierDetails =>
      // Forward supplier details to OrderAssignmentCoordinatorActor
      context.actorSelection("/user/orderAssignmentCoordinatorActor") ! supplier
  }
}

class OrderAssignmentCoordinatorActor @Inject()(implicit val materializer: Materializer) extends Actor {
  // Store received order and supplier details
  var orderDetails: Option[OrderDetails] = None
  var supplierDetails: Option[SupplierDetails] = None

  def receive: Receive = {
    case order: OrderDetails =>
      orderDetails = Some(order)
      processOrderAndSupplierDetails()

    case supplier: SupplierDetails =>
      supplierDetails = Some(supplier)
      processOrderAndSupplierDetails()
  }

  def processOrderAndSupplierDetails(): Unit = {
    (orderDetails, supplierDetails) match {
      case (Some(order), Some(supplier)) =>
        // Combine and process order and supplier details[TODO]
        val processedData = process(order, supplier)

        // Store final data into PostgreSQL
        storeDataToPostgreSQL(processedData)

        // Forward final data to DeliveryActor
        context.actorSelection("/user/deliveryActor") ! processedData

        // Reset order and supplier details
        orderDetails = None
        supplierDetails = None

      case _ =>
        // Wait for both order and supplier details to be received
        println("Waiting for order and supplier details...")
    }
  }

  def process(order: OrderDetails, supplier: SupplierDetails): FinalData = {
    if (order.productId == supplier.productId) {
      val orderState = if (supplier.inventory < 1000) "delivered" else "declined"
      val totalAmount = if (supplier.inventory < 1000) supplier.productCost + supplier.deliveryCharge else 0.0

      FinalData(order.orderId, order.productId, orderState, totalAmount)
    } else {
      // Handle error case
      FinalData(order.orderId, order.productId, "error", 0.0)
    }
  }

  def storeDataToPostgreSQL(data: FinalData): Unit = {
    // Store final data into PostgreSQL table using Slick
    val db = Database.forConfig("postgres")
    val finalDataQuery = TableQuery[FinalDataTable]

    val insertAction = finalDataQuery += data

    // Execute the insert action
    db.run(insertAction)

    // Close the database connection
    db.close()
  }
}

class DeliveryActor @Inject()(implicit val materializer: Materializer) extends Actor {
  // Kafka producer setup
  val kafkaProducer = context.actorOf(Props(classOf[KafkaProducerActor]), "kafkaProducer")

  def receive: Receive = {
    case data: FinalData =>
      // Send final data to Kafka topic "Delivery_Details"
      kafkaProducer ! data
  }
}

class KafkaProducerActor @Inject()(implicit val materializer: Materializer) extends Actor {
  // Simulated Kafka producer
  def receive: Receive = {
    case data: FinalData =>
      // Simulated operation to send data to Kafka
      println(s"Data sent to Kafka: $data")
  }
}





