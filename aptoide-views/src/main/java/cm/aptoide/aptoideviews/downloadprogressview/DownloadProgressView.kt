package cm.aptoide.aptoideviews.downloadprogressview

import android.animation.LayoutTransition
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.CheckResult
import androidx.annotation.VisibleForTesting
import cm.aptoide.aptoideviews.R
import cm.aptoide.aptoideviews.common.Debouncer
import kotlinx.android.synthetic.main.download_progress_view.view.*
import rx.Observable
import kotlin.math.max
import kotlin.math.min

/**
 * This view is responsible for handling the display of download progress.
 *
 */
class DownloadProgressView : FrameLayout {
  private var isPausable: Boolean = true
  private var payload: Any? = null
  private var eventListener: DownloadEventListener? = null

  private var debouncer = Debouncer(750)

  private var currentProgress: Int = 0

  private var animationsEnabled = false

  private var downloadingText: String = ""
  private var pausedText: String = ""
  private var installingText: String = ""

  private var stateMachine = StateMachine.create<State, Event, Any> {
    initialState(State.Queue)
    state<State.Queue> {
      onEnter {
        Log.d("DownloadProgressView", "State.Queue")
        resetProgress()
        progressBar.isIndeterminate = true
        cancelButton.visibility = View.INVISIBLE
        resumePauseButton.visibility = View.GONE
        downloadProgressNumber.visibility = View.GONE
        downloadState.setText(downloadingText)
      }
      on<Event.DownloadStart> {
        debouncer.reset()
        transitionTo(State.InProgress)
      }
      on<Event.PauseStart> {
        debouncer.reset()
        transitionTo(State.InitialPaused)
      }
      on<Event.Reset> {
        dontTransition()
      }
      on<Event.CancelClick> {
        eventListener?.onActionClick(
            DownloadEventListener.Action(DownloadEventListener.Action.Type.CANCEL, payload))
        transitionTo(State.Canceled)
      }
      on<Event.InstallStart> {
        transitionTo(State.Installing)
      }
    }
    state<State.Canceled> {
      onEnter {
        Log.d("DownloadProgressView", "State.Canceled")
        resetProgress()
        progressBar.isIndeterminate = true
        if (isPausable) {
          cancelButton.visibility = View.VISIBLE
          resumePauseButton.visibility = View.GONE
        } else {
          cancelButton.visibility = View.VISIBLE
          resumePauseButton.visibility = View.GONE
        }
        downloadProgressNumber.visibility = View.GONE
        downloadState.setText(downloadingText)
      }
      on<Event.Reset> {
        transitionTo(State.Queue)
      }
      on<Event.DownloadStart> {
        debouncer.reset()
        transitionTo(State.InProgress)
      }
      on<Event.PauseStart> {
        debouncer.reset()
        transitionTo(State.InitialPaused)
      }
    }
    state<State.InProgress> {
      onEnter {
        Log.d("DownloadProgressView", "State.InProgress")
        setProgress(currentProgress)
        progressBar.isIndeterminate = false
        if (isPausable) {
          cancelButton.visibility = View.GONE
          resumePauseButton.visibility = View.VISIBLE
        } else {
          cancelButton.visibility = View.VISIBLE
          resumePauseButton.visibility = View.GONE
        }
        resumePauseButton.play()
        downloadProgressNumber.visibility = View.VISIBLE
        downloadState.setText(downloadingText)
      }
      on<Event.PauseClick> {
        eventListener?.onActionClick(
            DownloadEventListener.Action(DownloadEventListener.Action.Type.PAUSE, payload))
        transitionTo(State.Paused)
      }
      on<Event.PauseStart> {
        transitionTo(State.Paused)
      }
      on<Event.InstallStart> {
        transitionTo(State.Installing)
      }
      on<Event.Reset> {
        transitionTo(State.Queue)
      }
      on<Event.CancelClick> {
        eventListener?.onActionClick(
            DownloadEventListener.Action(DownloadEventListener.Action.Type.CANCEL, payload))
        transitionTo(State.Canceled)
      }
    }
    state<State.Paused> {
      onEnter {
        Log.d("DownloadProgressView", "State.Paused")
        progressBar.isIndeterminate = false
        cancelButton.visibility = View.VISIBLE
        resumePauseButton.visibility = View.VISIBLE
        resumePauseButton.playReverse()
        downloadProgressNumber.visibility = View.VISIBLE
        downloadState.setText(pausedText)
      }
      on<Event.ResumeClick> {
        eventListener?.onActionClick(
            DownloadEventListener.Action(DownloadEventListener.Action.Type.RESUME, payload))
        transitionTo(State.InProgress)
      }
      on<Event.CancelClick> {
        eventListener?.onActionClick(
            DownloadEventListener.Action(DownloadEventListener.Action.Type.CANCEL, payload))
        transitionTo(State.Canceled)
      }
      on<Event.Reset> {
        transitionTo(State.Queue)
      }
    }
    state<State.InitialPaused> {
      onEnter {
        Log.d("DownloadProgressView", "State.InitialPaused")
        progressBar.isIndeterminate = false
        cancelButton.visibility = View.VISIBLE
        resumePauseButton.visibility = View.VISIBLE
        resumePauseButton.setReverseAsDefault()
        downloadProgressNumber.visibility = View.VISIBLE
        setProgress(currentProgress)
        downloadState.setText(pausedText)
      }
      on<Event.ResumeClick> {
        eventListener?.onActionClick(
            DownloadEventListener.Action(DownloadEventListener.Action.Type.RESUME, payload))
        transitionTo(State.InProgress)
      }
      on<Event.CancelClick> {
        eventListener?.onActionClick(
            DownloadEventListener.Action(DownloadEventListener.Action.Type.CANCEL, payload))
        transitionTo(State.Canceled)
      }
      on<Event.Reset> {
        transitionTo(State.Queue)
      }
    }
    state<State.Installing> {
      onEnter {
        Log.d("DownloadProgressView", "State.Installing")
        progressBar.isIndeterminate = true
        cancelButton.visibility = View.INVISIBLE
        resumePauseButton.visibility = View.GONE
        downloadProgressNumber.visibility = View.GONE
        downloadState.setText(installingText)
      }
      on<Event.Reset> {
        transitionTo(State.Queue)
      }
    }
  }

  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
      defStyleAttr) {
    inflate(context, R.layout.download_progress_view, this)
    retrievePreferences(attrs, defStyleAttr)
  }

  private fun retrievePreferences(attrs: AttributeSet?, defStyleAttr: Int) {
    val typedArray =
        context.obtainStyledAttributes(attrs, R.styleable.DownloadProgressView, defStyleAttr, 0)

    setProgressDrawable(typedArray.getDrawable(R.styleable.DownloadProgressView_progressDrawable))
    setEnableAnimations(
        typedArray.getBoolean(R.styleable.DownloadProgressView_enableAnimations, true))
    isPausable = typedArray.getBoolean(R.styleable.DownloadProgressView_isPausable, true)
    downloadingText =
        typedArray.getString(R.styleable.DownloadProgressView_downloadingText) ?: context.getString(
            R.string.appview_short_downloading)
    pausedText =
        typedArray.getString(R.styleable.DownloadProgressView_pausedText) ?: context.getString(
            R.string.apps_short_download_paused)
    installingText =
        typedArray.getString(R.styleable.DownloadProgressView_installingText) ?: context.getString(
            R.string.apps_short_installing)
    typedArray.recycle()
  }

  private fun setupClickListeners() {
    cancelButton.setOnClickListener {
      debouncer.execute {
        stateMachine.transition(Event.CancelClick)
      }
    }
    resumePauseButton.setOnClickListener {
      debouncer.execute {
        if (isPausable) {
          if (stateMachine.state == State.InProgress)
            stateMachine.transition(Event.PauseClick)
          else
            stateMachine.transition(Event.ResumeClick)
        }
      }
    }
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    cancelButton.setOnClickListener(null)
    resumePauseButton.setOnClickListener(null)
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    setupClickListeners()
  }

  /**
   * Should only be used if for some reason you can't use [events] directly.
   */
  fun setEventListener(eventListener: DownloadEventListener?) {
    this.eventListener = eventListener
    if (eventListener == null) {
      cancelButton.setOnClickListener(null)
      resumePauseButton.setOnClickListener(null)
    }
  }

  private fun resetProgress() {
    currentProgress = 0
    progressBar.progress = currentProgress
    val progressPercent = "$currentProgress%"
    downloadProgressNumber.text = progressPercent
  }

  @VisibleForTesting(otherwise = VisibleForTesting.NONE)
  internal fun setDebounceTime(time: Long) {
    debouncer = Debouncer(time)
  }

  /**
   * Sets if animations should be enabled
   * @param enableAnimations true to enable animations, false to disable animations
   */
  fun setEnableAnimations(enableAnimations: Boolean) {
    animationsEnabled = enableAnimations
    resumePauseButton.isAnimationsEnabled = enableAnimations
    rootLayout.layoutTransition = if (enableAnimations) LayoutTransition() else null
  }

  /**
   * Sets a specific drawable for progress
   * @param progressDrawable Progress drawable
   */
  fun setProgressDrawable(progressDrawable: Drawable?) {
    progressDrawable?.let { drawable ->
      progressBar.progressDrawable = drawable
    }
  }

  /**
   * Retrieves the events stream for this view.
   *
   * @return Observable<DownloadEventListener.Action>
   */
  @CheckResult
  fun events(): Observable<DownloadEventListener.Action> {
    return Observable.create(DownloadProgressViewEventOnSubscribe(this))
  }

  /**
   * Sets an optional payload to be retrieved with the event listener
   * E.g. Attaching an object identifying/describing the download
   *
   * @param payload
   */
  fun setPayload(payload: Any?) {
    this.payload = payload
  }

  /**
   * Sets the download progress. Note that it clips to 0-100.
   * If the view is in a paused state, it caches the value and sets it when it's in the resume state.
   *
   * @param progress, 0-100
   */
  fun setProgress(progress: Int) {
    if (stateMachine.state == State.Queue || stateMachine.state == State.Canceled) return
    currentProgress = min(max(progress, 0), 100)
    if (stateMachine.state == State.InProgress || stateMachine.state == State.InitialPaused) {
      if (Build.VERSION.SDK_INT >= 24) {
        progressBar.setProgress(currentProgress, animationsEnabled)
      } else {
        progressBar.progress = currentProgress
      }
      val progressPercent = "$currentProgress%"
      downloadProgressNumber.text = progressPercent
    }
  }

  /**
   * Notifies the view that downloading will now begin.
   * It changes the view to a InProgress state.
   */
  fun startDownload() {
    stateMachine.transition(Event.DownloadStart)
  }

  /**
   * Notifies the view that the download is paused.
   * It truly only does something if the download state is initially paused.
   */
  fun pauseDownload() {
    stateMachine.transition(Event.PauseStart)
  }

  /**
   * Notifies the view that installation will now begin. This implies that the download has ended.
   * It changes the view to an Canceled state.
   */
  fun startInstallation() {
    stateMachine.transition(Event.InstallStart)
  }


  /**
   * Notifies the view to reset the view. Use this after installing an app.
   */
  fun reset() {
    stateMachine.transition(Event.Reset)
  }

}