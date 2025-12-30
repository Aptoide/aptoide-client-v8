package cm.aptoide.pt.feature_updates.data.network

import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7ListResponse
import cm.aptoide.pt.feature_apps.data.model.AppJSON
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface UpdatesApi {
  @POST("apps/getUpdates")
  suspend fun getAppsUpdates(
    @Query("store_name") storeName: String?,
    @Body request: UpdatesRequest,
  ): BaseV7ListResponse<AppJSON>
}
