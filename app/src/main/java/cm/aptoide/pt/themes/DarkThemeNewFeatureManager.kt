package cm.aptoide.pt.themes

import android.os.Build

open class DarkThemeNewFeatureManager(val themeManager: ThemeManager,
                                      val newFeatureManager: NewFeatureManager) {

  fun scheduleNotification() {
    if (shouldShowFeature()) {
      newFeatureManager.scheduleNotification()
    } else {
      newFeatureManager.unscheduleNotification()
    }
  }

  open fun shouldShowFeature(): Boolean {
    return !newFeatureManager.hasShownFeature()
        && ((Build.VERSION.SDK_INT >= 29
        && !themeManager.isThemeDark()
        && themeManager.getThemeOption() == ThemeManager.ThemeOption.SYSTEM_DEFAULT)
        || (Build.VERSION.SDK_INT < 29 && !themeManager.isThemeDark()))
  }
}