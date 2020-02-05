package cm.aptoide.pt.themes

import android.content.SharedPreferences
import android.os.Build
import cm.aptoide.pt.R
import cm.aptoide.pt.notification.AptoideNotification
import cm.aptoide.pt.notification.sync.LocalNotificationSync
import cm.aptoide.pt.notification.sync.LocalNotificationSyncManager
import cm.aptoide.pt.util.StringProvider

class NewFeatureManager(val preferences: SharedPreferences,
                        val localNotificationSyncManager: LocalNotificationSyncManager,
                        val themeManager: ThemeManager,
                        val stringProvider: StringProvider) {

  companion object {
    const val HAS_SHOWN_FEATURE = "has_shown_feature_"
  }

  fun scheduleNotification() {
    if (shouldShowFeature()) {
      localNotificationSyncManager.schedule(getFeatureTitle(), getFeatureDescription(), "",
          getFeatureActionStringRes(),
          "aptoidefeature://id=${getFeatureId()}&action=${getFeatureActionId()}",
          LocalNotificationSync.NEW_FEATURE,
          AptoideNotification.NEW_FEATURE)
    } else {
      unscheduleNotification()
    }


  }

  private fun unscheduleNotification() {
    localNotificationSyncManager.unschedule(LocalNotificationSync.NEW_FEATURE)
  }

  fun setFeatureAsShown() {
    preferences.edit().putBoolean(HAS_SHOWN_FEATURE + getFeatureId(), true).apply()
    localNotificationSyncManager.unschedule(LocalNotificationSync.NEW_FEATURE)
  }

  private fun hasShownFeature(): Boolean {
    return preferences.getBoolean(HAS_SHOWN_FEATURE + getFeatureId(), false)
  }

  /**
   * To be set for each feature
   * This shouldn't be hard coded as it is now...
   */

  fun shouldShowFeature(): Boolean {
    return !hasShownFeature()
        && ((Build.VERSION.SDK_INT >= 29
        && !themeManager.isThemeDark()
        && themeManager.getThemeOption() == ThemeManager.ThemeOption.SYSTEM_DEFAULT)
        || (Build.VERSION.SDK_INT < 29 && !themeManager.isThemeDark()))
  }

  fun getFeatureId(): String {
    return "dark_theme"
  }

  fun getFeatureTitle(): String {
    return stringProvider.getString(R.string.dark_theme_notification_title)
  }

  fun getFeatureDescription(): String {
    return "Save energy and reduce eye strain in low light conditions."
  }

  fun getFeatureActionStringRes(): Int {
    return R.string.dark_theme_notification_button
  }

  fun getFeatureActionId(): String {
    return "turn_it_on"
  }
}