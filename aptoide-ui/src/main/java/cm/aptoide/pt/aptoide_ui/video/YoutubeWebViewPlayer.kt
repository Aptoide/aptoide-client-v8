package cm.aptoide.pt.aptoide_ui.video

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.AttributeSet
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class YoutubeWebViewPlayer : WebView {

  private val youtubeWebViewClient = YoutubeWebViewClient()

  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
    defStyleAttr) {
    webViewClient = youtubeWebViewClient
    webChromeClient = WebChromeClientWithoutPlayerPlaceholder()
    settings.javaScriptEnabled = true
  }

  init {
    setOnTouchListener { v, event ->
      if (Build.VERSION.SDK_INT >= 19) {
        val removeEndScreen =
          "document.getElementsByClassName('ytp-endscreen-content')[0].remove();"
        val removeEndScreenHtml5 = "document.getElementsByClassName('html5-endscreen')[0].remove();"
        val changeReplayPosition =
          "document.getElementsByClassName('ytp-replay-button')[0].style.top=0;"
        val removePauseOverlay =
          "document.getElementsByClassName('ytp-pause-overlay')[0].remove();"
        this.evaluateJavascript(
          removeEndScreen + removeEndScreenHtml5 + changeReplayPosition + removePauseOverlay,
          null)
      }
      false
    }
  }

  fun setOnPageFinishedAction(action: (() -> (Unit))) {
    youtubeWebViewClient.setOnPageFinishedAction(action)
  }

  fun setOnErrorAction(action: (() -> (Unit))) {
    youtubeWebViewClient.setOnError(action)
  }

  private class YoutubeWebViewClient : WebViewClient() {
    private var onPageFinishedAction: (() -> (Unit))? = null
    private var onErrorAction: (() -> (Unit))? = null

    private var hasError = false
    private var hasLoaded = false

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
      hasError = false
    }

    override fun shouldOverrideUrlLoading(
      view: WebView?,
      request: WebResourceRequest?,
    ): Boolean {
      return true
    }

    override fun onPageFinished(view: WebView, url: String?) {
      if (Build.VERSION.SDK_INT >= 19) {
        val removeTopBar = "document.getElementsByClassName('ytp-chrome-top')[0].remove();"
        val hoverBackground =
          "document.getElementsByClassName('ytp-icon-large-play-button-hover')[0].style.background=\"no-repeat url('https://cdn6.aptoide.com/includes/themes/2014/images/vanilla_appcoins_info_video_placeholder.svg')\";"
        val hoverBackgroundWidth =
          "document.getElementsByClassName('ytp-icon-large-play-button-hover')[0].style.width=\"75px\";"
        val hoverBackgroundHeight =
          "document.getElementsByClassName('ytp-icon-large-play-button-hover')[0].style.height=\"75px\";"
        view.evaluateJavascript(
          removeTopBar + hoverBackground + hoverBackgroundWidth + hoverBackgroundHeight) {
          if (!hasError) {
            hasLoaded = true
            onPageFinishedAction?.let { action -> action() }
          }
        }
      }
    }

    override fun onReceivedError(
      view: WebView?, request: WebResourceRequest?,
      error: WebResourceError?,
    ) {
      if (!hasLoaded) {
        onErrorAction?.let { action -> action() }
        hasError = true
      }
    }


    fun setOnPageFinishedAction(action: () -> (Unit)) {
      onPageFinishedAction = action
    }

    fun setOnError(action: () -> Unit) {
      onErrorAction = action

    }
  }

  fun loadVideo(videoId: String, enableSubtitles: Boolean) {
    val subtitles = if (enableSubtitles) "&cc_load_policy=1" else ""
    loadUrl("$videoId?rel=0$subtitles")
  }


}