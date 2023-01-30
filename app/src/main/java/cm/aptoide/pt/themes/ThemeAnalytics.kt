package cm.aptoide.pt.themes

import android.os.Bundle
import cm.aptoide.analytics.AnalyticsManager
import com.facebook.appevents.AppEventsLogger
import io.rakam.api.Rakam
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class ThemeAnalytics(val analyticsManager: AnalyticsManager) {

  companion object {
    const val DARK_THEME_INTERACT_EVENT = "Dark_Theme_Interact"
  }

  fun sendDarkThemeDialogTurnItOnClickEvent(context: String) {
    sendDarkThemeInteractEvent("turn it on", context)
  }

  fun sendDarkThemeDismissClickEvent(context: String) {
    sendDarkThemeInteractEvent("dismiss", context)
  }

  fun sendThemeChangedEvent(theme: ThemeManager.ThemeOption, context: String) {
    sendDarkThemeInteractEvent(getThemeOptionName(theme), context)
  }

  fun setDarkThemeUserProperty(darkThemeMode: DarkThemeMode) {
    val params = Bundle()
    params.putBoolean("dark_theme", darkThemeMode.isDark())
    AppEventsLogger.updateUserProperties(params) { }

    val rakamClient = Rakam.getInstance()
    rakamClient.superProperties =
        createDarkThemeRakamSuperProperty(rakamClient.superProperties, darkThemeMode)

//    Indicative.addProperty("theme", darkThemeMode.name.toLowerCase())
  }

  private fun createDarkThemeRakamSuperProperty(currentProperties: JSONObject?,
                                                darkThemeMode: DarkThemeMode): JSONObject {
    val superProperties = currentProperties ?: JSONObject()
    try {
      superProperties.put("theme", darkThemeMode.name.toLowerCase())
    } catch (e: JSONException) {
      e.printStackTrace()
    }
    return superProperties
  }

  private fun sendDarkThemeInteractEvent(action: String, context: String) {
    val params = HashMap<String, Any>()
    params["action"] = action
    analyticsManager.logEvent(params, DARK_THEME_INTERACT_EVENT, AnalyticsManager.Action.CLICK,
        context)
  }

  private fun getThemeOptionName(themeOption: ThemeManager.ThemeOption): String {
    return when (themeOption) {
      ThemeManager.ThemeOption.SYSTEM_DEFAULT -> "system default"
      ThemeManager.ThemeOption.LIGHT -> "light theme"
      ThemeManager.ThemeOption.DARK -> "dark theme"
    }
  }
}