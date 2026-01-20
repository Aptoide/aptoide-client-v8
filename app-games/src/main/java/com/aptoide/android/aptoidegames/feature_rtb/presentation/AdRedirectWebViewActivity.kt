package com.aptoide.android.aptoidegames.feature_rtb.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.core.net.toUri
import com.aptoide.android.aptoidegames.feature_rtb.analytics.RTBErrorLogger
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

/**
 * A fully transparent Activity that uses a WebView to resolve ad tracking URLs.
 * It follows all redirects (HTTP and JavaScript-based) until it reaches
 * a Google Play URL, market:// URL, or intent:// URL.
 */
@AndroidEntryPoint
class AdRedirectWebViewActivity : ComponentActivity() {

  @Inject lateinit var rtbErrorLogger: RTBErrorLogger

  private var webView: WebView? = null
  @Volatile private var hasFoundFinalUrl = false
  private var redirectCount = 0
  private val maxRedirects = 20
  private var lastLoadedUrl: String? = null
  private var lastErrorCode: Int? = null
  private var lastErrorDescription: String? = "n/a"
  private var campaignId: String? = null

  companion object {
    const val EXTRA_TRACKING_URL = "extra_tracking_url"
    const val EXTRA_TIMEOUT_SECONDS = "extra_timeout_seconds"
    private const val DEFAULT_TIMEOUT_SECONDS = 10
    private const val DEFAULT_TIMEOUT_ERROR_CODE = 99999
    const val EXTRA_CAMPAIGN_ID = "extra_campaign_id"

    private var onResultCallback: ((AdRedirectResult) -> Unit)? = null

    fun createIntent(
      context: Context,
      trackingUrl: String,
      timeoutSeconds: Int?,
      campaignId: String?,
      callback: (AdRedirectResult) -> Unit
    ): Intent {
      onResultCallback = callback
      return Intent(context, AdRedirectWebViewActivity::class.java).apply {
        putExtra(EXTRA_TRACKING_URL, trackingUrl)
        putExtra(EXTRA_TIMEOUT_SECONDS, timeoutSeconds ?: DEFAULT_TIMEOUT_SECONDS)
        putExtra(EXTRA_CAMPAIGN_ID, campaignId)
      }
    }

    private fun clearCallback() {
      onResultCallback = null
    }
  }

  sealed class AdRedirectResult {
    data class Success(val finalUrl: String) : AdRedirectResult()
    data class Error(
      val message: String,
      val lastUrl: String? = null,
      val lastErrorType: String? = null,
      val lastErrorDescription: String? = null
    ) : AdRedirectResult()
  }

  @SuppressLint("SetJavaScriptEnabled")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val frameLayout = FrameLayout(this).apply {
      setBackgroundColor(android.graphics.Color.TRANSPARENT)
    }

