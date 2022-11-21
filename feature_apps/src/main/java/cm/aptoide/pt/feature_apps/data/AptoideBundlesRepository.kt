package cm.aptoide.pt.feature_apps.data

import cm.aptoide.pt.feature_apps.domain.*
import cm.aptoide.pt.feature_editorial.data.EditorialRepository
import cm.aptoide.pt.feature_reactions.ReactionsRepository
import kotlinx.coroutines.flow.*
import timber.log.Timber

internal class AptoideBundlesRepository(
  private val widgetsRepository: WidgetsRepository,
  private val appsRepository: AppsRepository,
  private val editorialRepository: EditorialRepository,
  private val reactionsRepository: ReactionsRepository,
  private val bundleActionMapper: BundleActionMapper,
  private val myAppsBundleProvider: MyAppsBundleProvider
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
          WidgetType.APPS_GROUP -> appsRepository.getAppsList(widget.view.toString()).map {
            return@map mapAppsWidgetToBundle(it, widget)
          }.catch { it.printStackTrace() }
          WidgetType.ESKILLS -> appsRepository.getAppsList(14169744).map {
            return@map mapAppsWidgetToBundle(it, widget)
          }.catch { Timber.d(it) }
          WidgetType.ACTION_ITEM -> getEditorialBundle(widget)
          WidgetType.MY_APPS -> getMyApps()
          else -> appsRepository.getAppsList("").map {
            return@map mapAppsWidgetToBundle(it, widget)
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

  private fun getMyApps(): Flow<Bundle> {
    return myAppsBundleProvider.getBundleApps().map{MyAppsBundle(it)}
  }

  override fun getHomeBundleActionListApps(bundleIdentifier: String): Flow<List<App>> {
    return widgetsRepository.getWidget(bundleIdentifier)
      .map { widget -> widget?.action?.get(0)?.event?.action }
      .filterNotNull()
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

  private fun getEditorialBundle(widget: Widget) =
    editorialRepository.getArticleMeta(widget.view.toString())
      .flatMapConcat { editorialResult ->
        if (editorialResult is EditorialRepository.EditorialResult.Success && widget.type == WidgetType.ACTION_ITEM) {
          reactionsRepository.getTotalReactions(editorialResult.data.id)
            .map {
              if (it is ReactionsRepository.ReactionsResult.Success) {
                return@map mapEditorialWidgetToBundle(
                  editorialResult,
                  it.data.reactionsNumber,
                  widget.type
                )
              } else {
                throw IllegalStateException()
              }
            }
        } else {
          flow { BundlesResult.Error(IllegalStateException()) }
        }
      }

  private fun mapEditorialWidgetToBundle(
    editorialResult: EditorialRepository.EditorialResult,
    reactionsNumber: Int,
    widgetType: WidgetType,
  ): Bundle {
    if (editorialResult is EditorialRepository.EditorialResult.Success && widgetType == WidgetType.ACTION_ITEM
    ) {
      return EditorialBundle(
        editorialResult.data.id,
        editorialResult.data.title,
        editorialResult.data.summary,
        editorialResult.data.image,
        editorialResult.data.subtype,
        editorialResult.data.date,
        editorialResult.data.views,
        reactionsNumber
      )
    } else {
      throw java.lang.IllegalStateException()
    }
  }

  private fun mapAppsWidgetToBundle(
    appsResult: AppsResult,
    widget: Widget,
  ): Bundle {
    if (appsResult is AppsResult.Success) {
      return when (widget.type) {
        WidgetType.APPS_GROUP -> {
          return if (widget.tag == "appcoins-iab-featured") {
            Bundle(
              title = widget.title,
              bundleIcon = widget.icon,
              appsList = appsResult.data,
              type = Type.FEATURED_APPC,
              bundleAction = bundleActionMapper.mapWidgetActionToBundleAction(widget)
            )
          } else return when (widget.layout) {
            WidgetLayout.GRID -> {
              Bundle(
                title = widget.title,
                bundleIcon = widget.icon,
                appsList = appsResult.data,
                type = Type.APP_GRID,
                bundleAction = bundleActionMapper.mapWidgetActionToBundleAction(widget)
              )
            }
            WidgetLayout.PUBLISHER_TAKEOVER -> {
              Bundle(
                title = widget.title,
                appsList = appsResult.data,
                type = Type.FEATURE_GRAPHIC,
                bundleAction = bundleActionMapper.mapWidgetActionToBundleAction(widget)
                // TODO: this will have its bundle type and layout in the future
              )
            }
            WidgetLayout.CAROUSEL -> {
              Bundle(
                title = widget.title,
                bundleIcon = widget.icon,
                appsList = appsResult.data,
                type = Type.CAROUSEL,
                bundleAction = bundleActionMapper.mapWidgetActionToBundleAction(widget)
              )
            }
            WidgetLayout.CAROUSEL_LARGE -> {
              Bundle(
                title = widget.title,
                bundleIcon = widget.icon,
                appsList = appsResult.data,
                type = Type.CAROUSEL_LARGE,
                graphic = widget.graphic,
                bundleAction = bundleActionMapper.mapWidgetActionToBundleAction(widget)
              )
            }
            WidgetLayout.LIST -> {
              Bundle(
                title = widget.title,
                appsList = appsResult.data,
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
                appsList = appsResult.data,
                type = Type.FEATURE_GRAPHIC,
                bundleAction = bundleActionMapper.mapWidgetActionToBundleAction(widget)
              )
            }
          }
        }
        WidgetType.ESKILLS -> Bundle(
          title = widget.title,
          appsList = appsResult.data,
          type = Type.ESKILLS,
          bundleAction = bundleActionMapper.mapWidgetActionToBundleAction(widget)
        )
        WidgetType.MY_APPS -> Bundle(
          title = widget.title,
          appsList = appsResult.data,
          type = Type.MY_APPS
          // TODO: this will be implemented in the future
        )
        else -> Bundle(
          title = widget.title,
          appsList = emptyList(),
          type = Type.UNKNOWN_BUNDLE
        )
      }
    } else {
      throw java.lang.IllegalStateException("Unknown widget type " + widget.type.name)
    }
  }
}
