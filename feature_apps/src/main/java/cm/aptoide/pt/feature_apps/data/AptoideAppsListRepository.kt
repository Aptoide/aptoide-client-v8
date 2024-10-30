package cm.aptoide.pt.feature_apps.data

import cm.aptoide.pt.aptoide_network.data.network.CacheConstants
import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7DataListResponse
import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7ListResponse
import cm.aptoide.pt.feature_apps.data.model.AppJSON
import cm.aptoide.pt.feature_campaigns.CampaignRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID
import javax.inject.Inject

internal class AptoideAppsListRepository @Inject constructor(
  private val appsRemoteDataSource: Retrofit,
  private val storeName: String,
  private val campaignRepository: CampaignRepository,
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
    val randomAdListId = UUID.randomUUID().toString()
    appsRemoteDataSource.getAppsList(
      path = query,
      storeName = storeName,
      bypassCache = if (bypassCache) CacheConstants.NO_CACHE else null
    )
      .datalist
      ?.list
      ?.map {
        it.toDomainModel(
          campaignRepository = campaignRepository,
          adListId = randomAdListId
        )
      }
      ?: throw IllegalStateException()
  }

  override suspend fun getAppsList(
    storeId: Long,
    groupId: Long,
    bypassCache: Boolean,
  ): List<App> = withContext(scope.coroutineContext) {
    val randomAdListId = UUID.randomUUID().toString()
    appsRemoteDataSource.getAppsList(
      storeId = storeId,
      groupId = groupId,
      bypassCache = if (bypassCache) CacheConstants.NO_CACHE else null
    )
      .datalist?.list?.map {
        it.toDomainModel(
          campaignRepository = campaignRepository,
          adListId = randomAdListId
        )
      }
      ?: throw IllegalStateException()
  }

  override suspend fun getRecommended(path: String): List<App> =
    withContext(scope.coroutineContext) {
      val randomAdListId = UUID.randomUUID().toString()
      appsRemoteDataSource.getRecommendedAppsList(
        path = path,
        storeName = storeName,
      )
        .datalist
        ?.list
        ?.map {
          it.toDomainModel(
            campaignRepository = campaignRepository,
            adListId = randomAdListId
          )
        }
        ?: throw IllegalStateException()
    }

  override suspend fun getCategoryAppsList(categoryName: String): List<App> =
    withContext(scope.coroutineContext) {
      val randomAdListId = UUID.randomUUID().toString()
      appsRemoteDataSource.getAppsList(
        path = "group_name=$categoryName/sort=pdownloads",
        storeName = storeName,
        bypassCache = null
      )
        .datalist
        ?.list
        ?.map {
          it.toDomainModel(
            campaignRepository = campaignRepository,
            adListId = randomAdListId
          )
        }
        ?: throw IllegalStateException()
    }

  override suspend fun getAppVersions(packageName: String): List<App> =
    withContext(scope.coroutineContext) {
      val randomAdListId = UUID.randomUUID().toString()
      appsRemoteDataSource.getAppVersionsList(
        path = packageName,
        storeName = storeName
      )
        .list
        ?.map { appJSON ->
          appJSON.toDomainModel(
            campaignRepository = campaignRepository,
            adListId = randomAdListId
          )
        }
        ?: throw IllegalStateException()
    }

  override suspend fun getAppsList(packageNames: String): List<App> =
    withContext(scope.coroutineContext) {
      val randomAdListId = UUID.randomUUID().toString()
      appsRemoteDataSource.getAppsList(
        storeName = storeName,
        packageNames = packageNames,
      )
        .list
        ?.map { appJSON ->
          appJSON.toDomainModel(
            campaignRepository = campaignRepository,
            adListId = randomAdListId
          )
        }
        ?: throw IllegalStateException()
    }

  internal interface Retrofit {
    @GET("apps/get/{query}")
    suspend fun getAppsList(
      @Path(value = "query", encoded = true) path: String,
      @Query("store_name") storeName: String,
      @Query("aab") aab: Int = 1,
      @Header(CacheConstants.CACHE_CONTROL_HEADER) bypassCache: String?,
    ): BaseV7DataListResponse<AppJSON>

    @GET("apps/get/")
    suspend fun getAppsList(
      @Query("store_id", encoded = true) storeId: Long,
      @Query("group_id", encoded = true) groupId: Long,
      @Query("aab") aab: Int = 1,
      @Header(CacheConstants.CACHE_CONTROL_HEADER) bypassCache: String?,
    ): BaseV7DataListResponse<AppJSON>

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

    @GET("apps/getPackages")
    suspend fun getAppsList(
      @Query("store_name") storeName: String,
      @Query("aab") aab: Int = 1,
      @Query("package_names") packageNames: String,
    ): BaseV7ListResponse<AppJSON>
  }
}
