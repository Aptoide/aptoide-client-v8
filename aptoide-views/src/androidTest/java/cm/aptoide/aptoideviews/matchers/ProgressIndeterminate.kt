package cm.aptoide.aptoideviews.matchers

import android.view.View
import android.widget.ProgressBar
import androidx.annotation.CheckResult
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description

class ProgressIndeterminate(private val isIndeterminate: Boolean) :
    BoundedMatcher<View, ProgressBar>(ProgressBar::class.java) {
  companion object {
    @CheckResult
    fun withIndeterminate(isIndeterminate: Boolean): ProgressIndeterminate {
      return ProgressIndeterminate(isIndeterminate)
    }
  }

  override fun describeTo(description: Description) {
    description.appendText("isIndeterminate: ").appendValue(isIndeterminate)
  }

  override fun matchesSafely(progressBar: ProgressBar?): Boolean {
    return progressBar?.isIndeterminate == isIndeterminate
  }

}