package com.appcoins.payments.arch

interface Logger {
  fun logEvent(
    tag: String,
    message: String,
    data: Map<String, Any?> = emptyMap(),
  )

  fun logError(
    tag: String,
    throwable: Throwable,
  )
}

object MutedLogger : Logger {
  override fun logEvent(tag: String, message: String, data: Map<String, Any?>) {}
  override fun logError(tag: String, throwable: Throwable) {}
}
