package com.appcoins.payment_method.paypal.repository.model

import androidx.annotation.Keep

@Keep
internal data class TokenResponse(
  val token: String,
  val redirect: Redirect
)

@Keep
internal data class Redirect(
  val url: String,
  val method: String
)
