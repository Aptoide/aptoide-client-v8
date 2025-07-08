package com.aptoide.android.aptoidegames.notifications.analytics

import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import javax.inject.Inject

class NotificationsAnalytics @Inject constructor(
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

  fun sendNotificationOpened(notificationTag: String, notificationPackage: String?) {
    genericAnalytics.logEvent(
      name = "notification_event",
      params = mapOf(
        NOTIFICATION_ACTION to NOTIFICATION_ACTION_CLICK,
        NOTIFICATION_TAG to notificationTag,
        NOTIFICATION_PACKAGE to (notificationPackage ?: "n-a")
      )
    )
  }

  fun sendNotificationImpression(notificationTag: String, notificationPackage: String) {
    genericAnalytics.logEvent(
      name = "notification_event",
      params = mapOf(
        NOTIFICATION_ACTION to NOTIFICATION_ACTION_IMPRESSION,
        NOTIFICATION_TAG to notificationTag,
        NOTIFICATION_PACKAGE to notificationPackage
      )
    )
  }

  fun sendExperimentNotificationClick(notificationTag: String, notificationPackage: String?) {
    genericAnalytics.logEvent(
      name = "experiment3_notification_click",
      params = mapOf(
        NOTIFICATION_ACTION to NOTIFICATION_ACTION_CLICK,
        NOTIFICATION_TAG to notificationTag,
        NOTIFICATION_PACKAGE to (notificationPackage ?: "n-a")
      )
    )
  }

  fun sendRobloxNofiticationShown() {
    genericAnalytics.logEvent(
      name = "roblox_notification_shown",
      params = emptyMap()
    )
  }

  fun sendRobloxNotificationClick() {
    genericAnalytics.logEvent(
      name = "roblox_notification_clicked",
      params = emptyMap()
    )
  }

  fun sendCompanionAppNotificationOptIn() {
    genericAnalytics.logEvent(
      name = "roblox_companion_apps_participate",
      params = emptyMap()
    )
  }

  companion object {
    internal const val NOTIFICATION_ACTION = "action"
    internal const val NOTIFICATION_ACTION_CLICK = "click"
    internal const val NOTIFICATION_ACTION_IMPRESSION = "impression"
    internal const val NOTIFICATION_TAG = "tag"
    internal const val NOTIFICATION_PACKAGE = "package"
  }
}
