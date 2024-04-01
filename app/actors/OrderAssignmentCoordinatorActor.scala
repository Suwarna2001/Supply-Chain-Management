package actors

import akka.actor.Actor
import akka.stream.Materializer
import com.google.inject.Inject
import models.{DatabaseProvider, FinalData, FinalDataTable, OrderDetails, SupplierDetails}
import slick.lifted.TableQuery
import slick.jdbc.PostgresProfile.api._


class OrderAssignmentCoordinatorActor @Inject()(implicit val materializer: Materializer, val db: DatabaseProvider) extends Actor {

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
        // Combine and process order and supplier details
        val processedData = process(order, supplier)

        // Store final data into PostgreSQL
        storeDataToPostgreSQL(processedData)

        // Forward final data to DeliveryActor
        context.actorSelection("/user/deliveryActor") ! processedData

        // Reset order and supplier details
        orderDetails = None
        supplierDetails = None

      case _ =>
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



  def storeDataToPostgreSQL(data: FinalData)(implicit db: DatabaseProvider): Unit ={
    val finalDataQuery = TableQuery[FinalDataTable]

    val insertAction = finalDataQuery += data

    // Execute the insert action using the injected database connection
    db.database.run(insertAction)
  }
}


