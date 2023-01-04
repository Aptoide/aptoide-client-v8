package cm.aptoide.pt.feature_apps.data.network.service

import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7DataListResponse
import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7ListResponse
import cm.aptoide.pt.feature_apps.data.network.model.AppJSON
import cm.aptoide.pt.feature_apps.data.network.model.GetAppResponse
import cm.aptoide.pt.feature_apps.data.network.model.GroupJSON
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal class AptoideAppsNetworkService(
  private val appsRemoteDataSource: Retrofit,
  private val appsGroupRemoteDataSource: Retrofit,
  private val storeName: String,
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

  override suspend fun getAppGroupsList(
    packageName: String,
    groupId: Long?,
  ): BaseV7DataListResponse<GroupJSON> =
    appsGroupRemoteDataSource.getAppGroups(packageName, groupId)

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

    @GET("apks/groups/get")
    suspend fun getAppGroups(
      @Query(value = "package_name", encoded = true) packageName: String,
      @Query(value = "group_id", encoded = true) groupId: Long?,
      @Query("aab") aab: Int = 1,
    ): BaseV7DataListResponse<GroupJSON>
  }
}