package cm.aptoide.pt.feature_apps.domain

data class Widget(val title: String, val type: WidgetType, val layout: WidgetLayout)

enum class WidgetType {
  APPS_GROUP, ESKILLS, ADS, ACTION_ITEM, APPCOINS_ADS, DISPLAYS
}

enum class WidgetLayout {
  BRICK, GRID, CURATION_1, UNDEFINED
}
