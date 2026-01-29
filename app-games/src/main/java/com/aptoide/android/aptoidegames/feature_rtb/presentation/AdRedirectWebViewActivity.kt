package com.aptoide.android.aptoidegames.feature_rtb.presentation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.core.net.toUri
import timber.log.Timber

/**
 * A fully transparent Activity that uses a WebView to resolve ad tracking URLs.
 * It follows all redirects (HTTP and JavaScript-based) until it reaches
 * a Google Play URL, market:// URL, or intent:// URL.
 */
class AdRedirectWebViewActivity : Activity() {

  private var webView: WebView? = null
  @Volatile private var hasFoundFinalUrl = false
  private var redirectCount = 0
  private val maxRedirects = 20

  companion object {
    const val EXTRA_TRACKING_URL = "extra_tracking_url"
    const val EXTRA_TIMEOUT_SECONDS = "extra_timeout_seconds"
    private const val DEFAULT_TIMEOUT_SECONDS = 10

    private var onResultCallback: ((AdRedirectResult) -> Unit)? = null

    fun createIntent(
      context: Context,
      trackingUrl: String,
      timeoutSeconds: Int?,
      callback: (AdRedirectResult) -> Unit
    ): Intent {
      onResultCallback = callback
      return Intent(context, AdRedirectWebViewActivity::class.java).apply {
        putExtra(EXTRA_TRACKING_URL, trackingUrl)
        putExtra(EXTRA_TIMEOUT_SECONDS, timeoutSeconds ?: DEFAULT_TIMEOUT_SECONDS)
      }
    }

    private fun clearCallback() {
      onResultCallback = null
    }
  }

  sealed class AdRedirectResult {
    data class Success(val finalUrl: String) : AdRedirectResult()
    data class Error(val message: String) : AdRedirectResult()
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
        Timber.d("WebView: Timeout reached (${timeoutSeconds}s), returning current URL")
        finishWithError("Timeout waiting for redirect")
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
        errorCode: Int,
        description: String?,
        failingUrl: String?
      ) {
        super.onReceivedError(view, errorCode, description, failingUrl)
        Timber.d("WebView: onReceivedError: $errorCode - $description for $failingUrl")
        // Don't fail immediately on errors - the redirect might still work
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

  private fun finishWithError(errorMessage: String) {
    if (hasFoundFinalUrl) return // Prevent multiple callbacks
    hasFoundFinalUrl = true

    Timber.d("WebView: Finishing with error: $errorMessage")

    runOnUiThread {
      webView?.stopLoading()
      webView?.webViewClient = object : WebViewClient() {} // Clear callbacks

      onResultCallback?.invoke(AdRedirectResult.Error(errorMessage))
      clearCallback()
      finish()
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
