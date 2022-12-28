package cm.aptoide.pt.feature_home.data

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppsRepository
import cm.aptoide.pt.feature_home.domain.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import timber.log.Timber

@Suppress("OPT_IN_USAGE")
internal class AptoideBundlesRepository(
  private val widgetsRepository: WidgetsRepository,
  private val appsRepository: AppsRepository,
  private val bundleActionMapper: BundleActionMapper,
) :
  BundlesRepository {

  override fun getHomeBundles(): Flow<List<Bundle>> = flow {
    val bundlesFlow = widgetsRepository.getStoreWidgets()
      .flatMapConcat { it.asFlow() }
      // https://aptoide.atlassian.net/browse/APP-954
      // this should be a merge (to parallelize calls) the issue here atm is preserving order.
      // Which with merge only it isn't achieved.
      // Because we are only polling 5 bundles it isn't a big deal... but should be optimized.
      // This might be a good starting point:
      // https://discuss.kotlinlang.org/t/how-to-merge-flows-while-preserving-the-order/22002/2
      .flatMapConcat { widget ->
        when (widget.type) {
          WidgetType.APPS_GROUP -> getAppsGroupBundle(widget)
          WidgetType.ESKILLS -> appsRepository.getAppsList(14169744).toBundleFlow(widget)
          WidgetType.ACTION_ITEM -> getEditorialBundle(widget)
          WidgetType.MY_GAMES -> getMyGamesBundle(widget)
          else -> appsRepository.getAppsList("").toBundleFlow(widget)
        }
      }
    val toList = bundlesFlow
      .onEach { Timber.d("$it") }
      .toList()
    emit(toList)
  }

  private fun getAppsGroupBundle(widget: Widget): Flow<Bundle> {
    val action =
      getWidgetActionByType(widget.action, WidgetActionType.BOTTOM)?.event?.action
    val url = widget.view.toString()
    return if (action != null) {
      appsRepository.getAppsList(url).emptyOnError()
        .combine(appsRepository.getAppsList(action).emptyOnError()) { widgetApps, bottomApps ->
          mapAppsWidgetToBundle(widget, widgetApps, bottomApps)
        }
        .flowOn(Dispatchers.IO)
    } else {
      appsRepository.getAppsList(url).toBundleFlow(widget)
    }
  }

  private fun getWidgetActionByType(
    actionList: List<WidgetAction>?,
    widgetActionType: WidgetActionType
  ): WidgetAction? = actionList?.find { it.type == widgetActionType }

  private fun getMyGamesBundle(widget: Widget): Flow<Bundle> = flowOf(
    Bundle(
      title = widget.title,
      appsListList = emptyList(),
      type = Type.MY_GAMES,
      tag = widget.tag,
      bundleIcon = widget.icon
    )
  )

  override fun getHomeBundleActionListApps(bundleTag: String): Flow<List<App>> =
    widgetsRepository.getWidget(bundleTag)
      .filterNotNull()
      .map { getWidgetActionByType(it.action, WidgetActionType.BUTTON)?.event?.action }
      .flatMapConcat { url ->
        appsRepository.getAppsList("$url/limit=50")
      }

  private fun getEditorialBundle(widget: Widget) = flow {
    if (widget.type == WidgetType.ACTION_ITEM) {
      emit(
        Bundle(
          title = "Editorial",
          appsListList = emptyList(),
          type = Type.EDITORIAL,
          tag = widget.tag,
          view = widget.view,
          bundleIcon = widget.icon
        )
      )
    } else {
      throw IllegalStateException()
    }
  }

  private fun Flow<List<App>>.toBundleFlow(widget: Widget) =
    emptyOnError().map { mapAppsWidgetToBundle(widget, it) }

  private fun Flow<List<App>>.emptyOnError() =
    catch {
      Timber.d(it)
      emit(emptyList())
    }

  private fun mapAppsWidgetToBundle(
    widget: Widget,
    vararg appsList: List<App>
  ): Bundle {
    val appsListList = appsList.toList()
    return when (widget.type) {
      WidgetType.APPS_GROUP -> {
        return if (widget.tag == "appcoins-iab-featured") {
          Bundle(
            title = widget.title,
            bundleIcon = widget.icon,
            appsListList = appsListList,
            type = Type.FEATURED_APPC,
            tag = widget.tag,
            bundleButtonAction = bundleActionMapper.mapWidgetActionToBundleAction(widget)
          )
        } else return when (widget.layout) {
          WidgetLayout.GRID -> {
            Bundle(
              title = widget.title,
              bundleIcon = widget.icon,
              appsListList = appsListList,
              type = Type.APP_GRID,
              tag = widget.tag,
              bundleButtonAction = bundleActionMapper.mapWidgetActionToBundleAction(widget)
            )
          }
          WidgetLayout.PUBLISHER_TAKEOVER -> {
            Bundle(
              title = widget.title,
              appsListList = appsListList,
              type = Type.PUBLISHER_TAKEOVER,
              tag = widget.tag,
              bundleButtonAction = bundleActionMapper.mapWidgetActionToBundleAction(widget),
              background = widget.background,
              bundleIcon = widget.icon
            )
          }
          WidgetLayout.CAROUSEL -> {
            Bundle(
              title = widget.title,
              bundleIcon = widget.icon,
              appsListList = appsListList,
              type = Type.CAROUSEL,
              tag = widget.tag,
              bundleButtonAction = bundleActionMapper.mapWidgetActionToBundleAction(widget),
              background = widget.background
            )
          }
          WidgetLayout.CAROUSEL_LARGE -> {
            Bundle(
              title = widget.title,
              bundleIcon = widget.icon,
              appsListList = appsListList,
              type = Type.CAROUSEL_LARGE,
              tag = widget.tag,
              graphic = widget.graphic,
              background = widget.background,
              bundleButtonAction = bundleActionMapper.mapWidgetActionToBundleAction(widget)
            )
          }
          WidgetLayout.LIST -> {
            Bundle(
              title = widget.title,
              appsListList = appsListList,
              type = Type.APP_GRID,
              tag = widget.tag,
              bundleButtonAction = bundleActionMapper.mapWidgetActionToBundleAction(widget)
              // TODO: this will have its bundle type and layout in the future
            )
          }
          WidgetLayout.CURATION_1,
          WidgetLayout.UNDEFINED,
          WidgetLayout.BRICK,
          WidgetLayout.GRAPHIC -> {
            Bundle(
              title = widget.title,
              bundleIcon = widget.icon,
              appsListList = appsListList,
              type = Type.FEATURE_GRAPHIC,
              tag = widget.tag,
              bundleButtonAction = bundleActionMapper.mapWidgetActionToBundleAction(widget)
            )
          }
        }
      }
      WidgetType.ESKILLS -> Bundle(
        title = widget.title,
        appsListList = appsListList,
        type = Type.ESKILLS,
        tag = widget.tag,
        bundleButtonAction = bundleActionMapper.mapWidgetActionToBundleAction(widget)
      )
      WidgetType.MY_GAMES -> Bundle(
        title = widget.title,
        appsListList = appsListList,
        type = Type.MY_GAMES,
        tag = widget.tag,
        // TODO: this will be implemented in the future
      )
      else -> Bundle(
        title = widget.title,
        appsListList = emptyList(),
        type = Type.UNKNOWN_BUNDLE,
        tag = widget.tag,
      )
    }
  }
}
