package cm.aptoide.pt.feature_apps.data

import cm.aptoide.pt.feature_apps.domain.Bundle
import cm.aptoide.pt.feature_apps.domain.Widget
import cm.aptoide.pt.feature_apps.domain.WidgetType
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
      .flatMapMerge { widget ->
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
    it: AppsResult,
    widget: Widget,
  ): Bundle {
    if (it is AppsResult.Success) {
      return Bundle(widget.title, it.data)
    } else {
      throw java.lang.IllegalStateException()
    }
  }
}
