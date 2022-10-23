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
  private val bundleActionMapper: BundleActionMapper
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

  override fun getHomeBundleAction(bundleTag: String): Flow<List<App>> {
    /*return appsRepository.getAppsList()*/
    return flow {}
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
              appsList = appsResult.data,
              type = Type.FEATURED_APPC,
              bundleAction = bundleActionMapper.mapWidgetActionToBundleAction(widget)
            )
          } else if (widget.layout == WidgetLayout.GRID) {
            Bundle(
              title = widget.title,
              appsList = appsResult.data,
              type = Type.APP_GRID,
              bundleAction = bundleActionMapper.mapWidgetActionToBundleAction(widget)
            )
          } else {
            Bundle(
              title = widget.title,
              appsList = appsResult.data,
              type = Type.FEATURE_GRAPHIC,
              bundleAction = bundleActionMapper.mapWidgetActionToBundleAction(widget)
            )
          }
        }
        WidgetType.ESKILLS -> Bundle(
          title = widget.title,
          appsList = appsResult.data,
          type = Type.ESKILLS,
          bundleAction = bundleActionMapper.mapWidgetActionToBundleAction(widget)
        )
        else -> Bundle(
          title = widget.title,
          appsList = emptyList(),
          type = Type.UNKNOWN_BUNDLE
        )
      }
    } else {
      throw java.lang.IllegalStateException()
    }
  }
}
