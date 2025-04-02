package cm.aptoide.pt

import cm.aptoide.analytics.AnalyticsManager

public class FirebaseNotificationAnalytics(private val analyticsManager: AnalyticsManager) {

  public fun sendFirebaseNotificationReceived(
    messageId: String,
    messageName: String,
    messageDeviceTime: Long,
    label: String?,
    hasNotificationPermissions: Boolean
  ) {
    analyticsManager.logEvent(
      mapOfNonNull(
        P_MESSAGE_ID to messageId,
        P_MESSAGE_NAME to messageName,
        P_MESSAGE_DEVICE_TIME to messageDeviceTime,
        P_LABEL to label,
        P_STATUS to if (hasNotificationPermissions) "impression" else "no_permission",
      ),
      NOTIFICATION_RECEIVE, AnalyticsManager.Action.AUTO,
      "notification"
    )
  }

  fun sendFirebaseNotificationOpened(
    messageId: String,
    messageName: String,
    messageDeviceTime: Long,
    label: String?
  ) {
    analyticsManager.logEvent(
      mapOfNonNull(
        P_MESSAGE_ID to messageId,
        P_MESSAGE_NAME to messageName,
        P_MESSAGE_DEVICE_TIME to messageDeviceTime,
        P_LABEL to label,
      ),
      NOTIFICATION_OPEN, AnalyticsManager.Action.AUTO,
      "notification"
    )
  }

  fun <K, V : Any> mapOfNonNull(vararg pairs: Pair<K, V?>) = mapOf(*pairs)
    .filterValues { it != null }
    .mapValues { it.value as V }

  companion object {
    private const val P_MESSAGE_ID = "message_id"
    private const val P_MESSAGE_NAME = "message_name"
    private const val P_MESSAGE_DEVICE_TIME = "message_device_time"
    private const val P_LABEL = "label"
    private const val P_STATUS = "status"
    const val NOTIFICATION_RECEIVE = "vanilla_notification_receive"
    const val NOTIFICATION_OPEN = "vanilla_notification_open"
  }
}