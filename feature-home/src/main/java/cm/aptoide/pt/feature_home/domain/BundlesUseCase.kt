package cm.aptoide.pt.feature_home.domain

import cm.aptoide.pt.aptoide_network.domain.UrlsCache
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppsRepository
import cm.aptoide.pt.feature_home.data.WidgetsRepository
import dagger.hilt.android.scopes.ViewModelScoped
import timber.log.Timber
import javax.inject.Inject

@ViewModelScoped
class BundlesUseCase @Inject constructor(
  private val widgetsRepository: WidgetsRepository,
  private val urlsCache: UrlsCache,
  private val appsRepository: AppsRepository,
) {

  init {
    urlsCache.set(
      id = WIDGETS_TAG,
      url = ""
    )
  }

  suspend fun getHomeBundles(): List<Bundle> =
    widgetsRepository.getStoreWidgets(bypassCache = urlsCache.isInvalid(id = WIDGETS_TAG))
      .onEach { widget ->
        widget.view?.let {
          urlsCache.set(
            id = widget.tag,
            url = it
          )
        }
        widget.action?.forEach {
          if (it.tag.endsWith("-more")) {
            urlsCache.set(
              id = it.tag,
              url = it.url + "/limit=50"
            )
          } else {
            urlsCache.set(
              id = it.tag,
              url = it.url
            )
          }
        }
      }
      .map { widget ->
        val apps = if (widget.type == WidgetType.ESKILLS) {
          loadESkillsApps()
        } else {
          widget.getBundleApps()
        }
        mapAppsWidgetToBundle(widget, apps)
      }
      .onEach { Timber.d("$it") }

  private suspend fun Widget.getBundleApps(): List<List<App>> = listOfNotNull(
    view
      ?.let { loadApps(it, tag) }
      ?: emptyList(),
    getWidgetActionByType(action, WidgetActionType.BOTTOM)
      ?.let { loadApps(it.url, it.tag) }
  )

  suspend fun getMoreBundle(tag: String): List<App> = urlsCache.get(id = tag)
    ?.let {
      appsRepository.getAppsList(
        url = it,
        bypassCache = urlsCache.isInvalid(id = tag)
      )
    }
    ?: throw IllegalStateException("No urls found")

  private suspend fun loadApps(url: String, tag: String) = try {
    appsRepository.getAppsList(
      url = url,
      bypassCache = urlsCache.isInvalid(id = tag)
    )
  } catch (t: Throwable) {
    Timber.d(t)
    emptyList()
  }

  private suspend fun loadESkillsApps() = listOf(
    try {
      appsRepository.getAppsList(
        storeId = 15,
        groupId = 14169744,
        bypassCache = urlsCache.isInvalid(id = "eSkills/15/14169744")
      )
    } catch (t: Throwable) {
      Timber.d(t)
      emptyList()
    }
  )

  private fun mapAppsWidgetToBundle(
    widget: Widget,
    appsList: List<List<App>>
  ): Bundle = Bundle(
    title = widget.title,
    bundleIcon = widget.icon,
    background = widget.background,
    appsListList = appsList,
    type = widget.getType(),
    tag = widget.tag,
    view = widget.view,
    hasMoreAction = widget.hasMoreButtonAction(),
    bundleSource = widget.getBundleSource()
  )

  private fun Widget.getBundleSource(): BundleSource = when (type) {
    WidgetType.MY_GAMES,
    WidgetType.ACTION_ITEM -> BundleSource.MANUAL

    WidgetType.HTML_GAMES -> BundleSource.AUTOMATIC

    WidgetType.APPS_GROUP,
    WidgetType.ESKILLS,
    WidgetType.STORE_GROUPS -> if (view?.contains("group_id") == false) {
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
        WidgetLayout.GRAPHIC -> Type.FEATURE_GRAPHIC
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

fun getWidgetActionByType(
  actionList: List<WidgetAction>?,
  widgetActionType: WidgetActionType
): WidgetAction? = actionList?.find { it.type == widgetActionType }
