package com.aptoide.android.aptoidegames.notifications.analytics

import com.aptoide.android.aptoidegames.analytics.GenericAnalytics

class NotificationsAnalytics(
  private val genericAnalytics: GenericAnalytics,
) {

  fun sendNotificationOptIn() = genericAnalytics.logEvent(
    name = "notification_opt_in",
    params = emptyMap()
  )

  fun sendNotificationOptOut() = genericAnalytics.logEvent(
    name = "notification_opt_out",
    params = emptyMap()
  )

  fun sendGetNotifiedContinueClick() = genericAnalytics.logEvent(
    name = "get_notified_continue_clicked",
    params = emptyMap()
  )
}
