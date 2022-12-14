package cm.aptoide.pt.feature_home.data

import cm.aptoide.pt.feature_home.data.network.model.WidgetsJSON
import cm.aptoide.pt.feature_home.data.network.service.WidgetsRemoteService
import cm.aptoide.pt.feature_home.domain.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

internal class AptoideWidgetsRepository @Inject constructor(private val widgetsService: WidgetsRemoteService) :
  WidgetsRepository {

  private val cachedGetStoreWidgets: MutableMap<String, Widget> = mutableMapOf()

  override fun getStoreWidgets() = flow {
    val widgetsListResponse = widgetsService.getStoreWidgets()
    if (widgetsListResponse.isSuccessful) {
      widgetsListResponse.body()?.datalist?.list?.let {
        emit(Result.Success(it.map { widgetNetwork ->
          val widget = widgetNetwork.toDomainModel()
          cachedGetStoreWidgets[widget.tag] = widget
          return@map widget
        }))
      }
    } else {
      emit(Result.Error(IllegalStateException()))
    }
  }.flowOn(Dispatchers.IO)


  override fun getWidget(widgetIdentifier: String): Flow<Widget?> {
    return flowOf(cachedGetStoreWidgets[widgetIdentifier])
  }

  private fun WidgetsJSON.WidgetNetwork.toDomainModel(): Widget {
    return Widget(
      title = this.title!!,
      type = WidgetType.valueOf(this.type!!.name),
      layout = extractLayout(),
      view = this.view,
      tag = this.tag,
      action = extractWidgetListOfActions(),
      icon = this.data?.icon,
      graphic = this.data?.graphic,
      background = this.data?.background
    )
  }


  private fun WidgetsJSON.WidgetNetwork.extractWidgetListOfActions(): List<WidgetAction> {
    this.actions.let {
      val actionsList: ArrayList<WidgetAction> = arrayListOf()
      it?.forEach { actionJSON ->
        actionsList.add(
          WidgetAction(
            mapWidgetActionType(actionJSON.type), actionJSON.label, actionJSON.tag,
            Event(
              WidgetActionEventType.valueOf(actionJSON.event!!.type!!.name),
              WidgetActionEventName.valueOf(actionJSON.event!!.name!!.name),
              actionJSON.event?.action, extractLayoutFromAction()
            )
          )
        )
      }
      return actionsList
    }
  }

  private fun mapWidgetActionType(actionType: String?): WidgetActionType {
    return when (actionType) {
      "bottom" -> WidgetActionType.BOTTOM
      "button" -> WidgetActionType.BUTTON
      else -> {
        WidgetActionType.UNDEFINED
      }
    }
  }

  private fun WidgetsJSON.WidgetNetwork.extractLayout() = try {
    WidgetLayout.valueOf(this.data!!.layout!!.name)
  } catch (e: NullPointerException) {
    WidgetLayout.UNDEFINED
  }

  private fun WidgetsJSON.WidgetNetwork.extractLayoutFromAction() = try {
    WidgetLayout.valueOf(this.actions!![0].event!!.data!!.layout!!.name)
  } catch (e: NullPointerException) {
    WidgetLayout.UNDEFINED
  }
}
