package cm.aptoide.aptoideviews.downloadprogressview

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import cm.aptoide.aptoideviews.R
import kotlinx.android.synthetic.main.download_progress_view.view.*

/**
 * This view is responsible for handling the display of download progress
 */
class DownloadProgressView : ConstraintLayout {

  private var eventListener: EventListener? = null

  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
      defStyleAttr) {
    inflate(context, R.layout.download_progress_view, this)
    setupClickListeners()
    retrievePreferences(attrs, defStyleAttr)
  }

  private fun setupClickListeners() {
    pause_button.setOnClickListener {
      eventListener?.onActionClick(EventListener.Action.PAUSE)
    }
    cancel_button.setOnClickListener {
      eventListener?.onActionClick(EventListener.Action.CANCEL)
    }
    resume_button.setOnClickListener {
      eventListener?.onActionClick(EventListener.Action.RESUME)
    }
  }

  private fun retrievePreferences(attrs: AttributeSet?, defStyleAttr: Int) {
    val typedArray =
        context.obtainStyledAttributes(attrs, R.styleable.DownloadProgressView, defStyleAttr, 0)
    val progressDrawable: Drawable? =
        typedArray.getDrawable(R.styleable.DownloadProgressView_progressDrawable)
    progressDrawable?.let { drawable ->
      progress_bar.progressDrawable = drawable
    }
    typedArray.recycle()
  }

  /**
   * Sets the event listener
   *
   * Currently this is marked as internal to force the usage of Rx bindings
   * @see DownloadProgressView.events
   *
   * @param eventListener
   * @see EventListener.Action
   */
  internal fun setEventListener(eventListener: EventListener?) {
    this.eventListener = eventListener
    if (eventListener == null) {
      pause_button.setOnClickListener(null)
      cancel_button.setOnClickListener(null)
      resume_button.setOnClickListener(null)
    }
  }

  /**
   * Sets the download progress
   * @param progress, 0-100
   */
  fun setProgress(progress: Int) {
    progress_bar.progress = progress
    val progressPercent = "$progress%"
    download_progress_number.text = progressPercent
  }

  /**
   * Sets the download state
   * @param state
   * @see ProgressState
   */
  fun setState(state: ProgressState) {
    when (state) {
      ProgressState.IN_PROGRESS -> {
        progress_bar.isIndeterminate = false
        pause_button.visibility = View.VISIBLE
        cancel_button.visibility = View.GONE
        resume_button.visibility = View.GONE
        download_state.setText(R.string.appview_short_downloading)
      }
      ProgressState.PAUSED -> {
        progress_bar.isIndeterminate = false
        pause_button.visibility = View.GONE
        cancel_button.visibility = View.VISIBLE
        resume_button.visibility = View.VISIBLE
        download_state.setText(R.string.appview_short_downloading)
      }
      ProgressState.INDETERMINATE -> {
        progress_bar.isIndeterminate = true
        pause_button.visibility = View.VISIBLE
        cancel_button.visibility = View.GONE
        resume_button.visibility = View.GONE
        download_state.setText(R.string.appview_short_downloading)
      }
      ProgressState.COMPLETE -> {
        progress_bar.isIndeterminate = true
        pause_button.visibility = View.VISIBLE
        cancel_button.visibility = View.GONE
        resume_button.visibility = View.GONE
        download_state.setText(R.string.appview_short_downloading)
      }
      ProgressState.INSTALLING -> {
        progress_bar.isIndeterminate = false
        pause_button.visibility = View.GONE
        cancel_button.visibility = View.VISIBLE
        resume_button.visibility = View.VISIBLE
        download_state.setText(R.string.appview_short_installing)
      }
    }
  }

}