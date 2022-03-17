package cm.aptoide.pt.feature_apps.data

import cm.aptoide.pt.feature_apps.data.network.model.WidgetsJSON
import cm.aptoide.pt.feature_apps.data.network.service.WidgetsRemoteService
import cm.aptoide.pt.feature_apps.domain.Widget
import cm.aptoide.pt.feature_apps.domain.WidgetLayout
import cm.aptoide.pt.feature_apps.domain.WidgetType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

internal class AptoideWidgetsRepository @Inject constructor(private val widgetsService: WidgetsRemoteService) :
  WidgetsRepository {

  override fun getStoreWidgets() = flow {
    val widgetsListResponse = widgetsService.getStoreWidgets()
    if (widgetsListResponse.isSuccessful) {
      widgetsListResponse.body()?.datalist?.list?.let {
        emit(Result.Success(it.map { widgetNetwork -> widgetNetwork.toDomainModel() }))
      }
    } else {
      emit(Result.Error(IllegalStateException()))
    }
  }.flowOn(Dispatchers.IO)

  private fun WidgetsJSON.WidgetNetwork.toDomainModel(): Widget {
    return Widget(
      title = this.title!!,
      type = WidgetType.valueOf(this.type!!.name),
      layout = extractLayout(),
      view = this.view,
      tag = this.tag
    )
  }

  private fun WidgetsJSON.WidgetNetwork.extractLayout() = try {
    WidgetLayout.valueOf(this.data!!.layout!!.name)
  } catch (e: NullPointerException) {
    WidgetLayout.UNDEFINED
  }
}
