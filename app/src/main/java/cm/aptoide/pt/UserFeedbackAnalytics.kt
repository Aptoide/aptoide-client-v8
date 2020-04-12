package cm.aptoide.pt

import cm.aptoide.analytics.AnalyticsManager
import cm.aptoide.analytics.implementation.navigation.NavigationTracker
import java.util.*

class UserFeedbackAnalytics(val analyticsManager: AnalyticsManager,
                            val navigationTracker: NavigationTracker) {

  companion object {
    @JvmField
    var USER_FEEDBACK_EVENT_NAME: String = "user_feedback"
    var TYPE = "type"
    var CONTEXT = "context"
    var COMMENT_LEVEL_1_TYPE = "comment_level_1"
    var COMMENT_LEVEL_2_TYPE = "comment_level_2"
    var RATE_TYPE = "rate"
    var VOTE_UP_TYPE = "vote_up"
    var VOTE_DOWN_TYPE = "vote_down"
    var REACTION_TYPE = "reaction"
    var CONTEXT_APP = "app"
    var CONTEXT_STORES = "stores"
    var CONTEXT_EDITORIAL = "editorial"
  }

  fun sendSubmitAppRateEvent(hasComment: Boolean) {
    sendRatingEvent(CONTEXT_APP)
    if (hasComment) {
      sendFirstLevelCommentEvent(CONTEXT_APP)
    }
  }

  private fun sendFirstLevelCommentEvent(context: String) {
    val map = HashMap<String, Any>()
    map[TYPE] = COMMENT_LEVEL_1_TYPE
    map[CONTEXT] = context
    analyticsManager.logEvent(map, USER_FEEDBACK_EVENT_NAME, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true))
  }

  private fun sendRatingEvent(context: String) {
    val map = HashMap<String, Any>()
    map[TYPE] = RATE_TYPE
    map[CONTEXT] = context
    analyticsManager.logEvent(map, USER_FEEDBACK_EVENT_NAME, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true))
  }

}