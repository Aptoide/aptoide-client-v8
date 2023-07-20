package cm.aptoide.pt.feature_home.domain

import cm.aptoide.pt.aptoide_network.domain.UrlsCache
import cm.aptoide.pt.feature_home.data.WidgetsRepository
import dagger.hilt.android.scopes.ViewModelScoped
import timber.log.Timber
import javax.inject.Inject

@ViewModelScoped
class BundlesUseCase @Inject constructor(
  private val widgetsRepository: WidgetsRepository,
  private val urlsCache: UrlsCache,
) {

  init {
    urlsCache.set(
      id = WIDGETS_TAG,
      url = ""
    )
  }

  suspend fun getHomeBundles(): List<Bundle> =
    widgetsRepository.getStoreWidgets(bypassCache = urlsCache.isInvalid(WIDGETS_TAG))
      .onEach { it.cacheUrls(urlsCache::set) }
      .map { widget ->
        Bundle(
          title = widget.title,
          bundleIcon = widget.icon,
          background = widget.background,
          actions = widget.action ?: emptyList(),
          type = widget.getType(),
          tag = widget.tag,
          view = widget.view,
          bundleSource = widget.getBundleSource()
        )
      }

  private fun Widget.getBundleSource(): BundleSource = when (type) {
    WidgetType.MY_GAMES,
    WidgetType.ACTION_ITEM,
    -> BundleSource.MANUAL

    WidgetType.HTML_GAMES -> BundleSource.AUTOMATIC

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
        WidgetLayout.CAROUSEL_LARGE -> Type.CAROUSEL_LARGE
        WidgetLayout.LIST -> Type.APP_GRID
        WidgetLayout.CURATION_1,
        WidgetLayout.UNDEFINED,
        WidgetLayout.BRICK,
        WidgetLayout.GRAPHIC,
        -> Type.FEATURE_GRAPHIC
      }
    }

    WidgetType.ESKILLS -> Type.ESKILLS
    WidgetType.MY_GAMES -> Type.MY_GAMES
    WidgetType.ACTION_ITEM -> Type.EDITORIAL
    WidgetType.STORE_GROUPS -> Type.CATEGORIES
    WidgetType.HTML_GAMES -> Type.HTML_GAMES
    else -> Type.UNKNOWN_BUNDLE
  }

  companion object {
    const val WIDGETS_TAG = "widgets"
  }
}
