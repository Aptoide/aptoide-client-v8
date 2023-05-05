package cm.aptoide.pt.feature_apps.data.network.service

import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7DataListResponse
import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7ListResponse
import cm.aptoide.pt.feature_apps.data.network.model.AppJSON
import cm.aptoide.pt.feature_apps.data.network.model.GetAppResponse

internal interface AppsRemoteService {

  suspend fun getAppsList(query: String, bypassCache: Boolean = false): BaseV7DataListResponse<AppJSON>

  suspend fun getAppsList(groupId: Long, bypassCache: Boolean = false): BaseV7DataListResponse<AppJSON>

  suspend fun getApp(packageName: String, bypassCache: Boolean = false): GetAppResponse

  suspend fun getRecommended(url: String, bypassCache: Boolean = false): BaseV7DataListResponse<AppJSON>

  suspend fun getAppVersionsList(packageName: String): BaseV7ListResponse<AppJSON>
}
