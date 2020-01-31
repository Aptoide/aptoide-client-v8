package cm.aptoide.pt.themes

import android.os.Bundle
import cm.aptoide.analytics.AnalyticsManager
import com.facebook.appevents.AppEventsLogger
import java.util.*

class ThemeAnalytics(val analyticsManager: AnalyticsManager) {

  fun sendDarkThemeDialogTurnItOnClickEvent(context: String) {
    sendDarkThemeInteractEvent("turn it on", context)
  }

  fun sendDarkThemeDismissClickEvent(context: String) {
    sendDarkThemeInteractEvent("dismiss", context)
  }

  fun sendThemeChangedEvent(theme: ThemeManager.ThemeOption, context: String) {
    sendDarkThemeInteractEvent(getThemeOptionName(theme), context)
  }

  fun setDarkThemeUserProperty(isDarkTheme: Boolean) {
    val params = Bundle()
    params.putBoolean("dark_theme", isDarkTheme)
    AppEventsLogger.updateUserProperties(params) { }
  }

  private fun sendDarkThemeInteractEvent(action: String, context: String) {
    val params = HashMap<String, Any>()
    params.put("action", action)
    analyticsManager.logEvent(params, "Dark_Theme_Interact", AnalyticsManager.Action.CLICK, context)
  }

  private fun getThemeOptionName(themeOption: ThemeManager.ThemeOption): String {
    when (themeOption) {
      ThemeManager.ThemeOption.SYSTEM_DEFAULT -> return "system default"
      ThemeManager.ThemeOption.LIGHT -> return "light theme"
      ThemeManager.ThemeOption.DARK -> return "dark theme"
    }
  }
}