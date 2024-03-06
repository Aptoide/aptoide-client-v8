package com.appcoins.product_inventory.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class PurchasesResponse(val items: List<PurchaseResponse>)

@Keep
data class PurchaseResponse(
  val uid: String,
  val sku: String,
  val state: PurchaseStateResponse,
  @SerializedName("order_uid")
  val orderUid: String,
  val payload: String?,
  val created: String,
  val verification: Verification,
)

@Keep
data class Verification(
  val type: String,
  val data: String,
  val signature: String,
)

@Keep
enum class PurchaseStateResponse {
  PENDING, //The subscription purchase is pending acknowledgement.
  ACKNOWLEDGED, //The subscription purchase has been acknowledged.
  CONSUMED //The subscription purchase has been consumed.
}
