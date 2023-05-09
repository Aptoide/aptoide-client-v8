package cm.aptoide.pt.feature_home.domain

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppsRepository
import cm.aptoide.pt.feature_home.data.WidgetsRepository
import cm.aptoide.pt.feature_home.domain.*
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@Suppress("OPT_IN_USAGE")
@ViewModelScoped
class BundlesUseCase @Inject constructor(
  private val widgetsRepository: WidgetsRepository,
  private val appsRepository: AppsRepository,
) {

  fun getHomeBundles(bypassCache: Boolean): Flow<List<Bundle>> = flow {
    val bundlesFlow = widgetsRepository.getStoreWidgets(bypassCache)
      .flatMapConcat { it.asFlow() }
      // https://aptoide.atlassian.net/browse/APP-954
      // this should be a merge (to parallelize calls) the issue here atm is preserving order.
      // Which with merge only it isn't achieved.
      // Because we are only polling 5 bundles it isn't a big deal... but should be optimized.
      // This might be a good starting point:
      // https://discuss.kotlinlang.org/t/how-to-merge-flows-while-preserving-the-order/22002/2
      .flatMapConcat { widget ->
        if (widget.type == WidgetType.ESKILLS) {
          loadESkillsApps(bypassCache)
        } else {
          widget.getBundleApps(bypassCache)
        }
          .map { mapAppsWidgetToBundle(widget, it) }
      }
    val toList = bundlesFlow
      .onEach { Timber.d("$it") }
      .toList()
    emit(toList)
  }

  private fun Widget.getBundleApps(bypassCache: Boolean): Flow<List<List<App>>> = combine(
    flow = view
      ?.let { loadApps(it, bypassCache) }
      ?: flowOf(emptyList()),
    flow2 = getWidgetActionByType(action, WidgetActionType.BOTTOM)
      ?.url
      ?.let { loadApps(it, bypassCache) }
      ?: flowOf(emptyList())
  ) { widgetApps, bottomApps ->
    listOf(widgetApps, bottomApps)
  }
    .flowOn(Dispatchers.IO)

  fun getMoreBundle(bundleTag: String): Flow<Pair<List<App>, String>> =
    widgetsRepository.getWidget(bundleTag)
      .filterNotNull()
      .flatMapConcat { widget ->
        val action = getWidgetActionByType(widget.action, WidgetActionType.BUTTON)
        val tag = action?.tag ?: bundleTag
        val url = action?.url
        appsRepository.getAppsList("$url/limit=50").map { Pair(it, tag) }
      }

  private fun loadApps(url: String, bypassCache: Boolean) = appsRepository
    .getAppsList(url, bypassCache)
    .catch {
      Timber.d(it)
      emit(emptyList())
    }

  private fun loadESkillsApps(bypassCache: Boolean) = appsRepository
    .getAppsList(groupId = 14169744, bypassCache)
    .catch {
      Timber.d(it)
      emit(emptyList())
    }
    .map { listOf(it) }

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
}

fun getWidgetActionByType(
  actionList: List<WidgetAction>?,
  widgetActionType: WidgetActionType
): WidgetAction? = actionList?.find { it.type == widgetActionType }
