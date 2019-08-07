package cm.aptoide.aptoideviews.base

import android.content.Context
import android.content.Intent
import android.support.test.rule.ActivityTestRule
import android.view.View
import org.junit.Rule

abstract class BaseTestView {
  @Rule
  @JvmField
  val activityRule = ActivityTestRule<MockActivity>(
      MockActivity::class.java, true, true)

  fun getContext(): Context {
    return activityRule.activity
  }

  fun setView(view: View) {
    activityRule.runOnUiThread {
      activityRule.activity.setContentView(view)
    }
  }

  private fun restartActivity() {
    if (activityRule.activity != null) {
      activityRule.finishActivity()
    }
    activityRule.launchActivity(Intent())
  }
}