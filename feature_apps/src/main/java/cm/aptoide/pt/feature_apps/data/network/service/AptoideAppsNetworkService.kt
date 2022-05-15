package cm.aptoide.pt.feature_apps.data.network.service

import cm.aptoide.pt.feature_apps.data.network.model.AppJSON
import cm.aptoide.pt.feature_apps.data.network.model.BaseV7DataListResponse
import cm.aptoide.pt.feature_apps.data.network.model.GetAppResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal class AptoideAppsNetworkService(private val appsRemoteDataSource: Retrofit) :
  AppsRemoteService {
  override suspend fun getAppsList(query: String): Response<BaseV7DataListResponse<AppJSON>> {
    return appsRemoteDataSource.getAppsList(query)
  }

  override suspend fun getAppsList(groupId: Long): Response<BaseV7DataListResponse<AppJSON>> {
    return appsRemoteDataSource.getAppsList(15, groupId)
  }

  override suspend fun getApp(packageName: String): Response<GetAppResponse> {
    return appsRemoteDataSource.getApp(packageName)
  }

  override suspend fun getRecommended(url: String): Response<BaseV7DataListResponse<AppJSON>> {
    return appsRemoteDataSource.getRecommendedAppsList(url)
  }

  internal interface Retrofit {
    @GET("apps/get/{query}")
    suspend fun getAppsList(
      @Path(value = "query", encoded = true) path: String,
    ): Response<BaseV7DataListResponse<AppJSON>>

    @GET("apps/get/")
    suspend fun getAppsList(
      @Query("store_id", encoded = true) storeId: Long,
      @Query("group_id", encoded = true) groupId: Long,
    ): Response<BaseV7DataListResponse<AppJSON>>

    @GET("getApp/")
    suspend fun getApp(
      @Query(value = "package_name", encoded = true) path: String
    ): Response<GetAppResponse>

    @GET("apps/getRecommended/{query}")
    suspend fun getRecommendedAppsList(
      @Path(value = "query", encoded = true) path: String,
    ): Response<BaseV7DataListResponse<AppJSON>>
  }
}