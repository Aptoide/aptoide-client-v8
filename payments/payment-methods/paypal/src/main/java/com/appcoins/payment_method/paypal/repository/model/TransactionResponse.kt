package com.appcoins.payment_method.paypal.repository.model

import com.appcoins.payments.arch.TransactionStatus
import com.appcoins.payments.json.Json

@Json
internal data class TransactionResponse(
  val uid: String,
  val hash: String?,
  val status: TransactionStatus,
  val data: ErrorData?,
)

@Json
internal data class ErrorData(
  val name: String?,
  val message: String?,
)

@Json
internal data class TransactionError(val code: String?)
