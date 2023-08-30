package cm.aptoide.pt.aptoide_ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import cm.aptoide.pt.aptoide_ui.animations.animatedComposable
import cm.aptoide.pt.aptoide_ui.toolbar.NavigationTopBar
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

const val urlViewRoute = "url_view/{url}"

fun NavGraphBuilder.urlViewScreen(
  navigateBack: () -> Unit,
) = animatedComposable(urlViewRoute) {
  val urlTitle = "Web View"
  val url = it.arguments?.getString("url")!!
  UrlView(
    url = URLDecoder.decode(url, StandardCharsets.UTF_8.toString()),
    title = urlTitle,
    navigateBack = navigateBack
  )
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun UrlView(
  url: String,
  title: String,
  navigateBack: () -> Unit,
) {
  val loading = remember { mutableStateOf(false) }
  val onLoaded: (Boolean) -> Unit = { loading.value = !it }
  val webViewState = rememberWebViewState(url)
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .wrapContentHeight(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    NavigationTopBar(title, navigateBack)
    Box(contentAlignment = Alignment.Center) {
      WebView(
        modifier = Modifier.fillMaxSize(),
        state = webViewState,
        onCreated = {
          it.settings.javaScriptEnabled = true
          it.settings.apply {
            safeBrowsingEnabled = true
            javaScriptEnabled = true
            loadWithOverviewMode = true
            cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            domStorageEnabled = true
          }
        },
        client = AppWebViewClients(onLoaded)
      )
      if (loading.value) {
        CircularProgressIndicator()
      }
    }
  }
}

fun buildUrlViewRoute(url: String) = url
  .let { URLEncoder.encode(it, StandardCharsets.UTF_8.toString()) }
  .let { "url_view/$it" }

class AppWebViewClients(
  private val onLoaded: (Boolean) -> Unit,
) : AccompanistWebViewClient() {

  override fun shouldOverrideUrlLoading(
    view: WebView?,
    request: WebResourceRequest?,
  ): Boolean {
    val uri: Uri? = request?.url
    return when (uri?.scheme) {
      "http",
      "https",
      -> false

      "tel" -> {
        try {
          view?.context?.startActivity(Intent(Intent.ACTION_DIAL, uri))
        } catch (t: Throwable) {
          Timber.e(t)
          view?.let {
            Snackbar.make(
              it, "Phone app not found",
              BaseTransientBottomBar.LENGTH_SHORT
            ).show()
          }
        }
        true
      }

      else -> {
        try {
          view?.context?.startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (t: Throwable) {
          Timber.w(t)
          view?.let {
            Snackbar.make(
              it, "Handling app not found",
              BaseTransientBottomBar.LENGTH_SHORT
            ).show()
          }
        }
        true
      }
    }
  }

  override fun onPageStarted(
    view: WebView?,
    url: String?,
    favicon: Bitmap?,
  ) {
    onLoaded(false)
    super.onPageStarted(view, url, favicon)
  }

  override fun onPageFinished(
    view: WebView?,
    url: String?,
  ) {
    super.onPageFinished(view, url)
    onLoaded(true)
  }
}
