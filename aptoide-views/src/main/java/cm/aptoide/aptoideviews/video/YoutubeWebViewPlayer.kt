package cm.aptoide.aptoideviews.video

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class YoutubeWebViewPlayer : WebView {

  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
      defStyleAttr) {
    webViewClient = YoutubeWebViewClient()
    getSettings().setJavaScriptEnabled(true)
  }

  init {
    setOnTouchListener { v, event ->
      this.evaluateJavascript(
          "document.getElementsByClassName('ytp-endscreen-content')[0].remove()", null)
      this.evaluateJavascript("document.getElementsByClassName('html5-endscreen')[0].remove()",
          null)
      this.evaluateJavascript(
          "document.getElementsByClassName('ytp-replay-button')[0].style.top=0", null)
      false
    }
  }

  private class YoutubeWebViewClient : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView?,
                                          request: WebResourceRequest?): Boolean {
      return true
    }

    override fun onPageFinished(view: WebView, url: String?) {
      view.evaluateJavascript("document.getElementsByClassName('ytp-chrome-top')[0].remove()",
          null)
      view.evaluateJavascript(
          "document.getElementsByClassName('ytp-icon-large-play-button-hover')[0].style.background=\"no-repeat url('https://cdn6.aptoide.com/includes/themes/2014/images/vanilla_appcoins_info_video_placeholder.svg')\"",
          null)
      view.evaluateJavascript(
          "document.getElementsByClassName('ytp-icon-large-play-button-hover')[0].style.width=\"75px\"",
          null)
      view.evaluateJavascript(
          "document.getElementsByClassName('ytp-icon-large-play-button-hover')[0].style.height=\"75px\"",
          null)
    }
  }

  public fun loadVideo(videoId: String, enableSubtitles: Boolean) {
    val subtitles = if (enableSubtitles) "&cc_load_policy=1" else ""
    loadUrl("https://www.youtube.com/embed/$videoId?rel=0$subtitles")
  }


}