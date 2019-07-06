package cm.aptoide.aptoideviews

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.runner.AndroidJUnit4
import cm.aptoide.aptoideviews.base.BaseTestView
import cm.aptoide.aptoideviews.downloadprogressview.DownloadEventListener
import cm.aptoide.aptoideviews.downloadprogressview.DownloadProgressView
import cm.aptoide.aptoideviews.matchers.ProgressIndeterminate.Companion.withIndeterminate
import cm.aptoide.aptoideviews.viewactions.ProgressBarTestAction.Companion.replaceProgressBarDrawable
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
    onView(progressBar).perform(replaceProgressBarDrawable())
  }

  @Test
  fun testPauseInput_fromInProgressState() {
    activityRule.runOnUiThread {
      downloadProgressView.reset()
      downloadProgressView.startDownload()
    }
    onView(resumePauseButton).perform(click())

    verify(eventListener).onActionClick(
        DownloadEventListener.Action(DownloadEventListener.Action.Type.PAUSE, null))
    onView(progressBar).check(matches(withIndeterminate(false)))
    onView(cancelButton).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    onView(resumePauseButton).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    onView(progressNumber).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    onView(downloadStateText).check(matches(withText(R.string.appview_short_downloading)))
  }

  @Test
  fun testResumeInput_fromPausedState() {
    activityRule.runOnUiThread {
      downloadProgressView.reset()
      downloadProgressView.startDownload()
    }
    onView(resumePauseButton).perform(click()) // Pause
    onView(resumePauseButton).perform(click()) // Resume

    verify(eventListener).onActionClick(
        DownloadEventListener.Action(DownloadEventListener.Action.Type.RESUME, null))
    onView(progressBar).check(matches(withIndeterminate(false)))
    onView(cancelButton).check(matches(withEffectiveVisibility(Visibility.GONE)))
    onView(resumePauseButton).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    onView(progressNumber).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    onView(downloadStateText).check(matches(withText(R.string.appview_short_downloading)))
  }


  @Test
  fun testCancelInput_fromPausedState() {
    activityRule.runOnUiThread {
      downloadProgressView.reset()
      downloadProgressView.startDownload()
    }
    onView(resumePauseButton).perform(click())
    onView(cancelButton).perform(click())

    verify(eventListener).onActionClick(
        DownloadEventListener.Action(DownloadEventListener.Action.Type.CANCEL, null))
    onView(progressBar).check(matches(withIndeterminate(true)))
    onView(cancelButton).check(matches(withEffectiveVisibility(Visibility.GONE)))
    onView(resumePauseButton).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    onView(progressNumber).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    onView(downloadStateText).check(matches(withText(R.string.appview_short_downloading)))
  }

  @Test
  fun testDownloadStartInput_fromIndeterminateState() {
    activityRule.runOnUiThread {
      downloadProgressView.reset()
      downloadProgressView.startDownload()
    }

    onView(progressBar).check(matches(withIndeterminate(false)))
    onView(cancelButton).check(matches(withEffectiveVisibility(Visibility.GONE)))
    onView(resumePauseButton).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    onView(progressNumber).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    onView(downloadStateText).check(matches(withText(R.string.appview_short_downloading)))
  }

  @Test
  fun testInstallStartInput_fromInProgressState() {
    activityRule.runOnUiThread {
      downloadProgressView.reset()
      downloadProgressView.startDownload()
      downloadProgressView.startInstallation()
    }
    onView(progressBar).check(matches(withIndeterminate(true)))
    onView(cancelButton).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
    onView(resumePauseButton).check(matches(withEffectiveVisibility(Visibility.GONE)))
    onView(progressNumber).check(matches(withEffectiveVisibility(Visibility.GONE)))
    onView(downloadStateText).check(matches(withText(R.string.appview_short_installing)))
  }

  @Test
  fun testResetInput_fromInstallingState() {
    activityRule.runOnUiThread {
      downloadProgressView.reset()
      downloadProgressView.startDownload()
      downloadProgressView.startInstallation()
      downloadProgressView.reset()
    }

    onView(progressBar).check(matches(withIndeterminate(true)))
    onView(cancelButton).check(matches(withEffectiveVisibility(Visibility.GONE)))
    onView(resumePauseButton).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    onView(progressNumber).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    onView(downloadStateText).check(matches(withText(R.string.appview_short_downloading)))
  }

  @Test
  fun testInitialPausedState() {
    activityRule.runOnUiThread {
      downloadProgressView.reset()
      downloadProgressView.pauseDownload()
    }

    onView(progressBar).check(matches(withIndeterminate(false)))
    onView(cancelButton).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    onView(resumePauseButton).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    onView(progressNumber).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    onView(downloadStateText).check(matches(withText(R.string.appview_short_downloading)))
  }

  @Test
  fun testSetProgress_fromInProgressState() {
    activityRule.runOnUiThread {
      downloadProgressView.reset()
      downloadProgressView.startDownload()
      downloadProgressView.setProgress(37)
    }

    onView(progressNumber).check(matches(withText("37%")))
  }

  @Test
  fun testSetProgress_fromIndeterminateState() {
    activityRule.runOnUiThread {
      downloadProgressView.reset()
      downloadProgressView.setProgress(37)
    }
    onView(progressNumber).check(matches(withText("0%")))

    activityRule.runOnUiThread { downloadProgressView.startDownload() }
    onView(progressNumber).check(matches(withText("0%")))
  }

  @Test
  fun testSetProgress_fromPausedState() {
    activityRule.runOnUiThread {
      downloadProgressView.reset()
      downloadProgressView.startDownload()
    }
    onView(resumePauseButton).perform(click()) // Pause
    activityRule.runOnUiThread { downloadProgressView.setProgress(37) }

    onView(progressNumber).check(matches(withText("0%")))

    onView(resumePauseButton).perform(click()) // Resume
    onView(progressNumber).check(matches(withText("37%")))
  }

  @Test
  fun testSetProgress_fromInitialPausedState() {
    activityRule.runOnUiThread {
      downloadProgressView.reset()
      downloadProgressView.pauseDownload()
      downloadProgressView.setProgress(37)
    }

    onView(progressNumber).check(matches(withText("37%")))
  }

}