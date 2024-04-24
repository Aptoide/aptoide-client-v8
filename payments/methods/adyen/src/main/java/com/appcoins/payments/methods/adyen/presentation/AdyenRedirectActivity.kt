package com.appcoins.payments.methods.adyen.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import com.adyen.checkout.components.model.payments.response.RedirectAction
import com.adyen.checkout.redirect.RedirectComponent
import com.appcoins.payments.di.Payments
import com.appcoins.payments.di.redirectConfiguration

class AdyenRedirectActivity : ComponentActivity() {

  companion object {
    const val WRONG_URL = 2
  }

  private val logger = Payments.logger

  @Suppress("DEPRECATION")
  private val url by lazy {
    intent.getParcelableExtra<RedirectAction>(AdyenActionResolveContract.REDIRECT_ACTION)!!.url!!
  }

  @SuppressLint("SourceLockedOrientationActivity")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
      this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
    } else {
      this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
    }

    try {
      setContentView(buildAdyenWebView(url))
    } catch (e: Throwable) {
      logger.logError("adyen", e)
      setResult(WRONG_URL)
      finish()
    }
  }

  @Suppress("DEPRECATION")
  @Deprecated("Deprecated in Java")
  override fun onBackPressed() {
    setResult(RESULT_CANCELED)
    super.onBackPressed()
  }

  private fun handleRedirectResult(uri: Uri) {
    RedirectComponent.PROVIDER.get(
      this,
      application,
      Payments.redirectConfiguration
    ).run {
      removeObservers(this@AdyenRedirectActivity)
      observe(this@AdyenRedirectActivity) {
        setResult(
          RESULT_OK,
          Intent().putExtra(AdyenActionResolveContract.ACTION_COMPONENT_DATA, it)
        )
        finish()
      }
      handleIntent(Intent("", uri))
    }
  }

  @SuppressLint("SetJavaScriptEnabled")
  private fun buildAdyenWebView(url: String): View = WebView(this).apply {
    layoutParams = LayoutParams(
      LayoutParams.MATCH_PARENT,
      LayoutParams.MATCH_PARENT
    )

    webViewClient = object : WebViewClient() {
      override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?,
      ): Boolean {
        return request?.url
          ?.takeIf { it.scheme == AdyenActionResolveContract.ADYEN_CHECKOUT_SCHEME }
          ?.let {
            handleRedirectResult(it)
            true
          } ?: super.shouldOverrideUrlLoading(view, request)
      }
    }

    settings.apply {
      javaScriptEnabled = true
      domStorageEnabled = true
      useWideViewPort = true
    }

    loadUrl(url)
  }
}
