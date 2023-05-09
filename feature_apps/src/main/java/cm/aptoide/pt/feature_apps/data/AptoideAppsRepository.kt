package cm.aptoide.pt.feature_apps.data

import cm.aptoide.pt.aptoide_network.data.network.CacheConstants
import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7DataListResponse
import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7ListResponse
import cm.aptoide.pt.feature_apps.data.model.AppJSON
import cm.aptoide.pt.feature_apps.data.model.CampaignUrls
import cm.aptoide.pt.feature_apps.data.model.GetAppResponse
import cm.aptoide.pt.feature_apps.domain.Rating
import cm.aptoide.pt.feature_apps.domain.Store
import cm.aptoide.pt.feature_apps.domain.Votes
import cm.aptoide.pt.feature_campaigns.CampaignImpl
import cm.aptoide.pt.feature_campaigns.CampaignRepository
import cm.aptoide.pt.feature_campaigns.data.CampaignUrlNormalizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID
import javax.inject.Inject

internal class AptoideAppsRepository @Inject constructor(
  private val appsRemoteDataSource: Retrofit,
  private val storeName: String,
  private val campaignRepository: CampaignRepository,
  private val campaignUrlNormalizer: CampaignUrlNormalizer
) : AppsRepository {

  override fun getAppsList(
    url: String,
    bypassCache: Boolean
  ): Flow<List<App>> = flow<List<App>> {
    if (url.isEmpty()) {
      throw IllegalStateException()
    }
    val query = url.split("listApps/")[1]
    val randomAdListId = UUID.randomUUID().toString()
    val response = appsRemoteDataSource.getAppsList(
      path = query,
      storeName = storeName,
      bypassCache = if (bypassCache) CacheConstants.NO_CACHE else null
    )
      .datalist?.list?.map {
        it.toDomainModel(
          campaignRepository = campaignRepository,
          campaignUrlNormalizer = campaignUrlNormalizer,
          adListId = randomAdListId
        )
      }
      ?: throw IllegalStateException()
    emit(response)
  }.flowOn(Dispatchers.IO)

  override fun getAppsList(groupId: Long, bypassCache: Boolean): Flow<List<App>> = flow {
    val randomAdListId = UUID.randomUUID().toString()
    val response = appsRemoteDataSource.getAppsList(
      storeId = 15,
      groupId = groupId,
      bypassCache = if (bypassCache) CacheConstants.NO_CACHE else null
    )
      .datalist?.list?.map {
        it.toDomainModel(
          campaignRepository = campaignRepository,
          campaignUrlNormalizer = campaignUrlNormalizer,
          adListId = randomAdListId
        )
      }
      ?: throw IllegalStateException()
    emit(response)
  }

  override fun getApp(packageName: String, bypassCache: Boolean): Flow<App> = flow {
    val response = appsRemoteDataSource.getApp(
      path = packageName,
      storeName = storeName,
      bypassCache = if (bypassCache) CacheConstants.NO_CACHE else null
    )
      .nodes.meta.data
      .toDomainModel(
        campaignRepository = campaignRepository,
        campaignUrlNormalizer = campaignUrlNormalizer
      )
    emit(response)
  }.flowOn(Dispatchers.IO)

  override fun getRecommended(
    url: String,
    bypassCache: Boolean
  ): Flow<List<App>> = flow {
    val randomAdListId = UUID.randomUUID().toString()
    val response = appsRemoteDataSource.getRecommendedAppsList(
      path = url,
      storeName = storeName,
      bypassCache = if (bypassCache) CacheConstants.NO_CACHE else null
    )
      .datalist?.list?.map {
        it.toDomainModel(
          campaignRepository = campaignRepository,
          campaignUrlNormalizer = campaignUrlNormalizer,
          adListId = randomAdListId
        )
      }
      ?: throw IllegalStateException()
    emit(response)
  }

  override fun getCategoryAppsList(categoryName: String): Flow<List<App>> =
    flow<List<App>> {
      val query = "group_name=$categoryName/sort=pdownloads"
      val response = appsRemoteDataSource.getAppsList(
        path = query,
        storeName = storeName,
        bypassCache = null
      )
        .datalist?.list?.map {
          it.toDomainModel()
        }
        ?: throw IllegalStateException()
      emit(response)
    }.flowOn(Dispatchers.IO)

  override fun getAppVersions(packageName: String): Flow<List<App>> = flow {
    val randomAdListId = UUID.randomUUID().toString()
    val response = appsRemoteDataSource
      .getAppVersionsList(packageName, storeName)
      .list?.map { appJSON ->
        appJSON.toDomainModel(
          campaignRepository = campaignRepository,
          campaignUrlNormalizer = campaignUrlNormalizer,
          adListId = randomAdListId
        )
      }
      ?: throw IllegalStateException()
    emit(response)
  }

  internal interface Retrofit {
    @GET("apps/get/{query}")
    suspend fun getAppsList(
      @Path(value = "query", encoded = true) path: String,
      @Query("store_name") storeName: String,
      @Query("aab") aab: Int = 1,
      @Header(CacheConstants.CACHE_CONTROL_HEADER) bypassCache: String?
    ): BaseV7DataListResponse<AppJSON>

    @GET("apps/get/")
    suspend fun getAppsList(
      @Query("store_id", encoded = true) storeId: Long,
      @Query("group_id", encoded = true) groupId: Long,
      @Query("aab") aab: Int = 1,
      @Header(CacheConstants.CACHE_CONTROL_HEADER) bypassCache: String?
    ): BaseV7DataListResponse<AppJSON>

    @GET("app/get/")
    suspend fun getApp(
      @Query(value = "package_name", encoded = true) path: String,
      @Query("store_name") storeName: String,
      @Query("aab") aab: Int = 1,
      @Header(CacheConstants.CACHE_CONTROL_HEADER) bypassCache: String?
    ): GetAppResponse

    @GET("apps/getRecommended/{query}")
    suspend fun getRecommendedAppsList(
      @Path(value = "query", encoded = true) path: String,
      @Query("store_name") storeName: String,
      @Query("aab") aab: Int = 1,
      @Header(CacheConstants.CACHE_CONTROL_HEADER) bypassCache: String?
    ): BaseV7DataListResponse<AppJSON>

    @GET("listAppVersions/")
    suspend fun getAppVersionsList(
      @Query(value = "package_name", encoded = true) path: String,
      @Query("store_name") storeName: String,
      @Query("aab") aab: Int = 1,
    ): BaseV7ListResponse<AppJSON>
  }
}

