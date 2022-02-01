package cm.aptoide.pt.feature_search.data.network.service

import cm.aptoide.pt.feature_search.data.network.response.TopSearchAppsListResponse
import retrofit2.http.GET

interface TopSearchAppsRetrofitService {

  @GET("listApps/{url}")
  suspend fun getTopSearchApps(): TopSearchAppsListResponse
}