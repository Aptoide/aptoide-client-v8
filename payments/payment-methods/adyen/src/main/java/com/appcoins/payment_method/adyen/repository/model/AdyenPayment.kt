package com.appcoins.payment_method.adyen.repository.model

import com.appcoins.payments.json.Json
import org.json.JSONObject

@Json
data class AdyenPayment(
  @Json("payment.details") val details: JSONObject?,
  @Json("payment.data") val data: String?,
)
