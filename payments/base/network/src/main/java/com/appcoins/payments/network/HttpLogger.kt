package com.appcoins.payments.network

import com.appcoins.payments.arch.Logger
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit

/**
 * Logs request and response lines and their respective headers and bodies (if present).
 *
 * Example:
 * ```
 * HTTP --> POST /greeting http/1.1
 * Host: example.com
 * Content-Type: plain/text
 * Content-Length: 3
 * Body: Hi?
 *
 * HTTP <-- 200 OK (22ms)
 * Content-Type: plain/text
 * Content-Length: 6
 * Body: Hello!
 * ```
 */
class HttpLogger(private val logger: Logger) {

  private val timers = mutableMapOf<Int, Long>()

  fun logRequest(
    connection: HttpURLConnection,
    body: ByteArray,
  ) {
    val method = connection.requestMethod
    val url = connection.url

    var requestMessage = ("——→ $method $url")
    if (body.isNotEmpty()) {
      requestMessage += " (${body.size}-byte body)"
    }

    val data = mutableMapOf<String, String>()
    val headers = connection.requestProperties

    headers.filterNot { it.key == null || it.value == null }
      .forEach { (key, value) ->
        data[key] = value.joinToString("; ")
      }

    data["\uD83D\uDCE6Body"] = if (bodyHasUnknownEncoding(headers)) {
      "(encoded body omitted)"
    } else {
      body.toJsonString() ?: "(Not present)"
    }
    logger.logHttpEvent(
      message = requestMessage,
      data = data
    )
    timers[connection.hashCode()] = System.nanoTime()
  }

  fun logResponse(
    connection: HttpURLConnection,
    body: ByteArray,
  ) {
    val startNs = timers.remove(connection.hashCode())
    val tookMs = startNs?.let { TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - it) }

    val responseMessage = connection.responseMessage?.ifEmpty { null }?.let { " $it" } ?: ""

    val data = mutableMapOf<String, String>()
    val headers = connection.headerFields

    headers.filterNot { it.key == null || it.value == null }
      .forEach { (key, value) ->
        data[key] = value.joinToString("; ")
      }

    data["\uD83D\uDCE6Body"] = if (bodyHasUnknownEncoding(headers)) {
      "(encoded body omitted)"
    } else {
      body.toJsonString() ?: "(Not present)"
    }
    logger.logHttpEvent(
      message = "←—— ${connection.responseCode}$responseMessage ${connection.url} (${tookMs}ms)",
      data = data
    )
  }

  fun logError(
    connection: HttpURLConnection,
    error: Throwable,
  ) {
    timers.remove(connection.hashCode())
    if (error is HttpException) {
      logResponse(connection, error.bodyBytes)
    } else {
      val method = connection.requestMethod
      val url = connection.url
      logger.logHttpEvent("<-- FAILED to $method $url")
    }
    logger.logHttpError(error)
  }

  private fun bodyHasUnknownEncoding(headers: Map<String, List<String>>): Boolean {
    val contentEncoding = headers["Content-Encoding"]?.firstOrNull() ?: return false
    return !contentEncoding.equals("identity", ignoreCase = true) &&
      !contentEncoding.equals("gzip", ignoreCase = true)
  }
}

private fun Logger.logHttpError(throwable: Throwable) = logError(
  tag = "HTTP",
  throwable = throwable,
)

private fun Logger.logHttpEvent(
  message: String,
  data: Map<String, Any?> = emptyMap(),
) = logEvent(
  tag = "HTTP",
  message = message,
  data = data
)
