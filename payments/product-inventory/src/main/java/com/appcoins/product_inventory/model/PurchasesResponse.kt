package com.appcoins.product_inventory.model

import com.appcoins.payments.json.Json

@Json
data class PurchasesResponse(val items: List<PurchaseResponse>)

@Json
data class PurchaseResponse(
  val uid: String,
  val sku: String,
  val state: PurchaseStateResponse,
  @Json("order_uid")
  val orderUid: String,
  val payload: String?,
  val created: String,
  val verification: Verification,
)

@Json
data class Verification(
  val type: String,
  val data: String,
  val signature: String,
)

@Json
enum class PurchaseStateResponse {
  PENDING, //The subscription purchase is pending acknowledgement.
  ACKNOWLEDGED, //The subscription purchase has been acknowledged.
  CONSUMED //The subscription purchase has been consumed.
}
