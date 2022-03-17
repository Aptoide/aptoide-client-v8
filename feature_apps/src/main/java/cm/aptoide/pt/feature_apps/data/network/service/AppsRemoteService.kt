package cm.aptoide.pt.feature_apps.data.network.service

import cm.aptoide.pt.feature_apps.data.network.model.AppJSON
import cm.aptoide.pt.feature_apps.data.network.model.BaseV7DataListResponse
import retrofit2.Response

internal interface AppsRemoteService {

  suspend fun getAppsList(query: String): Response<BaseV7DataListResponse<AppJSON>>

  suspend fun getAppsList(groupId: Long): Response<BaseV7DataListResponse<AppJSON>>

}