fun AppJSON.toDomainModel(
  campaignRepository: CampaignRepository? = null,
  campaignUrlNormalizer: CampaignUrlNormalizer? = null,
  adListId: String = ""
) = App(
  name = this.name!!,
  packageName = this.packageName!!,
  appSize = this.file.filesize,
  md5 = this.file.md5sum,
  icon = this.icon!!,
  featureGraphic = this.graphic.toString(),
  isAppCoins = this.appcoins!!.billing,
  malware = this.file.malware?.rank,
  rating = Rating(
    avgRating = this.stats.rating.avg,
    totalVotes = this.stats.rating.total,
    votes = this.stats.rating.votes?.map { Votes(it.value, it.count) }
  ),
  pRating = Rating(
    avgRating = this.stats.prating.avg,
    totalVotes = this.stats.prating.total,
    votes = this.stats.prating.votes?.map { Votes(it.value, it.count) }
  ),
  downloads = this.stats.downloads,
  versionName = this.file.vername,
  versionCode = this.file.vercode,
  screenshots = this.media?.screenshots?.map { it.url },
  description = this.media?.description,
  store = Store(
    storeName = this.store.name,
    icon = this.store.avatar,
    apps = this.store.stats?.apps,
    subscribers = this.store.stats?.subscribers,
    downloads = this.store.stats?.downloads
  ),
  releaseDate = this.added,
  updateDate = this.updated,
  releaseUpdateDate = this.release?.updated,
  website = this.developer?.website,
  email = this.developer?.email,
  privacyPolicy = this.developer?.privacy,
  permissions = this.file.used_permissions,
  file = File(
    vername = this.file.vername,
    vercode = this.file.vercode,
    md5 = this.file.md5sum,
    filesize = this.file.filesize,
    path = this.file.path,
    path_alt = this.file.path_alt
  ),
  obb = mapObb(this),
  developerName = this.developer?.name,
  campaigns = this.urls.mapCampaigns(campaignRepository, campaignUrlNormalizer)
    ?.apply { this.adListId = adListId }
)

fun CampaignUrls.mapCampaigns(
  campaignRepository: CampaignRepository?,
  campaignUrlNormalizer: CampaignUrlNormalizer?
): CampaignImpl? {
  if (campaignRepository != null && campaignUrlNormalizer != null) {
    val impressionsList = this.impression?.map { campaignUrl -> campaignUrl.url } ?: emptyList()
    val clicksList = this.click?.map { campaignUrl -> campaignUrl.url } ?: emptyList()
    return CampaignImpl(
      impressions = impressionsList,
      clicks = clicksList,
      repository = campaignRepository,
      normalize = campaignUrlNormalizer.normalize
    )
  }
  return null
}

private fun mapObb(app: AppJSON): Obb? =
  if (app.obb != null) {
    val main = File(
      vername = app.file.vername,
      vercode = app.file.vercode,
      md5 = app.obb.main.md5sum,
      filesize = app.obb.main.filesize,
      path = app.obb.main.path,
      path_alt = ""
    )
    if (app.obb.patch != null) {
      Obb(
        main = main,
        patch = File(
          vername = app.file.vername,
          vercode = app.file.vercode,
          md5 = app.obb.patch.md5sum,
          filesize = app.obb.patch.filesize,
          path = app.obb.patch.path,
          path_alt = ""
        )
      )
    } else {
      Obb(main = main, patch = null)
    }
  } else {
    null
  }
