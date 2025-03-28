package cm.aptoide.pt.feature_apps.data

import cm.aptoide.pt.aptoide_network.data.network.CacheConstants
import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7DataListResponse
import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7ListResponse
import cm.aptoide.pt.feature_apps.data.model.AppJSON
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject

internal class AptoideAppsListRepository @Inject constructor(
  private val appsRemoteDataSource: Retrofit,
  private val storeName: String,
  private val mapper: AppsListMapper,
  private val scope: CoroutineScope,
) : AppsListRepository {

  override suspend fun getAppsList(
    url: String,
    bypassCache: Boolean,
  ): List<App> = withContext(scope.coroutineContext) {
    if (url.isEmpty()) {
      throw IllegalStateException()
    }
    val query = url.split("listApps/")[1]
    appsRemoteDataSource.getAppsList(
      path = query,
      storeName = storeName,
      bypassCache = if (bypassCache) CacheConstants.NO_CACHE else null
    )
      .datalist?.list?.let(mapper::map)
      ?: throw IllegalStateException()
  }

  override suspend fun getAppsList(
    storeId: Long,
    groupId: Long,
    bypassCache: Boolean,
  ): List<App> = withContext(scope.coroutineContext) {
    appsRemoteDataSource.getAppsList(
      storeId = storeId,
      groupId = groupId,
      bypassCache = if (bypassCache) CacheConstants.NO_CACHE else null
    )
      .datalist?.list?.let(mapper::map)
      ?: throw IllegalStateException()
  }

  override suspend fun getRecommended(path: String): List<App> =
    withContext(scope.coroutineContext) {
      appsRemoteDataSource.getRecommendedAppsList(
        path = path,
        storeName = storeName,
      )
        .datalist?.list?.let(mapper::map)
        ?: throw IllegalStateException()
    }

  override suspend fun getCategoryAppsList(categoryName: String): List<App> =
    withContext(scope.coroutineContext) {
      appsRemoteDataSource.getAppsList(
        path = "group_name=$categoryName/sort=pdownloads",
        storeName = storeName,
        bypassCache = null
      )
        .datalist?.list?.let(mapper::map)
        ?: throw IllegalStateException()
    }

  override suspend fun getAppVersions(packageName: String): List<App> =
    withContext(scope.coroutineContext) {
      appsRemoteDataSource.getAppVersionsList(
        path = packageName,
        storeName = storeName
      )
        .list?.let(mapper::map)
        ?: throw IllegalStateException()
    }

  override suspend fun getAppsList(packageNames: String): List<App> =
    withContext(scope.coroutineContext) {
      appsRemoteDataSource.getAppsList(
        storeName = storeName,
        packageNames = packageNames,
      )
        .list?.let(mapper::map)
        ?: throw IllegalStateException()
    }

  override suspend fun getSortedAppsList(sort: String, limit: Int): List<App> =
    withContext(scope.coroutineContext) {
      appsRemoteDataSource.getSortedAppsList(
        storeName = storeName,
        sort = sort,
        limit = limit
      )
        .datalist?.list?.let(mapper::map)
        ?: throw IllegalStateException()
    }

  internal interface Retrofit {
    @GET("apps/get/{query}")
    suspend fun getAppsList(
      @Path(value = "query", encoded = true) path: String,
      @Query("store_name") storeName: String,
      @Header(CacheConstants.CACHE_CONTROL_HEADER) bypassCache: String?,
    ): BaseV7DataListResponse<AppJSON>

    @GET("apps/get/")
    suspend fun getAppsList(
      @Query("store_id", encoded = true) storeId: Long,
      @Query("group_id", encoded = true) groupId: Long,
      @Header(CacheConstants.CACHE_CONTROL_HEADER) bypassCache: String?,
    ): BaseV7DataListResponse<AppJSON>

    @GET("apps/getRecommended/{query}")
    suspend fun getRecommendedAppsList(
      @Path(value = "query", encoded = true) path: String,
      @Query("store_name") storeName: String,
    ): BaseV7DataListResponse<AppJSON>

    @GET("listAppVersions/")
    suspend fun getAppVersionsList(
      @Query(value = "package_name", encoded = true) path: String,
      @Query("store_name") storeName: String,
    ): BaseV7ListResponse<AppJSON>

    @GET("apps/getPackages")
    suspend fun getAppsList(
      @Query("store_name") storeName: String,
      @Query("package_names") packageNames: String,
    ): BaseV7ListResponse<AppJSON>

    @GET("apps/get/")
    suspend fun getSortedAppsList(
      @Query("store_name") storeName: String,
      @Query("sort") sort: String,
      @Query("limit") limit: Int,
    ): BaseV7DataListResponse<AppJSON>
  }
}
