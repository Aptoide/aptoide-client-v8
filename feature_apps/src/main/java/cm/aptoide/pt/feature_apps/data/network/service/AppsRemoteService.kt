package cm.aptoide.pt.feature_apps.data.network.service

import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7DataListResponse
import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7ListResponse
import cm.aptoide.pt.feature_apps.data.network.model.AppJSON
import cm.aptoide.pt.feature_apps.data.network.model.GetAppResponse
import cm.aptoide.pt.feature_apps.data.network.model.GroupJSON
import retrofit2.Response

internal interface AppsRemoteService {

  suspend fun getAppsList(query: String): Response<BaseV7DataListResponse<AppJSON>>

  suspend fun getAppsList(groupId: Long): Response<BaseV7DataListResponse<AppJSON>>

  suspend fun getApp(packageName: String): Response<GetAppResponse>

  suspend fun getRecommended(url: String): Response<BaseV7DataListResponse<AppJSON>>

  suspend fun getAppVersionsList(packageName: String): Response<BaseV7ListResponse<AppJSON>>

  suspend fun getAppGroupsList(
    packageName: String,
    groupId: Long?
  ): Response<BaseV7DataListResponse<GroupJSON>>
}
