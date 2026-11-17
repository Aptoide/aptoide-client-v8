package com.aptoide.android.aptoidegames.wallet_web

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.wallet.authorization.data.WalletAuthRepository
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

const val walletWebRoute = "wallet_web"

fun walletWebScreen() = ScreenData.withAnalytics(
  route = walletWebRoute,
  screenAnalyticsName = "WalletWeb",
) { _, _, goBack ->
  WalletWebScreen(goBack = goBack)
}

/** Google CCT redirects (login-wallet-result://<applicationId>?code=…) on their way to the web. */
object WalletLoginResults {
  /** Set by openCCT so unsolicited login-wallet-result deep links are ignored. */
  @Volatile var awaitingLogin = false

  /** Held until the WebView consumes it, so it survives the tab not being composed yet. */
  val pending = MutableStateFlow<String?>(null)
}

@HiltViewModel
class WalletWebViewModel @Inject constructor(
  private val walletAuthRepository: WalletAuthRepository,
) : ViewModel() {

  fun onLogin(json: String) {
    val data = runCatching { JSONObject(json) }.getOrNull() ?: return
    val address = data.optString("address").ifBlank { return }
    val authToken = data.optString("authToken").ifBlank { return }
    val refreshToken = data.optString("refreshToken").ifBlank { return }
    // NonCancellable: leaving the tab right after login must not drop a half-written session.
    viewModelScope.launch(NonCancellable) {
      walletAuthRepository.saveSession(address, authToken, refreshToken)
    }
  }

  fun onLogout() {
    viewModelScope.launch { walletAuthRepository.clearAuthorizationData() }
  }
}

@SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
@Composable
fun WalletWebScreen(
  goBack: () -> Unit,
  viewModel: WalletWebViewModel = hiltViewModel(),
) {
  val webView = remember { mutableStateOf<WebView?>(null) }

  LaunchedEffect(webView.value) {
    val view = webView.value ?: return@LaunchedEffect
    WalletLoginResults.pending.filterNotNull().collect { deepLink ->
      if (view.url.isWalletUrl()) {
        view.evaluateJavascript("onLoginUpdate(${JSONObject.quote(deepLink)})", null)
      }
      WalletLoginResults.pending.value = null
    }
  }

  BackHandler {
    webView.value?.takeIf { it.canGoBack() }?.goBack() ?: goBack()
  }

  AndroidView(
    modifier = Modifier.fillMaxSize(),
    factory = { context ->
      WebView(context).apply {
        // AndroidView defaults to WRAP_CONTENT, which makes WebView report a 0px viewport height.
        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.useWideViewPort = true
        // Google refuses OAuth in user agents that look like a WebView.
        settings.userAgentString = WebSettings.getDefaultUserAgent(context)
          .replace("; wv", "")
          .replace(Regex("""\s*Version/\d+\.\d+\s*"""), " ")
          .trim()
        CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
        // Only the wallet origin may run inside this WebView (it gets the JS bridge).
        webViewClient = object : WebViewClient() {
          override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest) =
            if (request.url.toString().isWalletUrl()) {
              false
            } else {
              openExternally(context, request.url.toString())
              true
            }
        }
        addJavascriptInterface(WalletWebAppInterface(context, viewModel), "WebAppInterface")
        loadUrl(BuildConfig.WALLET_WEB_URL)
        webView.value = this
      }
    },
    onRelease = {
      it.removeJavascriptInterface("WebAppInterface")
      it.destroy()
      webView.value = null
    },
  )
}

private val walletUri = Uri.parse(BuildConfig.WALLET_WEB_URL)

private fun String?.isWalletUrl() = this?.let(Uri::parse)
  ?.let { it.scheme == walletUri.scheme && it.authority == walletUri.authority } == true

private fun openExternally(context: Context, url: String) {
  if (!url.startsWith("https://")) return
  context.startActivity(
    Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  )
}

/** Contract expected by the wallet web app (wallet-appcoins-react: WebAppInterface.d.ts). */
private class WalletWebAppInterface(
  private val context: Context,
  private val viewModel: WalletWebViewModel,
) {
  @JavascriptInterface
  fun onLogin(json: String) {
    viewModel.onLogin(json)
    CookieManager.getInstance().flush()
  }

  @JavascriptInterface
  fun onLogout() = viewModel.onLogout()

  @JavascriptInterface
  fun getHostPackage(): String = context.packageName

  @JavascriptInterface
  fun getFlavor(): String = "aptoide"

  @JavascriptInterface
  fun isCCTAvailable(): Boolean = true

  @JavascriptInterface
  fun openCCT(url: String) {
    if (url.startsWith("https://")) {
      WalletLoginResults.awaitingLogin = true
      CustomTabsIntent.Builder().build().launchUrl(context, Uri.parse(url))
    }
  }

  @JavascriptInterface
  fun openInBrowser(url: String?) = url?.let { openExternally(context, it) }

  // ponytail: Intercom support not wired in V10; add the SDK when the wallet support chat is wanted.
  @JavascriptInterface
  fun openIntercom(data: String?) = Unit
}
