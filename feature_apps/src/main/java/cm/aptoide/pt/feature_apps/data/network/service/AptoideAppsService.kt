package cm.aptoide.pt.feature_apps.data.network.service

import cm.aptoide.pt.feature_apps.data.network.model.AppJSON
import cm.aptoide.pt.feature_apps.data.network.model.BaseV7DataListResponse
import retrofit2.Response
import retrofit2.http.GET

internal class AptoideAppsService(private val appsRemoteDataSource: Retrofit) : AppsRemoteService {
  override suspend fun getAppsList(): Response<BaseV7DataListResponse<AppJSON>> {
    return appsRemoteDataSource.getAppsList()
  }

  internal interface Retrofit {
    @GET("apps/get?aptoide_vercode=20000&store_id=15")
    suspend fun getAppsList(): Response<BaseV7DataListResponse<AppJSON>>
  }
}