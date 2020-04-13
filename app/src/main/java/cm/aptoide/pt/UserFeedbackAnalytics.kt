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
    var TYPE_COMMENT_LEVEL_1 = "comment_level_1"
    var TYPE_COMMENT_LEVEL_2 = "comment_level_2"
    var TYPE_RATE = "rate"
    var TYPE_VOTE_UP = "vote_up"
    var TYPE_VOTE_DOWN = "vote_down"
    var TYPE_REACTION = "reaction"
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
    sendUserFeedbackEvent(context, TYPE_COMMENT_LEVEL_1)
  }

  private fun sendRatingEvent(context: String) {
    sendUserFeedbackEvent(context, TYPE_RATE)
  }

  private fun sendUserFeedbackEvent(context: String, type: String) {
    val map = HashMap<String, Any>()
    map[TYPE] = type
    map[CONTEXT] = context
    analyticsManager.logEvent(map, USER_FEEDBACK_EVENT_NAME, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true))
  }

  fun sendStoreCommentEvent() {
    sendUserFeedbackEvent(CONTEXT_STORES, TYPE_COMMENT_LEVEL_1)
  }

  fun sendStoreCommentReplyEvent() {
    sendUserFeedbackEvent(CONTEXT_STORES, TYPE_COMMENT_LEVEL_2)
  }

  fun sendAppReviewReplyComment() {
    sendUserFeedbackEvent(CONTEXT_APP, TYPE_COMMENT_LEVEL_2)
  }

  fun sendVoteUpEvent() {
    sendUserFeedbackEvent(CONTEXT_APP, TYPE_VOTE_UP)
  }

  fun sendVoteDownEvent() {
    sendUserFeedbackEvent(CONTEXT_APP, TYPE_VOTE_DOWN)
  }

}