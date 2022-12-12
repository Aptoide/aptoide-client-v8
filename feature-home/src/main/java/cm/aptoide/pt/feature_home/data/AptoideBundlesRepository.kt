package cm.aptoide.pt.feature_home.data

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppsRepository
import cm.aptoide.pt.feature_apps.data.AppsResult
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

  override fun getHomeBundles(): Flow<BundlesResult> = flow {
    val bundlesFlow = widgetsRepository.getStoreWidgets()
      .flatMapConcat {
        if (it is Result.Success) {
          return@flatMapConcat it.data.asFlow()
        } else {
          return@flatMapConcat flow { BundlesResult.Error(IllegalStateException()) }
        }
      }
      // https://aptoide.atlassian.net/browse/APP-954
      // this should be a merge (to parallelize calls) the issue here atm is preserving order.
      // Which with merge only it isn't achieved.
      // Because we are only polling 5 bundles it isn't a big deal... but should be optimized.
      // This might be a good starting point:
      // https://discuss.kotlinlang.org/t/how-to-merge-flows-while-preserving-the-order/22002/2
      .flatMapConcat { widget ->
        when (widget.type) {
          WidgetType.APPS_GROUP -> getAppsGroupBundle(widget)
          WidgetType.ESKILLS -> appsRepository.getAppsList(14169744).map {
            return@map mapAppsWidgetToBundle(listOf(it), widget)
          }.catch { Timber.d(it) }
          WidgetType.ACTION_ITEM -> getEditorialBundle(widget)
          WidgetType.MY_APPS -> getMyAppsBundle(widget)
          else -> appsRepository.getAppsList("").map {
            return@map mapAppsWidgetToBundle(listOf(it), widget)
          }.catch { it.printStackTrace() }
        }
      }
    try {
      val toList = bundlesFlow.toList()
      toList.forEach {
        Timber.d("$it")
      }
      emit(BundlesResult.Success(toList))
    } catch (e: Exception) {
      e.printStackTrace()
      emit(BundlesResult.Error(IllegalStateException()))
    }
  }

  private fun getAppsGroupBundle(widget: Widget): Flow<Bundle> {
    val widgetBottomAction: WidgetAction? =
      getWidgetActionByType(widget.action, WidgetActionType.BOTTOM)
    return if (widgetBottomAction != null) {
      return appsRepository.getAppsList(widget.view.toString())
        .combine(appsRepository.getAppsList(widgetBottomAction.event?.action.toString())) { widgetAppsResult, bottomAppsResult ->
          mapAppsWidgetToBundle(listOf(widgetAppsResult, bottomAppsResult), widget)
        }
        .flowOn(Dispatchers.IO)
        .catch { it.printStackTrace() }
    } else {
      appsRepository.getAppsList(widget.view.toString()).map {
        return@map mapAppsWidgetToBundle(listOf(it), widget)
      }.catch { it.printStackTrace() }
    }
  }

  private fun getWidgetActionByType(
    actionList: List<WidgetAction>?,
    widgetActionType: WidgetActionType
  ): WidgetAction? {
    var bottomWidgetAction: WidgetAction? = null
    actionList?.forEach { action ->
      if (action.type == widgetActionType) {
        bottomWidgetAction = action
      }
    }
    return bottomWidgetAction
  }

  private fun getMyAppsBundle(widget: Widget): Flow<Bundle> {
    return flowOf(
      Bundle(
        title = widget.title,
        appsListList = emptyList(),
        type = Type.MY_APPS,
        bundleIcon = widget.icon
      )
    )
  }

  override fun getHomeBundleActionListApps(bundleTag: String): Flow<List<App>> {
    return widgetsRepository.getWidget(bundleTag)
      .filterNotNull()
      .map { widget ->
        getWidgetActionByType(widget.action, WidgetActionType.BUTTON)?.event?.action
      }
      .flatMapConcat { url ->
        appsRepository.getAppsList("$url/limit=50").map {
          if (it is AppsResult.Success) {
            return@map it.data
          } else {
            throw IllegalStateException()
          }
        }
      }
  }

  private fun getEditorialBundle(widget: Widget) = flow {
    if (widget.type == WidgetType.ACTION_ITEM) {
      emit(
        Bundle(
          title = "Editorial",
          appsListList = emptyList(),
          type = Type.EDITORIAL,
          view = widget.view
        )
      )
    } else {
      throw IllegalStateException()
    }
  }

  private fun mapAppsWidgetToBundle(
    appsResult: List<AppsResult>,
    widget: Widget
  ): Bundle {
    val appsListList = appsResult.map {
      if (it is AppsResult.Success) {
        it.data
      } else {
        emptyList()
      }
    }
    return when (widget.type) {
      WidgetType.APPS_GROUP -> {
        return if (widget.tag == "appcoins-iab-featured") {
          Bundle(
            title = widget.title,
            bundleIcon = widget.icon,
            appsListList = appsListList,
            type = Type.FEATURED_APPC,
            bundleAction = bundleActionMapper.mapWidgetActionToBundleAction(widget)
          )
        } else return when (widget.layout) {
          WidgetLayout.GRID -> {
            Bundle(
              title = widget.title,
              bundleIcon = widget.icon,
              appsListList = appsListList,
              type = Type.APP_GRID,
              bundleAction = bundleActionMapper.mapWidgetActionToBundleAction(widget)
            )
          }
          WidgetLayout.PUBLISHER_TAKEOVER -> {
            Bundle(
              title = widget.title,
              appsListList = appsListList,
              type = Type.PUBLISHER_TAKEOVER,
              bundleAction = bundleActionMapper.mapWidgetActionToBundleAction(widget),
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
              bundleAction = bundleActionMapper.mapWidgetActionToBundleAction(widget)
            )
          }
          WidgetLayout.CAROUSEL_LARGE -> {
            Bundle(
              title = widget.title,
              bundleIcon = widget.icon,
              appsListList = appsListList,
              type = Type.CAROUSEL_LARGE,
              graphic = widget.graphic,
              background = widget.background,
              bundleAction = bundleActionMapper.mapWidgetActionToBundleAction(widget)
            )
          }
          WidgetLayout.LIST -> {
            Bundle(
              title = widget.title,
              appsListList = appsListList,
              type = Type.APP_GRID,
              bundleAction = bundleActionMapper.mapWidgetActionToBundleAction(widget)
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
              bundleAction = bundleActionMapper.mapWidgetActionToBundleAction(widget)
            )
          }
        }
      }
      WidgetType.ESKILLS -> Bundle(
        title = widget.title,
        appsListList = appsListList,
        type = Type.ESKILLS,
        bundleAction = bundleActionMapper.mapWidgetActionToBundleAction(widget)
      )
      WidgetType.MY_APPS -> Bundle(
        title = widget.title,
        appsListList = appsListList,
        type = Type.MY_APPS
        // TODO: this will be implemented in the future
      )
      else -> Bundle(
        title = widget.title,
        appsListList = emptyList(),
        type = Type.UNKNOWN_BUNDLE
      )
    }
  }
}
