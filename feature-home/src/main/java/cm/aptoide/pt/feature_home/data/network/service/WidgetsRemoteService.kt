package cm.aptoide.pt.feature_home.data.network.service

import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7DataListResponse
import cm.aptoide.pt.feature_home.data.network.model.WidgetsJSON

interface WidgetsRemoteService {

  suspend fun getStoreWidgets(): BaseV7DataListResponse<WidgetsJSON.WidgetNetwork>

}