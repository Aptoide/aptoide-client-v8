package com.appcoins.payments.methods.adyen

import org.json.JSONObject

class PaymentMethodDetailsData(
  val price: Double,
  val currency: String,
  val json: JSONObject,
)
