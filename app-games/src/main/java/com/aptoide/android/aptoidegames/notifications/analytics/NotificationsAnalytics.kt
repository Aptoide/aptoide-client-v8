package com.aptoide.android.aptoidegames.notifications.analytics

import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import javax.inject.Inject

class NotificationsAnalytics @Inject constructor(
  private val genericAnalytics: GenericAnalytics,
) {

  fun sendExperimentNotificationsAllowed() = genericAnalytics.logEvent(
    name = "experiment_notifications_allowed",
    params = emptyMap()
  )

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

  fun sendNotificationClicked(notificationTag: String, notificationPackage: String?) {
    genericAnalytics.logEvent(
      name = "notification_clicked",
      params = mapOf(
        NOTIFICATION_TAG to notificationTag,
        NOTIFICATION_PACKAGE to (notificationPackage ?: "n-a")
      )
    )
  }

  fun sendNotificationReceived(notificationTag: String, notificationPackage: String? = null) {
    genericAnalytics.logEvent(
      name = "notification_received",
      params = mapOf(
        NOTIFICATION_TAG to notificationTag,
        NOTIFICATION_PACKAGE to (notificationPackage ?: "n-a")
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

  fun sendEditorsChoiceNotificationShown() {
    genericAnalytics.logEvent(
      name = "experiment6_notification_impression",
      params = emptyMap()
    )
  }

  fun sendEditorsChoiceNotificationClick() {
    genericAnalytics.logEvent(
      name = "experiment6_notification_click",
      params = emptyMap()
    )
  }

  companion object {
    internal const val NOTIFICATION_ACTION = "action"
    internal const val NOTIFICATION_ACTION_CLICK = "click"
    internal const val NOTIFICATION_TAG = "tag"
    internal const val NOTIFICATION_PACKAGE = "package"
  }
}
