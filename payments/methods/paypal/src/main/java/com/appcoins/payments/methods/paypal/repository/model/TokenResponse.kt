package com.appcoins.payments.methods.paypal.repository.model

import com.appcoins.payments.json.Json

@Json
internal data class TokenResponse(
  val token: String,
  val redirect: Redirect,
)

@Json
internal data class Redirect(
  val url: String,
  val method: String,
)
