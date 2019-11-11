package cm.aptoide.aptoideviews.video

import android.animation.LayoutTransition
import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import cm.aptoide.aptoideviews.R
import cm.aptoide.aptoideviews.safeLet
import kotlinx.android.synthetic.main.youtube_player.view.*

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
    webview.setOnPageFinishedAction {
      threadHandler.postDelayed({
        webview.visibility = View.VISIBLE
        progress_bar.visibility = View.GONE
        status_text.visibility = View.GONE
      }, 500)

    }
    webview.setOnErrorAction {
      status_text.visibility = View.VISIBLE
      webview.visibility = View.INVISIBLE
      progress_bar.visibility = View.GONE
    }
    status_text.setOnClickListener {
      progress_bar.visibility = View.VISIBLE
      status_text.visibility = View.GONE
      safeLet(currentVideoId, currentEnableSubtitles) { videoId, enableSubtitles ->
        webview.loadVideo(videoId, enableSubtitles)
      }
    }
  }

  fun loadVideo(videoId: String, enableSubtitles: Boolean) {
    currentVideoId = videoId
    currentEnableSubtitles = enableSubtitles
    webview.loadVideo(videoId, enableSubtitles)
  }

}