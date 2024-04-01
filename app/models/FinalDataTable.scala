package models
import slick.jdbc.PostgresProfile.api._

class FinalDataTable(tag: Tag) extends Table[FinalData](tag, "final_data") {

  def orderId = column[String]("order_id", O.PrimaryKey)
  def productId = column[String]("product_id")
  def orderState = column[String]("order_state")
  def totalAmount = column[Double]("total_amount")

  def * = (orderId, productId, orderState, totalAmount) <> (FinalData.tupled, FinalData.unapply)
}

