package cm.aptoide.pt.feature_apps.data.network.service

import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7DataListResponse
import cm.aptoide.pt.feature_apps.data.network.model.WidgetsJSON
import retrofit2.Response

interface WidgetsRemoteService {

  suspend fun getStoreWidgets(): Response<BaseV7DataListResponse<WidgetsJSON.WidgetNetwork>>

}