    val wv = WebView(this).apply {
      layoutParams = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT
      )
      visibility = View.INVISIBLE
      setBackgroundColor(android.graphics.Color.TRANSPARENT)
    }
    webView = wv
    frameLayout.addView(wv)
    setContentView(frameLayout)

    setupWebView()

    val trackingUrl = intent.getStringExtra(EXTRA_TRACKING_URL)
    campaignId = intent.getStringExtra(EXTRA_CAMPAIGN_ID)
    if (trackingUrl.isNullOrBlank()) {
      Timber.d("No tracking URL provided")
      finishWithError("No tracking URL provided")
      return
    }

    val timeoutSeconds = intent.getIntExtra(EXTRA_TIMEOUT_SECONDS, DEFAULT_TIMEOUT_SECONDS)
    val timeoutMillis = timeoutSeconds * 1000L

    Timber.d("WebView: Starting redirect resolution for URL: $trackingUrl with timeout: ${timeoutSeconds}s")
    wv.loadUrl(trackingUrl)

    wv.postDelayed({
      if (!hasFoundFinalUrl) {
        Timber.d("WebView: Timeout reached (${timeoutSeconds}s), lastUrl=$lastLoadedUrl, lastErrorCode=$lastErrorCode, lastErrorDescription=$lastErrorDescription")

        val campaignId = this@AdRedirectWebViewActivity.campaignId
        if (campaignId != null) {
          rtbErrorLogger.logError(
            campaignId = campaignId,
            code = DEFAULT_TIMEOUT_ERROR_CODE,
            errorType = "timeout",
            description = "Timeout reached (${timeoutSeconds}s)",
            url = lastLoadedUrl
          )
        }

        finishWithError(
          errorMessage = "Timeout waiting for redirect",
          lastUrl = lastLoadedUrl
        )
      }
    }, timeoutMillis)
  }

  @SuppressLint("SetJavaScriptEnabled")
  private fun setupWebView() {
    val wv = webView ?: return

    wv.settings.apply {
      javaScriptEnabled = true
      domStorageEnabled = true
    }

    wv.webViewClient = object : WebViewClient() {

      override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        if (hasFoundFinalUrl) return true

        val url = request.url.toString()
        Timber.d("WebView: shouldOverrideUrlLoading: $url")
        redirectCount++

        if (redirectCount > maxRedirects) {
          Timber.d("WebView: Max redirects reached ($maxRedirects)")
          finishWithError("Max redirects reached")
          return true
        }

        if (isFinalDestinationUrl(url)) {
          Timber.d("WebView: Found final destination URL in shouldOverrideUrlLoading: $url")
          finishWithSuccess(url)
          return true
        }

        Timber.d("WebView: Following redirect #$redirectCount to: $url")
        return false
      }

      @Deprecated("Deprecated in Java")
      override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        if (hasFoundFinalUrl) return true

        Timber.d("WebView: shouldOverrideUrlLoading (legacy): $url")
        redirectCount++

        if (redirectCount > maxRedirects) {
          Timber.d("WebView: Max redirects reached ($maxRedirects)")
          finishWithError("Max redirects reached")
          return true
        }

        if (isFinalDestinationUrl(url)) {
          Timber.d("WebView: Found final destination URL in shouldOverrideUrlLoading (legacy): $url")
          finishWithSuccess(url)
          return true
        }

        Timber.d("WebView: Following redirect #$redirectCount to: $url")
        return false
      }

      override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        Timber.d("WebView: onPageStarted: $url")
        url?.let { lastLoadedUrl = it }
      }

      override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        Timber.d("WebView: onPageFinished: $url")

        // Only check here as a fallback if shouldOverrideUrlLoading didn't catch it
        // (can happen with some JavaScript redirects that land on the final page)
        if (!hasFoundFinalUrl) {
          url?.let {
            if (isFinalDestinationUrl(it)) {
              finishWithSuccess(it)
            }
          }
        }
      }

      override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
      ) {
        super.onReceivedError(view, request, error)
        val errorCode = error?.errorCode
        val description = error?.description?.toString()
        val url = request?.url?.toString()
        val isMainFrame = request?.isForMainFrame == true

        Timber.d("WebView: onReceivedError: $errorCode - $description for $url (isMainFrame: $isMainFrame)")

        // Log every error to remote endpoint for monitoring
        val campaignId = this@AdRedirectWebViewActivity.campaignId
        if (campaignId != null) {
          rtbErrorLogger.logError(
            campaignId = campaignId,
            code = errorCode ?: -1,
            errorType = getErrorType(errorCode),
            description = description,
            url = url
          )
        }

        // Check if this is a terminal error that cannot be recovered by JavaScript
        if (errorCode != null && isTerminalError(errorCode)) {
          Timber.d("WebView: Terminal error encountered, stopping redirect resolution")
          lastErrorCode = errorCode
          lastErrorDescription = description
          finishWithError(
            errorMessage = "Terminal error: ${getErrorType(errorCode)}",
            lastUrl = url
          )
          return
        }

        // Don't fail immediately on other errors - the redirect might still work via JavaScript
        // Prioritize main frame errors over sub-resource errors (tracking pixels, etc.)
        if (isMainFrame || lastErrorCode == null) {
          lastErrorCode = errorCode
          lastErrorDescription = description
        }
      }
    }
  }

  private fun isFinalDestinationUrl(url: String): Boolean {
    return url.startsWith("market://") ||
      url.startsWith("intent://") ||
      url.contains("play.google.com/store") ||
      url.contains("market.android.com")
  }

  private fun finishWithSuccess(finalUrl: String) {
    if (hasFoundFinalUrl) {
      Timber.d("WebView: finishWithSuccess called but already found final URL, ignoring")
      return
    }
    hasFoundFinalUrl = true

    Timber.d("WebView: Finishing with success, final URL: $finalUrl")

    val processedUrl = processIntentUrl(finalUrl)
    Timber.d("WebView: Processed URL: $processedUrl")

    runOnUiThread {
      webView?.stopLoading()
      webView?.webViewClient = object : WebViewClient() {} // Clear callbacks

      onResultCallback?.invoke(AdRedirectResult.Success(processedUrl))
      clearCallback()

      finish()
    }
  }

  private fun finishWithError(
    errorMessage: String,
    lastUrl: String? = null
  ) {
    if (hasFoundFinalUrl) return // Prevent multiple callbacks
    hasFoundFinalUrl = true

    val errorType = lastErrorCode?.let { getErrorType(it) } ?: "n/a"
    Timber.d("WebView: Finishing with error: $errorMessage, lastUrl: $lastUrl, errorType: $errorType, errorDescription: $lastErrorDescription")

    runOnUiThread {
      webView?.stopLoading()
      webView?.webViewClient = object : WebViewClient() {} // Clear callbacks

      onResultCallback?.invoke(
        AdRedirectResult.Error(
          message = errorMessage,
          lastUrl = lastUrl,
          lastErrorType = errorType,
          lastErrorDescription = lastErrorDescription
        )
      )
      clearCallback()
      finish()
    }
  }

  /**
   * Maps WebView error codes to human-readable error types for analytics grouping.
   * Returns "unknown" for null error codes.
   */
  private fun getErrorType(errorCode: Int?): String {
    return when (errorCode) {
      null -> "unknown"
      WebViewClient.ERROR_HOST_LOOKUP -> "dns_lookup_failed"
      WebViewClient.ERROR_CONNECT -> "connection_failed"
      WebViewClient.ERROR_TIMEOUT -> "connection_timeout"
      WebViewClient.ERROR_IO -> "io_error"
      WebViewClient.ERROR_FAILED_SSL_HANDSHAKE -> "ssl_handshake_failed"
      WebViewClient.ERROR_BAD_URL -> "bad_url"
      WebViewClient.ERROR_UNSUPPORTED_SCHEME -> "unsupported_scheme"
      WebViewClient.ERROR_FILE_NOT_FOUND -> "file_not_found"
      WebViewClient.ERROR_AUTHENTICATION -> "authentication_failed"
      WebViewClient.ERROR_UNKNOWN -> "unknown"
      else -> "other_$errorCode"
    }
  }

  /**
   * Determines if an error is terminal (cannot be recovered by JavaScript).
   * Terminal errors should stop the redirect resolution immediately.
   */
  private fun isTerminalError(errorCode: Int): Boolean {
    return when (errorCode) {
      WebViewClient.ERROR_BAD_URL, // Malformed URL, can't be recovered
      WebViewClient.ERROR_UNSUPPORTED_SCHEME, // Protocol WebView doesn't handle
      WebViewClient.ERROR_HOST_LOOKUP, // DNS resolution failed
      WebViewClient.ERROR_CONNECT, // Can't establish TCP connection
      WebViewClient.ERROR_AUTHENTICATION, // Requires unavailable auth
      WebViewClient.ERROR_FAILED_SSL_HANDSHAKE -> // SSL negotiation failed
        true

      else -> false
    }
  }

  /**
   * Process intent:// URLs to extract the actual target.
   * Format: intent://details?id=com.example#Intent;scheme=market;...;end
   */
  private fun processIntentUrl(url: String): String {
    if (!url.startsWith("intent://")) return url

    try {
      val uri = url.toUri()

      val packageName = uri.getQueryParameter("id")
        ?: uri.host?.takeIf { it == "details" }?.let { uri.getQueryParameter("id") }

      if (url.contains("scheme=market") && packageName != null) {
        return "market://details?id=$packageName"
      }

      val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
      intent.data?.toString()?.let { return it }

      packageName?.let {
        return "market://details?id=$it"
      }
    } catch (e: Exception) {
      Timber.d(e, "WebView: Failed to process intent URL: $url")
    }

    return url
  }

  override fun onBackPressed() {
    super.onBackPressed()
    finishWithError("User cancelled")
  }

  override fun onDestroy() {
    // Don't manually destroy WebView - let Android handle it
    // Manual destruction was causing renderer crashes that corrupted the Activity result
    webView?.let { wv ->
      wv.stopLoading()
      wv.loadUrl("about:blank") // Clear current page
    }
    webView = null
    super.onDestroy()
  }
}
