package cm.aptoide.pt.feature_home.domain

import cm.aptoide.pt.aptoide_network.domain.UrlsCache
import cm.aptoide.pt.feature_bonus.data.BonusData
import cm.aptoide.pt.feature_home.data.WidgetsRepository
import javax.inject.Inject

class BundlesUseCase @Inject constructor(
  private val widgetsRepository: WidgetsRepository,
  private val urlsCache: UrlsCache,
) {

  suspend fun getHomeBundles(context: String? = null): List<Bundle> =
    widgetsRepository.getStoreWidgets(
      context = context,
      bypassCache = urlsCache.isInvalid(WIDGETS_TAG)
        .also { urlsCache.putAll(mapOf(WIDGETS_TAG to "")) }
    )
      .also { urlsCache.putAll(it.tagsUrls) }
      .also {
        it.find { bundle ->
          bundle.type == WidgetType.APPC_BANNER
        }?.let { bonusBundle ->
          BonusData.setBonusData(bonusBundle.title, bonusBundle.tag)
        } ?: urlsCache.putAll(
          mapOf(
            "bonus-banner-more" to "listApps/store_id=3613731/group_id=15614123/order=rand"
          )
        )
      }
      .map {
        Bundle(
          title = it.title,
          bundleIcon = it.icon,
          background = it.background,
          actions = it.action ?: emptyList(),
          type = it.getType(),
          tag = it.tag,
          view = it.view,
          bundleSource = it.getBundleSource(),
          url = it.url
        )
      }

  private fun Widget.getBundleSource(): BundleSource = when (type) {
    WidgetType.MY_GAMES,
    WidgetType.GAMES_MATCH,
    WidgetType.ACTION_ITEM,
    WidgetType.NEW_APP,
    WidgetType.NEWS_ITEM,
    WidgetType.APP_COMING_SOON,
    WidgetType.IN_GAME_EVENT,
    WidgetType.NEW_APP_VERSION
      -> BundleSource.MANUAL

    WidgetType.HTML_GAMES,
    WidgetType.RTB_PROMO
      -> BundleSource.AUTOMATIC

    WidgetType.APPS_GROUP,
    WidgetType.ESKILLS,
    WidgetType.STORE_GROUPS,
      -> if (view?.contains("group_id") == false) {
      BundleSource.AUTOMATIC
    } else {
      BundleSource.MANUAL
    }

    else -> BundleSource.MANUAL
  }

  private fun Widget.getType(): Type = when (type) {
    WidgetType.APPS_GROUP -> if (tag == "appcoins-iab-featured") {
      Type.FEATURED_APPC
    } else {
      when (layout) {
        WidgetLayout.GRID -> Type.APP_GRID
        WidgetLayout.PUBLISHER_TAKEOVER -> Type.PUBLISHER_TAKEOVER
        WidgetLayout.CAROUSEL -> Type.CAROUSEL
        WidgetLayout.BRICK,
        WidgetLayout.GRAPHIC,
        WidgetLayout.CAROUSEL_LARGE -> Type.CAROUSEL_LARGE

        WidgetLayout.PROMO_GRAPHIC -> Type.NEW_APP
        WidgetLayout.LIST -> Type.APP_GRID
        WidgetLayout.CURATION_1,
        WidgetLayout.UNDEFINED,
          -> Type.FEATURE_GRAPHIC
      }
    }

    WidgetType.APPC_BANNER -> Type.APPC_BANNER
    WidgetType.ESKILLS -> Type.ESKILLS
    WidgetType.MY_GAMES -> Type.MY_GAMES
    WidgetType.RTB_PROMO -> Type.RTB_PROMO
    WidgetType.GAMES_MATCH -> Type.GAMES_MATCH
    WidgetType.ACTION_ITEM,
    WidgetType.EDITORIAL -> Type.EDITORIAL

    WidgetType.STORE_GROUPS -> Type.CATEGORIES
    WidgetType.HTML_GAMES -> Type.HTML_GAMES

    WidgetType.NEW_APP -> Type.NEW_APP
    WidgetType.NEWS_ITEM -> Type.NEWS_ITEM
    WidgetType.APP_COMING_SOON -> Type.APP_COMING_SOON
    WidgetType.IN_GAME_EVENT -> Type.IN_GAME_EVENT
    WidgetType.NEW_APP_VERSION -> Type.NEW_APP_VERSION

    else -> Type.UNKNOWN_BUNDLE
  }

  companion object {
    const val WIDGETS_TAG = "widgets"
  }
}

val List<Widget>.tagsUrls: Map<String, String>
  get() = map { widget ->
    (widget.action?.tagsUrls ?: emptyList()) +
      widget.view?.let { widget.tag to it }
  }.flatten()
    .filterNotNull()
    .toMap()

val List<WidgetAction>.tagsUrls: List<Pair<String, String>>
  get() = map {
    if (it.tag.endsWith("-more")) {
      it.tag to it.url + "/limit=50"
    } else {
      it.tag to it.url
    }
  }
