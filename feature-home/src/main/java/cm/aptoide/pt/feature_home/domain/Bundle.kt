package cm.aptoide.pt.feature_home.domain

open class Bundle(
  val title: String,
  val actions: List<WidgetAction>,
  val type: Type,
  val tag: String,
  val bundleIcon: String? = null,
  val background: String? = null,
  val view: String? = null,
  val bundleSource: BundleSource = BundleSource.MANUAL,
  val timestamp: String = System.currentTimeMillis().toString(),
) {

  val hasMoreAction: Boolean
    get() = actions.firstOrNull { it.type == WidgetActionType.BUTTON && it.tag.endsWith("-more") } != null

  val bottomTag: String?
    get() = actions.firstOrNull { it.type == WidgetActionType.BOTTOM }?.tag
}

enum class BundleSource {
  AUTOMATIC,
  MANUAL,
  NONE
}

enum class Type {
  FEATURE_GRAPHIC,
  APP_GRID,
  ESKILLS,
  FEATURED_APPC,
  EDITORIAL,
  UNKNOWN_BUNDLE,
  GAMES_MATCH,
  MY_GAMES,
  CAROUSEL,
  CAROUSEL_LARGE,
  LIST,
  PUBLISHER_TAKEOVER,
  CATEGORIES,
  HTML_GAMES
}
