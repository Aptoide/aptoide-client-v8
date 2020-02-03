package cm.aptoide.pt.themes

import android.content.SharedPreferences
import android.os.Build

open class DarkThemeNewFeatureManager(val themeManager: ThemeManager,
                                      val preferences: SharedPreferences) {

  companion object {
    const val SHOULD_SHOW_DARKTHEME_DIALOG = "should_show_dark_theme_dialog"
  }

  fun setDarkTheme() {
    themeManager.setThemeOption(ThemeManager.ThemeOption.DARK)
    themeManager.resetToBaseTheme()
  }

  fun setDarkThemeFeatureAsShown() {
    preferences.edit().putBoolean(SHOULD_SHOW_DARKTHEME_DIALOG, false).apply()
  }

  open fun shouldShowDarkThemeFeature(): Boolean {
    return preferences.getBoolean(SHOULD_SHOW_DARKTHEME_DIALOG, true)
        && ((Build.VERSION.SDK_INT >= 29
        && !themeManager.isThemeDark()
        && themeManager.getThemeOption() == ThemeManager.ThemeOption.SYSTEM_DEFAULT)
        || (Build.VERSION.SDK_INT < 29 && !themeManager.isThemeDark()))
  }
}