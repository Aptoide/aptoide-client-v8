package cm.aptoide.pt.feature_home.domain

import cm.aptoide.pt.extensions.getRandomString

data class Widget(
  val title: String,
  val type: WidgetType,
  val layout: WidgetLayout,
  val view: String?,
  val tag: String,
  val action: List<WidgetAction>?,
  val icon: String?,
  val graphic: String?,
  val background: String?,
  val url: String?,
)

enum class WidgetType {
  APPS_GROUP,
  APPC_BANNER,
  ESKILLS,
  ADS,
  ACTION_ITEM,
  APPCOINS_ADS,
  DISPLAYS,
  MY_GAMES,
  GAMES_MATCH,
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
  var url: String,
)

enum class WidgetActionType {
  BOTTOM,
  BUTTON,
  UNDEFINED
}

val randomWidgetAction =
  WidgetAction(
    type = WidgetActionType.values().random(),
    tag = getRandomString(range = 2..5, capitalize = true),
    label = getRandomString(range = 2..5, capitalize = true),
    url = getRandomString(range = 2..5, capitalize = true),
  )
