package com.appcoins.payments.network

import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit

class HttpLogger(
  private val level: Level = Level.NONE,
  private val logger: Logger = Logger.DEFAULT,
) {

  enum class Level {
    /** No logs. */
    NONE,

    /**
     * Logs request and response lines.
     *
     * Example:
     * ```
     * --> POST /greeting http/1.1 (3-byte body)
     *
     * <-- 200 OK (22ms, 6-byte body)
     * ```
     */
    BASIC,

    /**
     * Logs request and response lines and their respective headers.
     *
     * Example:
     * ```
     * --> POST /greeting http/1.1
     * Host: example.com
     * Content-Type: plain/text
     * Content-Length: 3
     * --> END POST
     *
     * <-- 200 OK (22ms)
     * Content-Type: plain/text
     * Content-Length: 6
     * <-- END HTTP
     * ```
     */
    HEADERS,

    /**
     * Logs request and response lines and their respective headers and bodies (if present).
     *
     * Example:
     * ```
     * --> POST /greeting http/1.1
     * Host: example.com
     * Content-Type: plain/text
     * Content-Length: 3
     *
     * Hi?
     * --> END POST
     *
     * <-- 200 OK (22ms)
     * Content-Type: plain/text
     * Content-Length: 6
     *
     * Hello!
     * <-- END HTTP
     * ```
     */
    BODY
  }

  fun interface Logger {
    fun log(message: String)

    companion object {
      @JvmField
      val DEFAULT: Logger = DefaultLogger()

      private class DefaultLogger : Logger {
        override fun log(message: String) {
          println(message)
        }
      }
    }
  }

  private val timers = mutableMapOf<Int, Long>()

  fun logRequest(
    connection: HttpURLConnection,
    body: ByteArray,
  ) {
    if (level == Level.NONE) return
    val logBody = level == Level.BODY
    val logHeaders = logBody || level == Level.HEADERS

    val method = connection.requestMethod

    var requestStartMessage = ("--> $method ${connection.url}")
    if (!logHeaders && body.isNotEmpty()) {
      requestStartMessage += " (${body.size}-byte body)"
    }
    logger.log(requestStartMessage)

    if (logHeaders) {
      val headers = connection.requestProperties

      headers.forEach {
        logger.log(it.key + ": " + it.value.joinToString("; "))
      }

      if (!logBody) {
        logger.log("--> END $method")
      } else if (bodyHasUnknownEncoding(headers)) {
        logger.log("--> END $method (encoded body omitted)")
      } else {
        body.toJsonString()?.also {
          logger.log("")
          logger.log(it)
        }
        logger.log("--> END $method (${body.size}-byte body)")
      }
    }
    timers[connection.hashCode()] = System.nanoTime()
  }

  fun logResponse(
    connection: HttpURLConnection,
    body: ByteArray,
  ) {
    val logBody = level == Level.BODY
    val logHeaders = logBody || level == Level.HEADERS
    val startNs = timers.remove(connection.hashCode())
    val tookMs = startNs?.let { TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - it) }

    val responseMessage = connection.responseMessage?.ifEmpty { null }?.let { " $it" } ?: ""
    logger.log(
      "<-- ${connection.responseCode}$responseMessage ${connection.url} (${tookMs}ms${if (!logHeaders) ", ${body.size}-byte body" else ""})"
    )

    if (logHeaders) {
      val headers = connection.headerFields

      headers.forEach {
        logger.log(it.key + ": " + it.value.joinToString("; "))
      }

      if (!logBody) {
        logger.log("<-- END HTTP")
      } else if (bodyHasUnknownEncoding(headers)) {
        logger.log("<-- END HTTP (encoded body omitted)")
      } else {
        body.toJsonString()?.also {
          logger.log("")
          logger.log(it)
        }
        logger.log("<-- END HTTP (${body.size}-byte body)")
      }
    }
  }

  fun logError(
    connection: HttpURLConnection,
    error: Throwable,
  ) {
    timers.remove(connection.hashCode())
    if (error is HttpException) {
      logResponse(connection, error.bodyBytes)
    } else {
      logger.log("<-- HTTP FAILED: $error")
    }
  }

  private fun bodyHasUnknownEncoding(headers: Map<String, List<String>>): Boolean {
    val contentEncoding = headers["Content-Encoding"]?.firstOrNull() ?: return false
    return !contentEncoding.equals("identity", ignoreCase = true) &&
      !contentEncoding.equals("gzip", ignoreCase = true)
  }
}
