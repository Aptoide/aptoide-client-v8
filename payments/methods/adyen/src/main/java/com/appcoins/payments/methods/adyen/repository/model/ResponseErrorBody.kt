package com.appcoins.payments.methods.adyen.repository.model

import com.appcoins.payments.json.Json

@Json
data class ResponseErrorBody(
  val code: String?,
  val path: String?,
  val text: String?,
  val data: Any?,
)