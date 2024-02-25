package com.appcoins.payments.network

import com.appcoins.payments.network.HttpLogger.Level
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.time.Duration

interface RestClient {

  /**
   * Make a network call getting the [T] result
   *
   * @param method for the URL request, one of: GET, POST, HEAD, OPTIONS, PUT, DELETE, TRACE
   * @param path String to be added to the base url of the request
   * @param query Map of the key values to be added as query
   * @param header Map of the key values to be added to the header
   * @param body object that will be converted into Json body.
   * @param responseType Class type [T] of the response body.
   *
   * @returns [T] result for HTTP codes 2XX
   * @throws HttpException for HTTP codes 1XX, 3XX, 4XX and 5XX
   * @throws any other exceptions for any other errors
   */
  suspend fun <T : Any> call(
    method: String,
    path: String,
    header: Map<String, String>,
    query: Map<String, String?>,
    body: Any?,
    responseType: Class<T>,
  ): T

  companion object {
    fun with(
      scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
      httpLogger: HttpLogger? = HttpLogger(Level.BODY),
      baseUrl: String,
      timeout: Duration = Duration.ofSeconds(10),
      getUserAgent: GetUserAgent,
    ): RestClient = RestClientImpl(
      scope = scope,
      httpLogger = httpLogger,
      baseUrl = baseUrl,
      timeout = timeout,
      getUserAgent = getUserAgent
    )
  }
}

suspend inline fun <reified T : Any> RestClient.get(
  path: String,
  header: Map<String, String> = emptyMap(),
  query: Map<String, String?> = emptyMap(),
): T = call(
  method = "GET",
  path = path,
  header = header,
  query = query,
  body = null,
  responseType = T::class.java
)

suspend inline fun <reified T : Any> RestClient.post(
  path: String,
  header: Map<String, String> = emptyMap(),
  query: Map<String, String?> = emptyMap(),
  body: Any? = null,
): T = call(
  method = "POST",
  path = path,
  header = header,
  query = query,
  body = body,
  responseType = T::class.java
)

suspend inline fun <reified T : Any> RestClient.patch(
  path: String,
  header: Map<String, String> = emptyMap(),
  query: Map<String, String?> = emptyMap(),
  body: Any? = null,
): T = call(
  method = "PATCH",
  path = path,
  header = header,
  query = query,
  body = body,
  responseType = T::class.java
)
