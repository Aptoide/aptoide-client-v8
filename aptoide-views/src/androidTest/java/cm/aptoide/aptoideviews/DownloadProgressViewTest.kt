package cm.aptoide.aptoideviews

import android.support.test.annotation.UiThreadTest
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.runner.AndroidJUnit4
import android.widget.ProgressBar
import cm.aptoide.aptoideviews.base.BaseTestView
import cm.aptoide.aptoideviews.downloadprogressview.DownloadEventListener
import cm.aptoide.aptoideviews.downloadprogressview.DownloadProgressView
import cm.aptoide.aptoideviews.downloadprogressview.Event
import cm.aptoide.aptoideviews.downloadprogressview.State
import cm.aptoide.aptoideviews.viewactions.ProgressBarTestAction.Companion.replaceProgressBarDrawable
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class DownloadProgressViewTest : BaseTestView() {

  private val resumePauseButton = withId(R.id.resumePauseButton)
  private val cancelButton = withId(R.id.cancelButton)
  private val progressBar = withId(R.id.progressBar)
  private val progressNumber = withId(R.id.downloadProgressNumber)
  private val downloadStateText = withId(R.id.downloadState)

  @Mock
  lateinit var eventListener: DownloadEventListener

  private val downloadProgressView by lazy {
    DownloadProgressView(getContext())
  }

  @Before
  fun setup() {
    MockitoAnnotations.initMocks(this)
    downloadProgressView.setEventListener(eventListener)
    downloadProgressView.setDebounceTime(0)
    setView(downloadProgressView)
    onView(isAssignableFrom(ProgressBar::class.java)).perform(replaceProgressBarDrawable())
  }

  @Test
  fun testPauseInput_fromInProgressState() {
    activityRule.runOnUiThread {
      downloadProgressView.setState(State.Indeterminate)
      downloadProgressView.consumeEvent(Event.DownloadStart)
    }
    onView(resumePauseButton).perform(click())

    verify(eventListener).onActionClick(
        DownloadEventListener.Action(DownloadEventListener.Action.Type.PAUSE, null))
    assertEquals(downloadProgressView.getState(), State.Paused)
  }

  @Test
  fun testResumeInput_fromPausedState() {
    activityRule.runOnUiThread {
      downloadProgressView.setState(State.Indeterminate)
      downloadProgressView.consumeEvent(Event.DownloadStart)
      downloadProgressView.consumeEvent(Event.PauseClick)
    }
    onView(resumePauseButton).perform(click())

    verify(eventListener).onActionClick(
        DownloadEventListener.Action(DownloadEventListener.Action.Type.RESUME, null))
    assertEquals(downloadProgressView.getState(), State.InProgress)
  }


  @Test
  fun testCancelInput_fromPausedState() {
    activityRule.runOnUiThread {
      downloadProgressView.setState(State.Indeterminate)
      downloadProgressView.consumeEvent(Event.DownloadStart)
      downloadProgressView.consumeEvent(Event.PauseClick)
    }

    onView(cancelButton).perform(click())

    verify(eventListener).onActionClick(
        DownloadEventListener.Action(DownloadEventListener.Action.Type.CANCEL, null))
    assertEquals(downloadProgressView.getState(), State.Indeterminate)
  }

  @Test
  fun testDownloadStartInput_fromIndeterminateState() {
    activityRule.runOnUiThread {
      downloadProgressView.setState(State.Indeterminate)
      downloadProgressView.startDownload()
    }
    assertEquals(downloadProgressView.getState(), State.InProgress)
  }

  @Test
  fun testInstallStartInput_fromInProgressState() {
    activityRule.runOnUiThread {
      downloadProgressView.setState(State.InProgress)
      downloadProgressView.startInstallation()
    }

    assertEquals(downloadProgressView.getState(), State.Indeterminate)
  }

  @Test
  fun testSetProgress_fromInProgressState() {
    activityRule.runOnUiThread {
      downloadProgressView.resetProgress()
      downloadProgressView.setState(State.InProgress)
      downloadProgressView.setProgress(37)
    }

    onView(progressNumber).check(matches(withText("37%")))
  }

  @Test
  fun testSetProgress_fromIndeterminateState() {
    activityRule.runOnUiThread {
      downloadProgressView.resetProgress()
      downloadProgressView.setState(State.Indeterminate)
      downloadProgressView.setProgress(37)
    }

    onView(progressNumber).check(matches(withText("0%")))

    // Check if anything changes when we go back to InProgress
    activityRule.runOnUiThread { downloadProgressView.consumeEvent(Event.InstallStart) }
    onView(progressNumber).check(matches(withText("0%")))
  }

  @Test
  fun testSetProgress_fromPausedState() {
    activityRule.runOnUiThread {
      downloadProgressView.resetProgress()
      downloadProgressView.setState(State.Paused)
      downloadProgressView.setProgress(37)
    }

    onView(progressNumber).check(matches(withText("0%")))

    // Check if anything changes when we go back to InProgress
    activityRule.runOnUiThread { downloadProgressView.consumeEvent(Event.ResumeClick) }
    onView(progressNumber).check(matches(withText("37%")))
  }

}