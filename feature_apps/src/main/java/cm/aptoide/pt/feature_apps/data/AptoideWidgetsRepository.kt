package cm.aptoide.pt.feature_apps.data

import cm.aptoide.pt.feature_apps.data.network.model.WidgetsJSON
import cm.aptoide.pt.feature_apps.data.network.service.WidgetsRemoteService
import cm.aptoide.pt.feature_apps.domain.*
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
      tag = this.tag,
      action = WidgetAction(
        this.actions?.get(0)?.type,
        this.actions?.get(0)?.label,
        this.actions?.get(0)?.tag,
        Event(
          WidgetActionEventType.valueOf(this.actions?.get(0)?.event?.type?.name!!),
          WidgetActionEventName.valueOf(this.actions?.get(0)?.event?.name?.name!!),
          this.actions?.get(0)?.event?.action!!,
          WidgetLayout.valueOf(this.actions?.get(0)?.event?.data!!.layout!!.name)
        )
      )
    )
  }

  private fun WidgetsJSON.WidgetNetwork.extractLayout() = try {
    WidgetLayout.valueOf(this.data!!.layout!!.name)
  } catch (e: NullPointerException) {
    WidgetLayout.UNDEFINED
  }
}
