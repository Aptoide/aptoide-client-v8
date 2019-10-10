package cm.aptoide.aptoideviews.viewactions

import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.ProgressBar
import androidx.annotation.CheckResult
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import org.hamcrest.Matcher

/**
 * This ViewAction is used so progress bars don't block the tests when it is indeterminate
 * because of its animation.
 */
class ProgressBarTestAction : ViewAction {

  companion object {
    @CheckResult
    fun replaceProgressBarDrawable(): ProgressBarTestAction {
      return ProgressBarTestAction()
    }
  }

  override fun getDescription(): String {
    return "replace the ProgressBar drawable";
  }

  override fun getConstraints(): Matcher<View> {
    return isAssignableFrom(ProgressBar::class.java)
  }

  override fun perform(uiController: UiController?, view: View?) {
    val progressBar = view as ProgressBar
    progressBar.indeterminateDrawable = ColorDrawable(-0x10000)
  }
}