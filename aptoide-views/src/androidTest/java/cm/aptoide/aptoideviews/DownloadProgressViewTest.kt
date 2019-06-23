package cm.aptoide.aptoideviews

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.runner.AndroidJUnit4
import android.widget.ProgressBar
import cm.aptoide.aptoideviews.base.BaseTestView
import cm.aptoide.aptoideviews.downloadprogressview.DownloadProgressView
import cm.aptoide.aptoideviews.downloadprogressview.DownloadEventListener
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

  private val pauseButton = withId(R.id.pause_button)
  private val resumeButton = withId(R.id.resumePauseButton)
  private val cancelButton = withId(R.id.cancelButton)
  private val progressBar = withId(R.id.progressBar)
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
    setView(downloadProgressView)
    onView(isAssignableFrom(ProgressBar::class.java)).perform(replaceProgressBarDrawable())
  }

  // We don't use @OnUiThread because it seems to hang the espresso calls forever
  @Test
  fun testPauseInput() {
    activityRule.runOnUiThread { downloadProgressView.setState(ProgressState.IN_PROGRESS) }
    onView(pauseButton).perform(click())

    verify(eventListener).onActionClick(DownloadEventListener.Action(DownloadEventListener.Action.Type.PAUSE, null))
  }

  @Test
  fun testResumeInput() {
    activityRule.runOnUiThread { downloadProgressView.setState(ProgressState.PAUSED) }
    onView(resumeButton).perform(click())

    verify(eventListener).onActionClick(
        DownloadEventListener.Action(DownloadEventListener.Action.Type.RESUME, null))
  }

  @Test
  fun testCancelInput() {
    activityRule.runOnUiThread { downloadProgressView.setState(ProgressState.PAUSED) }
    onView(cancelButton).perform(click())

    verify(eventListener).onActionClick(
        DownloadEventListener.Action(DownloadEventListener.Action.Type.CANCEL, null))
  }


  @Test
  fun testPausedState() {
    activityRule.runOnUiThread { downloadProgressView.setState(ProgressState.PAUSED) }

    onView(pauseButton).check(matches((withEffectiveVisibility(Visibility.GONE))))
    onView(resumeButton).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    onView(cancelButton).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    onView(progressBar).check(matches(withIndeterminate(false)))
    onView(downloadStateText).check(matches(withText(R.string.appview_short_downloading)))
  }

  @Test
  fun testInProgressState() {
    activityRule.runOnUiThread { downloadProgressView.setState(ProgressState.IN_PROGRESS) }

    onView(pauseButton).check(matches((withEffectiveVisibility(Visibility.VISIBLE))))
    onView(resumeButton).check(matches(withEffectiveVisibility(Visibility.GONE)))
    onView(cancelButton).check(matches(withEffectiveVisibility(Visibility.GONE)))
    onView(progressBar).check(matches(withIndeterminate(false)))
    onView(downloadStateText).check(matches(withText(R.string.appview_short_downloading)))
  }

  @Test
  fun testIndeterminateState() {
    activityRule.runOnUiThread { downloadProgressView.setState(ProgressState.INDETERMINATE) }

    onView(pauseButton).check(matches((withEffectiveVisibility(Visibility.VISIBLE))))
    onView(resumeButton).check(matches(withEffectiveVisibility(Visibility.GONE)))
    onView(cancelButton).check(matches(withEffectiveVisibility(Visibility.GONE)))
    onView(progressBar).check(matches(withIndeterminate(true)))
    onView(downloadStateText).check(matches(withText(R.string.appview_short_downloading)))
  }

  @Test
  fun testCompleteState() {
    activityRule.runOnUiThread { downloadProgressView.setState(ProgressState.COMPLETE) }

    onView(pauseButton).check(matches((withEffectiveVisibility(Visibility.VISIBLE))))
    onView(resumeButton).check(matches(withEffectiveVisibility(Visibility.GONE)))
    onView(cancelButton).check(matches(withEffectiveVisibility(Visibility.GONE)))
    onView(progressBar).check(matches(withIndeterminate(true)))
    onView(downloadStateText).check(matches(withText(R.string.appview_short_downloading)))
  }

  @Test
  fun testInstallingState() {
    activityRule.runOnUiThread {
      downloadProgressView.setState(ProgressState.INSTALLING)
    }

    onView(pauseButton).check(matches((withEffectiveVisibility(Visibility.GONE))))
    onView(resumeButton).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    onView(cancelButton).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    onView(progressBar).check(matches(withIndeterminate(false)))
    onView(downloadStateText).check(matches(withText(R.string.appview_short_installing)))
  }

}