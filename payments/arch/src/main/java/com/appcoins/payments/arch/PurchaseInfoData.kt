package com.appcoins.payments.arch

import java.util.Date

data class PurchaseInfoData(
  val uid: String,
  val productName: String,
  val state: PurchaseState,
  val autoRenewing: Boolean,
  val renewal: Date?,
  val packageName: String,
  val signatureValue: String,
  val signatureMessage: String,
)

enum class PurchaseState {
  PENDING, //The subscription purchase is pending acknowledgement.
  ACKNOWLEDGED, //The subscription purchase has been acknowledged.
  CONSUMED //The subscription purchase has been consumed.
}
