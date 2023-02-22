package cm.aptoide.pt.feature_apps.data.network.service

import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7DataListResponse
import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7ListResponse
import cm.aptoide.pt.feature_apps.data.network.model.*
import retrofit2.http.*

internal class AptoideAppsNetworkService(
  private val appsRemoteDataSource: Retrofit,
  private val storeName: String
) :
  AppsRemoteService {
  override suspend fun getAppsList(query: String): BaseV7DataListResponse<AppJSON> {
    return appsRemoteDataSource.getAppsList(query, storeName)
  }

  override suspend fun getAppsList(groupId: Long): BaseV7DataListResponse<AppJSON> {
    return appsRemoteDataSource.getAppsList(15, groupId)
  }

  override suspend fun getApp(packageName: String): GetAppResponse {
    return appsRemoteDataSource.getApp(packageName, storeName)
  }

  override suspend fun getRecommended(url: String): BaseV7DataListResponse<AppJSON> {
    return appsRemoteDataSource.getRecommendedAppsList(url, storeName)
  }

  override suspend fun getAppVersionsList(packageName: String): BaseV7ListResponse<AppJSON> {
    return appsRemoteDataSource.getAppVersionsList(packageName, storeName)
  }

  override suspend fun getAppCategories(packageNames: List<String>): BaseV7ListResponse<AppCategoryJSON> {
    return appsRemoteDataSource.getAppsCategories(Names(packageNames))
  }

  internal interface Retrofit {
    @GET("apps/get/{query}")
    suspend fun getAppsList(
      @Path(value = "query", encoded = true) path: String,
      @Query("store_name") storeName: String,
      @Query("aab") aab: Int = 1,
    ): BaseV7DataListResponse<AppJSON>

    @GET("apps/get/")
    suspend fun getAppsList(
      @Query("store_id", encoded = true) storeId: Long,
      @Query("group_id", encoded = true) groupId: Long,
      @Query("aab") aab: Int = 1,
    ): BaseV7DataListResponse<AppJSON>

    @GET("app/get/")
    suspend fun getApp(
      @Query(value = "package_name", encoded = true) path: String,
      @Query("store_name") storeName: String,
      @Query("aab") aab: Int = 1,
    ): GetAppResponse

    @GET("apps/getRecommended/{query}")
    suspend fun getRecommendedAppsList(
      @Path(value = "query", encoded = true) path: String,
      @Query("store_name") storeName: String,
      @Query("aab") aab: Int = 1,
    ): BaseV7DataListResponse<AppJSON>

    @GET("listAppVersions/")
    suspend fun getAppVersionsList(
      @Query(value = "package_name", encoded = true) path: String,
      @Query("store_name") storeName: String,
      @Query("aab") aab: Int = 1,
    ): BaseV7ListResponse<AppJSON>

    @POST("hub/apps/get/")
    suspend fun getAppsCategories(
      @Body names: Names
    ): BaseV7ListResponse<AppCategoryJSON>
  }
}
