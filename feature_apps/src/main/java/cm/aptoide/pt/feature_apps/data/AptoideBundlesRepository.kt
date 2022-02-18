package cm.aptoide.pt.feature_apps.data

import cm.aptoide.pt.feature_apps.domain.*
import kotlinx.coroutines.flow.*
import timber.log.Timber

internal class AptoideBundlesRepository(
  private val widgetsRepository: WidgetsRepository,
  private val appsRepository: AppsRepository,
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
      // this should be a merge (to parallelize calls) the issue here atm is preserving order.
      // Which with merge only it isn't achieved.
      // Because we are only polling 5 bundles it isn't a big deal... but should be optimized.
      // This might be a good starting point:
      // https://discuss.kotlinlang.org/t/how-to-merge-flows-while-preserving-the-order/22002/2
      .flatMapConcat { widget ->
        when (widget.type) {
          WidgetType.APPS_GROUP -> appsRepository.getAppsList("").map {
            return@map mapAppsWidgetToBundle(it, widget)
          }
          else -> appsRepository.getAppsList(widget.title).map {
            return@map mapAppsWidgetToBundle(it, widget)
          }
        }
      }

    val toList = bundlesFlow.toList()
    toList.forEach {
      Timber.d("$it")
    }
    emit(BundlesResult.Success(toList))
  }

  private fun mapAppsWidgetToBundle(
    appsResult: AppsResult,
    widget: Widget,
  ): Bundle {
    if (appsResult is AppsResult.Success) {
      return when (widget.type) {
        WidgetType.APPS_GROUP -> {
          return if (widget.layout == WidgetLayout.GRID)
            Bundle(widget.title, appsResult.data, Type.APP_GRID)
          else {
            Bundle(widget.title, appsResult.data, Type.FEATURE_GRAPHIC)
          }
        }
        else -> Bundle(widget.title, emptyList(), Type.UNKNOWN_BUNDLE)
      }
    } else {
      throw java.lang.IllegalStateException()
    }
  }
}
