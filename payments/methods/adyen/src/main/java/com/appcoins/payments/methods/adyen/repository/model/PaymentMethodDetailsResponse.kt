package com.appcoins.payments.methods.adyen.repository.model

import com.appcoins.payments.json.Json
import org.json.JSONObject
import java.math.BigDecimal

@Json
data class PaymentMethodDetailsResponse(
  val price: AdyenPrice,
  val payment: JSONObject,
)

@Json
data class AdyenPrice(
  val value: BigDecimal,
  val currency: String,
)
