package com.appcoins.payment_method.adyen

import org.json.JSONObject

class PaymentMethodDetailsData(
  val price: Double,
  val currency: String,
  val json: JSONObject
)
