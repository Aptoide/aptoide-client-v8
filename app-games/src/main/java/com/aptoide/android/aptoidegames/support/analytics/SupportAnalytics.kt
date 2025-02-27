package com.aptoide.android.aptoidegames.support.analytics

import com.aptoide.android.aptoidegames.analytics.GenericAnalytics

class SupportAnalytics(
  private val genericAnalytics: GenericAnalytics,
) {

  fun sendFeedbackSent(feedbackType: String) = genericAnalytics.logEvent(
    name = "feedback_sent",
    params = mapOf("feedback_type" to feedbackType)
  )
}
