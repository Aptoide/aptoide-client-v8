package cm.aptoide.pt.feature_apps.data

import cm.aptoide.pt.feature_apps.domain.Bundle
import cm.aptoide.pt.feature_apps.domain.Type
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
    appsResult: AppsResult,
    widget: Widget,
  ): Bundle {
    if (appsResult is AppsResult.Success) {
      return if (widget.type == WidgetType.APPS_GROUP) {
        Bundle(widget.title, appsResult.data, Type.APP_GRID)
      } else {
        Bundle(widget.title, emptyList(), Type.UNKNOWN_BUNDLE)
      }
    } else {
      throw java.lang.IllegalStateException()
    }
  }
}
