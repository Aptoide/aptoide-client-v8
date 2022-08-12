package cm.aptoide.pt.aptoide_ui.video

import android.animation.LayoutTransition
import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import cm.aptoide.pt.aptoide_ui.R


class YoutubePlayer : FrameLayout {

  private var currentVideoId: String? = null
  private var currentEnableSubtitles: Boolean? = null
  private val threadHandler = Handler()

  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
    defStyleAttr) {
    layoutTransition = LayoutTransition()
    inflate(context, R.layout.youtube_player, this)
    setListeners()
  }

  private fun setListeners() {
    val webview = findViewById<YoutubeWebViewPlayer>(R.id.webview)
    val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
    val statusText = findViewById<TextView>(R.id.status_text)
    webview.setOnPageFinishedAction {
      threadHandler.postDelayed({
        webview.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        statusText.visibility = View.GONE
      }, 500)

    }
    webview.setOnErrorAction {
      statusText.visibility = View.VISIBLE
      webview.visibility = View.INVISIBLE
      progressBar.visibility = View.GONE
    }
    statusText.setOnClickListener {
      progressBar.visibility = View.VISIBLE
      statusText.visibility = View.GONE
      safeLet(currentVideoId, currentEnableSubtitles) { videoId, enableSubtitles ->
        webview.loadVideo(videoId, enableSubtitles)
      }
    }
  }

  private fun <T1 : Any, T2 : Any, R : Any> safeLet(p1: T1?, p2: T2?, block: (T1, T2) -> R?): R? {
    return if (p1 != null && p2 != null) block(p1, p2) else null
  }

  fun loadVideo(videoId: String, enableSubtitles: Boolean) {
    currentVideoId = videoId
    currentEnableSubtitles = enableSubtitles
    val webview = findViewById<YoutubeWebViewPlayer>(R.id.webview)
    webview.loadVideo(videoId, enableSubtitles)
  }

}