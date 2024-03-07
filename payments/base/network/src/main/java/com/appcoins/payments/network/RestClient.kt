package com.appcoins.payments.network

import com.appcoins.payments.arch.GetUserAgent
import com.appcoins.payments.network.HttpLogger.Level
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.time.Duration

interface RestClient {

  /**
   * Make a network call getting the [String] result
   *
   * @param method for the URL request, one of: GET, POST, HEAD, OPTIONS, PUT, DELETE, TRACE
   * @param path String to be added to the base url of the request
   * @param query Map of the key values to be added as query
   * @param header Map of the key values to be added to the header
   * @param body a Json [String] that will be sent as a body.
   * @param timeout the timeout values used in the network call
   *
   * @returns a Json [String] body for 2XX HTTP codes if any or null
   * @throws HttpException for 1XX, 3XX, 4XX and 5XX HTTP codes
   * @throws any other exceptions for any other errors
   */
  suspend fun call(
    method: String,
    path: String,
    header: Map<String, String>,
    query: Map<String, String?>,
    body: String?,
    timeout: Duration = Duration.ofSeconds(10),
  ): String?

  companion object {
    fun with(
      scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
      httpLogger: HttpLogger? = HttpLogger(Level.BODY),
      baseUrl: String,
      getUserAgent: GetUserAgent,
    ): RestClient = RestClientImpl(
      scope = scope,
      httpLogger = httpLogger,
      baseUrl = baseUrl,
      getUserAgent = getUserAgent
    )
  }
}

suspend fun RestClient.get(
  path: String,
  header: Map<String, String> = emptyMap(),
  query: Map<String, String?> = emptyMap(),
  timeout: Duration = Duration.ofSeconds(10),
): String? = call(
  method = "GET",
  path = path,
  header = header,
  query = query,
  body = null,
  timeout = timeout,
)

suspend fun RestClient.post(
  path: String,
  header: Map<String, String> = emptyMap(),
  query: Map<String, String?> = emptyMap(),
  body: String? = null,
  timeout: Duration = Duration.ofSeconds(10),
): String? = call(
  method = "POST",
  path = path,
  header = header,
  query = query,
  body = body,
  timeout = timeout,
)

suspend fun RestClient.patch(
  path: String,
  header: Map<String, String> = emptyMap(),
  query: Map<String, String?> = emptyMap(),
  body: String? = null,
  timeout: Duration = Duration.ofSeconds(10),
): String? = call(
  method = "PATCH",
  path = path,
  header = header,
  query = query,
  body = body,
  timeout = timeout,
)
