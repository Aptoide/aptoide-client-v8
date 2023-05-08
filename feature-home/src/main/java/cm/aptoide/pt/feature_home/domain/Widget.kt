package cm.aptoide.pt.feature_home.domain

data class Widget(
  val title: String,
  val type: WidgetType,
  val layout: WidgetLayout,
  val view: String?,
  val tag: String,
  val action: List<WidgetAction>?,
  val icon: String?,
  val graphic: String?,
  val background: String?
) {

  fun hasMoreButtonAction(): Boolean =
    action
      ?.firstOrNull { it.type == WidgetActionType.BUTTON && it.tag.endsWith("-more") } != null
}

enum class WidgetType {
  APPS_GROUP,
  ESKILLS,
  ADS,
  ACTION_ITEM,
  APPCOINS_ADS,
  DISPLAYS,
  MY_GAMES,
  STORE_GROUPS,
  HTML_GAMES
}

enum class WidgetLayout {
  BRICK,
  GRID,
  CURATION_1,
  UNDEFINED,
  GRAPHIC,
  PUBLISHER_TAKEOVER,
  CAROUSEL,
  CAROUSEL_LARGE,
  LIST
}

data class WidgetAction(
  var type: WidgetActionType,
  var label: String? = null,
  var tag: String,
  var url: String
)

enum class WidgetActionType {
  BOTTOM,
  BUTTON,
  UNDEFINED
}
