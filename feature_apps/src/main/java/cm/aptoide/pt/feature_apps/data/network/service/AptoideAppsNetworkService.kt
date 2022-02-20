package cm.aptoide.pt.feature_apps.data.network.service

import cm.aptoide.pt.feature_apps.data.network.model.AppJSON
import cm.aptoide.pt.feature_apps.data.network.model.BaseV7DataListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

internal class AptoideAppsNetworkService(private val appsRemoteDataSource: Retrofit) :
  AppsRemoteService {
  override suspend fun getAppsList(query: String): Response<BaseV7DataListResponse<AppJSON>> {
    return appsRemoteDataSource.getAppsList(query)
  }

  internal interface Retrofit {
    @GET("apps/get/{query}")
    suspend fun getAppsList(
      @Path(value = "query", encoded = true) path: String,
    ): Response<BaseV7DataListResponse<AppJSON>>
  }
}