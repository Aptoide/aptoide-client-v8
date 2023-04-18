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

  override fun getStoreWidgets(bypassCache: Boolean): Flow<List<Widget>> = flow<List<Widget>> {
    val result = widgetsService.getStoreWidgets(bypassCache = bypassCache)
      .datalist?.list?.mapNotNull { widget ->
        widget.toDomainModel()?.also { cachedGetStoreWidgets[it.tag] = it }
      }
      ?: throw IllegalStateException()
    emit(result)
  }.flowOn(Dispatchers.IO)

  override fun getWidget(widgetIdentifier: String): Flow<Widget?> {
    return flowOf(cachedGetStoreWidgets[widgetIdentifier])
  }

  private fun WidgetsJSON.WidgetNetwork.toDomainModel(): Widget? {
    val type = this.type ?: return null
    return Widget(
      title = this.title!!,
      type = WidgetType.valueOf(type.name),
      layout = extractLayout(),
      view = this.view,
      tag = this.tag,
      action = extractWidgetListOfActions(),
      icon = this.data?.icon,
      graphic = this.data?.graphic,
      background = this.data?.background
    )
  }


  private fun WidgetsJSON.WidgetNetwork.extractWidgetListOfActions(): List<WidgetAction> =
    this.actions?.mapNotNull { actionJSON ->
      val eventType = actionJSON.event?.type?.name ?: return@mapNotNull null
      val eventName = actionJSON.event?.name?.name ?: return@mapNotNull null
      WidgetAction(
        mapWidgetActionType(actionJSON.type), actionJSON.label, actionJSON.tag,
        Event(
          WidgetActionEventType.valueOf(eventType),
          WidgetActionEventName.valueOf(eventName),
          actionJSON.event?.action, extractLayoutFromAction()
        )
      )
    } ?: listOf()

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
