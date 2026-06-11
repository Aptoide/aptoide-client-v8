package cm.aptoide.pt.feature_home.domain

import cm.aptoide.pt.extensions.getRandomString
import cm.aptoide.pt.feature_home.domain.WidgetActionType.BOTTOM
import cm.aptoide.pt.feature_home.domain.WidgetActionType.BUTTON
import kotlin.random.Random

data class Bundle(
  val title: String,
  val actions: List<WidgetAction>,
  val type: Type,
  val tag: String,
  val bundleIcon: String? = null,
  val background: String? = null,
  val view: String? = null,
  val bundleSource: BundleSource = BundleSource.MANUAL,
  val timestamp: String = System.currentTimeMillis().toString(),
  val url: String? = null
) {

  val hasMoreAction: Boolean
    get() = actions.firstOrNull { it.type == BUTTON && it.tag.endsWith("-more") } != null

  /**
   * True only when the "see all" action resolves to an apps-list (listApps) URL,
   * which is the only kind the apps-list See All screen can fetch. Bundles whose
   * -more action targets something else (e.g. getStoreWidgets) are excluded so we
   * don't surface a See All that would just lead to an error screen.
   */
  val hasAppsListMoreAction: Boolean
    get() = actions.firstOrNull {
      it.type == BUTTON && it.tag.endsWith("-more") && "listApps/" in it.url
    } != null

  val bottomTag: String?
    get() = actions.firstOrNull { it.type == BOTTOM }?.tag
}

enum class BundleSource {
  AUTOMATIC,
  MANUAL,
  NONE
}

enum class Type {
  FEATURE_GRAPHIC,
  APPC_BANNER,
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
  HTML_GAMES,
  APP_COMING_SOON,
  NEWS_ITEM,
  NEW_APP,
  NEW_APP_VERSION,
  IN_GAME_EVENT,
  RTB_PROMO,
  GAMES_FEED,
  PLAY_AND_EARN
}

val randomBundle
  get() = Bundle(
    title = getRandomString(range = 2..5, capitalize = true),
    actions = List(Random.nextInt(WidgetActionType.values().size)) {
      val type = WidgetActionType.values()[it]
      randomWidgetAction.copy(type = type).run {
        if (type == BUTTON && Random.nextBoolean()) {
          copy(tag = "$tag-more")
        } else {
          this
        }
      }
    }.shuffled(),
    type = Type.values().random(),
    tag = getRandomString(range = 2..5, capitalize = true)
  )
