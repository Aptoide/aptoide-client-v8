package cm.aptoide.pt.feature_home.data.fake

import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7DataListResponse
import cm.aptoide.pt.aptoide_network.data.network.base_response.DataList
import cm.aptoide.pt.feature_home.data.network.model.WidgetTypeJSON
import cm.aptoide.pt.feature_home.data.network.model.WidgetsJSON
import cm.aptoide.pt.feature_home.data.network.service.WidgetsRemoteService
import kotlinx.coroutines.delay

@Suppress("unused")
internal class WidgetsFakeService : WidgetsRemoteService {
  override suspend fun getStoreWidgets(): BaseV7DataListResponse<WidgetsJSON.WidgetNetwork> {
    delay(1000)
    val baseV7DataListResponse = BaseV7DataListResponse<WidgetsJSON.WidgetNetwork>()
    baseV7DataListResponse.datalist = createFakeDatalist()
    return baseV7DataListResponse
  }

  private fun createFakeDatalist(): DataList<WidgetsJSON.WidgetNetwork> {
    return DataList(
      total = 100,
      count = 0,
      offset = 0,
      limit = 10,
      next = 10,
      hidden = 1,
      isLoaded = false,
      list = createWidgetJsonList()
    )
  }

  private fun createWidgetJsonList(): List<WidgetsJSON.WidgetNetwork> {
    return listOf(createWidget(), createWidget(), createWidget(), createWidget(), createWidget())
  }

  private fun createWidget(): WidgetsJSON.WidgetNetwork {
    return WidgetsJSON.WidgetNetwork(
      type = WidgetTypeJSON.APPS_GROUP,
      title = "Widget Title",
      tag = "Widget tag",
      view = null,
      actions = null,
      data = null
    )
  }
}