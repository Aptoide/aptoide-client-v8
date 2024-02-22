package com.appcoins.payment_method.paypal.presentation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.appcoins.payment_method.paypal.BuildConfig
import com.appcoins.payment_method.paypal.Constants

internal class PaypalWebViewActivity : Activity() {
  companion object {
    internal const val EXTRA_URL = "${BuildConfig.LIBRARY_PACKAGE_NAME}.EXTRA_URL"
  }

  private val url by lazy { intent.getStringExtra(EXTRA_URL) ?: "" }

  private val successScheme = Constants.PAYPAL_SUCCESS_SCHEMA
  private val cancelScheme = Constants.PAYPAL_ERROR_SCHEMA

  init {
    CookieManager.getInstance().setAcceptCookie(true)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(paypalWebView(this, url, ::finish))
  }

  private fun finish(uri: Uri): Boolean {
    return when (uri.scheme) {
      successScheme -> {
        val resultIntent = Intent().apply { data = uri }
        val resultCode = uri.getQueryParameter("resultCode")
        val result = if (resultCode.equals("cancelled", true)) {
          RESULT_CANCELED
        } else {
          RESULT_OK
        }
        setResult(result, resultIntent)
        finish()
        true
      }

      cancelScheme -> {
        val resultIntent = Intent().apply { data = uri }
        setResult(RESULT_CANCELED, resultIntent)
        finish()
        true
      }

      else -> false
    }
  }
}

@SuppressLint("SetJavaScriptEnabled")
private fun paypalWebView(
  context: Context,
  url: String,
  finish: (Uri) -> Boolean,
): View = WebView(context).apply {
  layoutParams = LayoutParams(
    LayoutParams.MATCH_PARENT,
    LayoutParams.MATCH_PARENT
  )

  webViewClient = object : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
      return request?.url?.let {
        finish(it)
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
