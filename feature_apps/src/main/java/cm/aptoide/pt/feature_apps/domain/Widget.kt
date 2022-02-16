package cm.aptoide.pt.feature_apps.domain

data class Widget(val title: String, val type: WidgetType)

enum class WidgetType {
  APPS_GROUP, ESKILLS, ADS, ACTION_ITEM, APPCOINS_ADS, DISPLAYS
}
