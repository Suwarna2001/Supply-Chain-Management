package models

import play.api.libs.json.{Json, Reads}

object JsonFormats {
  implicit val orderDetailsFormat: Reads[OrderDetails] = Json.format[OrderDetails]
  implicit val supplierDetailsFormat: Reads[SupplierDetails] = Json.format[SupplierDetails]
}