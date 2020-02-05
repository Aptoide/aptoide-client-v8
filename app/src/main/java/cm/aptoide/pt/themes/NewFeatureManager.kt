package cm.aptoide.pt.themes

import android.content.SharedPreferences
import cm.aptoide.pt.BuildConfig
import cm.aptoide.pt.notification.AptoideNotification
import cm.aptoide.pt.notification.sync.LocalNotificationSync
import cm.aptoide.pt.notification.sync.LocalNotificationSyncManager

class NewFeatureManager(val preferences: SharedPreferences,
                        val localNotificationSyncManager: LocalNotificationSyncManager,
                        val newFeature: NewFeature) {

  companion object {
    const val HAS_SHOWN_FEATURE: String = "${BuildConfig.VERSION_CODE}_has_shown_new_feature_"
  }

  fun scheduleNotification() {
    localNotificationSyncManager.schedule(newFeature.title, newFeature.description, "",
        newFeature.actionStringRes,
        "aptoidefeature://id=${newFeature.id}&action=${newFeature.actionId}",
        LocalNotificationSync.NEW_FEATURE,
        AptoideNotification.NEW_FEATURE)
  }

  fun unscheduleNotification() {
    localNotificationSyncManager.unschedule(LocalNotificationSync.NEW_FEATURE)
  }

  fun setFeatureAsShown() {
    preferences.edit().putBoolean(HAS_SHOWN_FEATURE + newFeature.id, true).apply()
  }

  fun hasShownFeature(): Boolean {
    return preferences.getBoolean(HAS_SHOWN_FEATURE + newFeature.id, false)
  }

}