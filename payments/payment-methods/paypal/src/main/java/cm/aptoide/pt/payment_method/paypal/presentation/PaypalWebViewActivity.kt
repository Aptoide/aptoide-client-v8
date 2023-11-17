package cm.aptoide.pt.payment_method.paypal.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cm.aptoide.pt.payment_method.paypal.BuildConfig
import cm.aptoide.pt.payment_method.paypal.Constants
import com.google.accompanist.web.AccompanistWebChromeClient
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState

internal class PaypalWebViewActivity : AppCompatActivity() {
  companion object {
    internal const val EXTRA_URL = "${BuildConfig.LIBRARY_PACKAGE_NAME}.EXTRA_URL"
    internal const val FAIL = 100
  }

  private val url by lazy { intent.getStringExtra(EXTRA_URL) ?: "" }

  private val successScheme = Constants.PAYPAL_SUCCESS_SCHEMA
  private val cancelScheme = Constants.PAYPAL_ERROR_SCHEMA

  init {
    CookieManager.getInstance().setAcceptCookie(true)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      AccompanishWebClient(
        modifier = Modifier.fillMaxSize(),
        url = url,
        finish = ::finish
      )
    }
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
        setResult(FAIL, resultIntent)
        finish()
        true
      }

      else -> false
    }
  }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun AccompanishWebClient(
  modifier: Modifier = Modifier,
  url: String,
  finish: (Uri) -> Boolean,
) {
  val webViewState = rememberWebViewState(url = url)

  WebView(
    modifier = modifier,
    state = webViewState,
    captureBackPresses = true,
    client = object : AccompanistWebViewClient() {

      override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?,
      ): Boolean {
        return request?.url?.let {
          finish(it)
        } ?: super.shouldOverrideUrlLoading(view, request)
      }
    },
    chromeClient = AccompanistWebChromeClient(),
    onCreated = { it: WebView ->
      it.settings.javaScriptEnabled = true
      it.settings.domStorageEnabled = true
      it.settings.useWideViewPort = true
    }
  )
}
