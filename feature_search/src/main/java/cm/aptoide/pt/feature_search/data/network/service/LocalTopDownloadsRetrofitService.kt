package cm.aptoide.pt.feature_search.data.network.service

import cm.aptoide.pt.feature_search.data.network.response.TopDownloadsListResponse
import retrofit2.http.GET

interface LocalTopDownloadsRetrofitService {

  @GET("listApps/{url}")
  suspend fun getLocalTopDownloadedApps(): TopDownloadsListResponse
}