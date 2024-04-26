package com.appcoins.payments.network

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.time.withTimeout
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.time.Duration

class RestClientImpl(
  private val scope: CoroutineScope,
  private val httpLogger: HttpLogger,
  private val baseUrl: String,
  private val restClientInjectParams: RestClientInjectParams,
) : RestClient {

  override suspend fun call(
    method: String,
    path: String,
    header: Map<String, String>,
    query: Map<String, String?>,
    body: String?,
    timeout: Duration,
  ): String? = withContext(scope.coroutineContext) {
    val requestQuery = query.injectQueryParams(path).toQuery()
    val requestBody = body?.injectParams(path)?.toBytes() ?: ByteArray(0)
    val requestHeaders = header.injectHeaders(requestBody)
    (URL("$baseUrl$path?$requestQuery").openConnection() as HttpURLConnection).run {
      requestMethod = method
      connectTimeout = timeout.toMillis().toInt()
      readTimeout = timeout.toMillis().toInt()
      requestHeaders.entries.forEach {
        setRequestProperty(it.key, it.value)
      }
      httpLogger.logRequest(this, requestBody)
      try {
        if (requestBody.isNotEmpty()) {
          outputStream.use { os ->
            withTimeout(timeout) {
              os.write(requestBody)
            }
          }
        }
        val responseBody = inputStream.use(InputStream::readBytes)
        // We accept only 2XX as success, 1XX and 3XX we treat as errors.
        responseCode.takeIf { it !in 200..299 }?.also {
          throw HttpException(
            code = it,
            message = responseMessage,
            bodyBytes = responseBody
          )
        }
        httpLogger.logResponse(this, responseBody)
        responseBody.toJsonString()
      } catch (error: Throwable) {
        val realError = error as? HttpException // For 1XX and 3XX cases
          ?: responseCode.takeIf { it >= 0 && it !in 200..299 } // Also for 4XX and 5XX cases
            ?.let {
              HttpException(
                code = it,
                message = responseMessage,
                bodyBytes = runCatching { errorStream?.use(InputStream::readBytes) }.getOrNull()
                  ?: ByteArray(0)
              )
            }
          ?: error // For everything else we just leave this as is
        httpLogger.logError(this, realError)
        throw realError
      } finally {
        disconnect()
      }
    }
  }

  private fun Map<String, String?>.toQuery(): String = entries
    .mapNotNull { (key, value) ->
      value?.let {
        URLEncoder.encode(key, Charsets.UTF_8.name()) +
          "=" + URLEncoder.encode(it, Charsets.UTF_8.name())
      }
    }
    .joinToString(separator = "&")

  private fun Map<String, String?>.injectHeaders(body: ByteArray): Map<String, String?> =
    toMutableMap().apply {
      this["User-Agent"] = restClientInjectParams.getUserAgent()
      this["Content-Type"] = if (body.isNotEmpty()) {
        "application/json; charset=UTF-8"
      } else {
        "application/octet-stream"
      }
      this["Content-Length"] = "${body.size}"
    }

  private fun Map<String, String?>.injectQueryParams(path: String): Map<String, String?> =
    if (path.contains(Regex("broker/8.*/methods"))) {
      this + ("channel" to restClientInjectParams.channel)
    } else {
      this
    }

  private fun String.injectParams(path: String): String =
    if (path.contains(Regex("broker/8.*/gateways/.*/transactions"))) {
      JSONObject(this).put("channel", restClientInjectParams.channel).toString()
    } else {
      this
    }
}
