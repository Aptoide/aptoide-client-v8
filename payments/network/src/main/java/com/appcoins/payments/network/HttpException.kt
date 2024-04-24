package com.appcoins.payments.network

/** Exception for an unexpected, non-2xx HTTP response.  */
class HttpException(
  val code: Int,
  message: String?,
  val bodyBytes: ByteArray,
) : RuntimeException(message) {

  val body: String? get() = bodyBytes.toJsonString()
}
