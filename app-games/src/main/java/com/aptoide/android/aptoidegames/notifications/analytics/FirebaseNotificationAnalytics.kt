package com.aptoide.android.aptoidegames.notifications.analytics

import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import com.aptoide.android.aptoidegames.analytics.mapOfNonNull
import com.aptoide.android.aptoidegames.notifications.FirebaseNotificationAnalyticsInfo

class FirebaseNotificationAnalytics(
  private val biAnalytics: BIAnalytics,
) {

  fun sendNotificationReceived(
    notificationAnalyticsInfo: FirebaseNotificationAnalyticsInfo,
    hasNotificationPermissions: Boolean,
  ) = with(notificationAnalyticsInfo) {
    biAnalytics.logEvent(
      name = "ag_notification_receive",
      mapOfNonNull(
        P_MESSAGE_ID to messageId,
        P_MESSAGE_NAME to messageName,
        P_MESSAGE_DEVICE_TIME to messageDeviceTime,
        P_LABEL to label,
        P_STATUS to if (hasNotificationPermissions) "impression" else "no_permission",
      )
    )
  }

  fun sendNotificationOpened(notificationAnalyticsInfo: FirebaseNotificationAnalyticsInfo) =
    with(notificationAnalyticsInfo) {
      biAnalytics.logEvent(
        name = "ag_notification_open",
        mapOfNonNull(
          P_MESSAGE_ID to messageId,
          P_MESSAGE_NAME to messageName,
          P_MESSAGE_DEVICE_TIME to messageDeviceTime,
          P_LABEL to label,
        )
      )
    }

  companion object {
    private const val P_MESSAGE_ID = "message_id"
    private const val P_MESSAGE_NAME = "message_name"
    private const val P_MESSAGE_DEVICE_TIME = "message_device_time"
    private const val P_LABEL = "label"
    private const val P_STATUS = "status"
  }
}
