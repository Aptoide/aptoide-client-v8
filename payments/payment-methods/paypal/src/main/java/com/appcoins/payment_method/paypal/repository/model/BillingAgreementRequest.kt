package com.appcoins.payment_method.paypal.repository.model

import com.appcoins.payments.json.Json

@Json
internal data class BillingAgreementRequest(
  @Json("urls") val urls: Urls,
)

@Json
internal data class Urls(
  @Json("return") val returnUrl: String,
  @Json("cancel") val cancelUrl: String,
)
