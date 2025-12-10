package com.aptoide.android.aptoidegames.feature_rtb.repository

import cm.aptoide.pt.aptoide_network.di.RawOkHttp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import javax.inject.Inject

class RTBAdsRepository @Inject constructor(
  @RawOkHttp private val okHttpClient: OkHttpClient,
) {

  suspend fun resolveAdRedirects(url: String): Result<String> = withContext(Dispatchers.IO) {
    try {
      var currentUrl = url
      var redirectCount = 0
      val maxRedirects = 10
      var shouldContinue = true

      while (redirectCount < maxRedirects && shouldContinue) {
        val request = Request.Builder()
          .url(currentUrl)
          .get()
          .build()

        okHttpClient.newCall(request).execute().use { response ->
          val responseCode = response.code

          when (responseCode) {
            in 300..399 -> {
              val location = response.header("Location")
              if (location != null) {
                if (location.startsWith("http://") || location.startsWith("https://")) {
                  // Absolute HTTP(S) redirect – follow it
                  redirectCount++
                  currentUrl = location
                } else if (location.contains("://")) {
                  // Non-HTTP(S) absolute URL (e.g. market://, intent://) – treat as terminal
                  return@withContext Result.success(location)
                } else {
                  // Relative URL - resolve against current URL
                  redirectCount++
                  val baseUrl = response.request.url
                  currentUrl = baseUrl.resolve(location)?.toString()
                    ?: throw IllegalStateException("Failed to resolve relative URL: $location")
                }
              } else {
                // Redirect without Location header - stop here
                Timber.w("Redirect response without Location header")
                shouldContinue = false
              }
            }

            in 200..299 -> {
              // Final destination reached
              return@withContext Result.success(currentUrl)
            }

            else -> {
              return@withContext Result.failure(
                Exception("Unexpected HTTP response code: $responseCode")
              )
            }
          }
        }
      }

      if (redirectCount >= maxRedirects) {
        Timber.w("Max redirects reached ($maxRedirects). Returning last URL: $currentUrl")
      }

      Result.success(currentUrl)
    } catch (e: Exception) {
      Timber.e(e, "Error resolving ad redirects for URL: $url")
      Result.failure(e)
    }
  }

  companion object {
    private const val TAG = "RTBAdsRepository"
  }
}
