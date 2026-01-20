package com.aptoide.android.aptoidegames.feature_rtb.analytics

import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

/**
 * Logs RTB tracking errors to a remote endpoint configured via Firebase Remote Config.
 * This is a fire-and-forget logger that sends error information as it happens,
 * providing visibility into WebView redirect issues.
 */
class RTBErrorLogger(
  private val featureFlags: FeatureFlags,
  private val okHttpClient: OkHttpClient,
  private val scope: CoroutineScope
) {
  private var cachedEndpoint: String? = null
  private var hasCheckedEndpoint = false

  /**
   * Logs an error to the remote endpoint.
   * This is fire-and-forget - failures are silently ignored.
   *
   * @param campaignId The RTB campaign ID associated with this ad
   * @param code The WebView error code (e.g., -2 for ERROR_HOST_LOOKUP, -1 for unknown)
   * @param errorType The predefined error type identifier (e.g., "dns_lookup_failed", "unknown")
   * @param description The error description from WebView
   * @param url The URL that caused the error
   */
  fun logError(
    campaignId: String,
    code: Int,
    errorType: String,
    description: String?,
    url: String?
  ) {
    scope.launch(Dispatchers.IO) {
      val endpoint = getEndpoint() ?: return@launch
      runCatching {
        val json = buildBody(
          campaignId = campaignId,
          code = code,
          errorType = errorType,
          description = description,
          url = url
        )
        val request = Request.Builder()
          .url(endpoint)
          .addHeader("Accept", "application/json")
          .addHeader("Content-Type", "application/json")
          .post(json.toRequestBody(JSON_MEDIA_TYPE))
          .build()
        okHttpClient.newCall(request).execute().close()
      }
    }
  }

  private suspend fun getEndpoint(): String? {
    if (!hasCheckedEndpoint) {
      cachedEndpoint = featureFlags.getFlagAsString(RTB_ERROR_ENDPOINT_KEY)
      hasCheckedEndpoint = true
    }
    return cachedEndpoint?.takeIf { it.isNotBlank() }
  }

  private fun buildBody(
    campaignId: String,
    code: Int,
    errorType: String,
    description: String?,
    url: String?
  ): String {
    return JSONObject().apply {
      put("campaign_id", campaignId)
      put("code", code.toString())
      put("error_type", errorType)
      put("description", description ?: "n/a")
      put("url", url ?: "unknown")
    }.toString()
  }

  companion object {
    const val RTB_ERROR_ENDPOINT_KEY = "rtb_error_endpoint_url"
    private val JSON_MEDIA_TYPE = "application/json".toMediaType()
  }
}
