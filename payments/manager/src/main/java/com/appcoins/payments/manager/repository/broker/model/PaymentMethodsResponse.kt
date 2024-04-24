package com.appcoins.payments.manager.repository.broker.model

import com.appcoins.payments.json.Json
import java.math.BigDecimal

@Json
data class PaymentMethodsResponse(
  val next: String?,
  val items: List<PaymentMethodJSON>,
)

@Json
data class PaymentMethodJSON(
  val name: String,
  val label: String,
  val icon: String,
  val status: String,
  val message: String?,
  val gateway: GatewayJSON,
  val async: Boolean,
  val fee: FeeJSON?,
) {
  val isAvailable: Boolean
    get() = this.status != "UNAVAILABLE"
}

@Json
data class GatewayJSON(
  val name: String,
)

@Json
data class FeeJSON(
  val type: FeeType,
  val cost: FeeCostJSON?,
)

@Json
enum class FeeType {
  EXACT,
  UNKNOWN
}

@Json
data class FeeCostJSON(
  val value: BigDecimal,
  val currency: String,
)
