package cm.aptoide.pt.feature_apps.domain

data class Widget(
  val title: String,
  val type: WidgetType,
  val layout: WidgetLayout,
  val view: String?,
  val tag: String?,
  val action: WidgetAction?
)

enum class WidgetType {
  APPS_GROUP, ESKILLS, ADS, ACTION_ITEM, APPCOINS_ADS, DISPLAYS
}

enum class WidgetLayout {
  BRICK, GRID, CURATION_1, UNDEFINED
}

data class WidgetAction(
  var type: String? = null,
  var label: String? = null,
  var tag: String? = null, var event: Event?
)


data class Event(
  var type: WidgetActionEventType,
  var name: WidgetActionEventName,
  var action: String, val layout: WidgetLayout?
)

enum class WidgetActionEventType {
  API
}

enum class WidgetActionEventName {
  listApps, getStoreWidgets, getMoreBundle, getAds, getAppCoinsAds, eSkills
}