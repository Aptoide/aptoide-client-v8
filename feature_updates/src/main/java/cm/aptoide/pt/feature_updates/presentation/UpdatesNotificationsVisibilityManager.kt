package cm.aptoide.pt.feature_updates.presentation

import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import cm.aptoide.pt.feature_updates.domain.NotificationTypes
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdatesNotificationsVisibilityManager @Inject constructor(
  private val featureFlags: FeatureFlags,
) {

  private var cachedAllowedNotificationTags: MutableSet<NotificationTypes> = mutableSetOf()

  suspend fun shouldShowNotification(type: NotificationTypes): Boolean {
    if (cachedAllowedNotificationTags.isEmpty()) {
      calculateAllowedNotifications()
    }
    return cachedAllowedNotificationTags.contains(type)
  }

  private suspend fun calculateAllowedNotifications() {
    val variant = featureFlags.getFlagAsString("updates_notification_type")
    when (variant) {
      "baseline" -> cachedAllowedNotificationTags.addAll(
        listOf(
          NotificationTypes.GENERAL_NOTIFICATION,
          NotificationTypes.VIP_NOTIFICATION
        )
      )

      "generic_only" -> cachedAllowedNotificationTags.add(NotificationTypes.GENERAL_NOTIFICATION)
      "auto_update_off" -> cachedAllowedNotificationTags.add(NotificationTypes.VIP_NOTIFICATION)
      "auto_update_on" -> cachedAllowedNotificationTags.add(NotificationTypes.AUTO_UPDATE_SUCCESSFUL)

      else -> cachedAllowedNotificationTags.addAll(
        listOf(
          NotificationTypes.GENERAL_NOTIFICATION,
          NotificationTypes.VIP_NOTIFICATION
        )
      )
    }
  }

  suspend fun shouldAutoUpdateGames(): Boolean {
    val variant = featureFlags.getFlagAsString("updates_notification_type")
    return variant != "auto_update_off" && variant != "generic_only"
  }
}
