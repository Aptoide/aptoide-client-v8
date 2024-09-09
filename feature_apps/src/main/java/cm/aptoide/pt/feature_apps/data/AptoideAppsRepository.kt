package cm.aptoide.pt.feature_apps.data

import cm.aptoide.pt.aptoide_network.data.network.CacheConstants
import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7DataListResponse
import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7ListResponse
import cm.aptoide.pt.feature_apps.data.model.AppJSON
import cm.aptoide.pt.feature_apps.data.model.CampaignUrl
import cm.aptoide.pt.feature_apps.data.model.CampaignUrls
import cm.aptoide.pt.feature_apps.data.model.DynamicSplitJSON
import cm.aptoide.pt.feature_apps.data.model.GetAppResponse
import cm.aptoide.pt.feature_apps.data.model.GetMetaResponse
import cm.aptoide.pt.feature_apps.data.model.VideoTypeJSON
import cm.aptoide.pt.feature_apps.domain.Rating
import cm.aptoide.pt.feature_apps.domain.Store
import cm.aptoide.pt.feature_apps.domain.Votes
import cm.aptoide.pt.feature_campaigns.CampaignImpl
import cm.aptoide.pt.feature_campaigns.CampaignRepository
import cm.aptoide.pt.feature_campaigns.CampaignTuple
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
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
  private val scope: CoroutineScope,
) : AppsRepository {

  override suspend fun getAppsList(
    url: String,
    bypassCache: Boolean,
  ): List<App> =
    withContext(scope.coroutineContext) {
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
  ): List<App> =
    withContext(scope.coroutineContext) {
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

  override suspend fun getApp(
    packageName: String,
    bypassCache: Boolean,
  ): App =
    withContext(scope.coroutineContext) {
      appsRemoteDataSource.getApp(
        path = packageName,
        storeName = if (packageName != "com.appcoins.wallet") storeName else null,
        bypassCache = if (bypassCache) CacheConstants.NO_CACHE else null
      )
        .nodes.meta.data
        .toDomainModel(
          campaignRepository = campaignRepository,
          adListId = UUID.randomUUID().toString()
        )
    }

  override suspend fun getAppMeta(
    source: String,
    bypassCache: Boolean,
    useStoreName: Boolean,
  ): App =
    withContext(scope.coroutineContext) {

      appsRemoteDataSource.getAppMeta(
        path = source,
        storeName = if (useStoreName) storeName else null,
        bypassCache = if (bypassCache) CacheConstants.NO_CACHE else null
      ).data
        .toDomainModel(
          campaignRepository = campaignRepository,
          adListId = UUID.randomUUID().toString()
        )
    }

  override suspend fun getRecommended(
    path: String,
    bypassCache: Boolean,
  ): List<App> =
    withContext(scope.coroutineContext) {
      val randomAdListId = UUID.randomUUID().toString()
      appsRemoteDataSource.getRecommendedAppsList(
        path = path,
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

  override suspend fun getAppsDynamicSplits(md5: String): List<DynamicSplit> =
    withContext(scope.coroutineContext) {
      appsRemoteDataSource.getDynamicSplits(md5 = md5).list
        ?.map(DynamicSplitJSON::toDomainModel)
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

    @GET("app/get/")
    suspend fun getApp(
      @Query(value = "package_name", encoded = true) path: String,
      @Query("store_name") storeName: String? = null,
      @Query("aab") aab: Int = 1,
      @Header(CacheConstants.CACHE_CONTROL_HEADER) bypassCache: String?,
    ): GetAppResponse

    @GET("app/getMeta/{source}")
    suspend fun getAppMeta(
      @Path(value = "source", encoded = true) path: String,
      @Query("store_name") storeName: String? = null,
      @Query("aab") aab: Int = 1,
      @Header(CacheConstants.CACHE_CONTROL_HEADER) bypassCache: String?,
    ): GetMetaResponse

    @GET("apps/getRecommended/{query}")
    suspend fun getRecommendedAppsList(
      @Path(value = "query", encoded = true) path: String,
      @Query("store_name") storeName: String,
      @Query("aab") aab: Int = 1,
      @Header(CacheConstants.CACHE_CONTROL_HEADER) bypassCache: String?,
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

    //TODO: should this be in a separate module that specifically deals with aabs?
    @GET("app/getDynamicSplits")
    suspend fun getDynamicSplits(
      @Query("apk_md5sum") md5: String,
      @Query("aab") aab: Int = 1,
    ): BaseV7ListResponse<DynamicSplitJSON>
  }
}

fun AppJSON.toDomainModel(
  campaignRepository: CampaignRepository,
  adListId: String,
) = App(
  id = this.id!!,
  name = this.name!!,
  packageName = this.packageName!!,
  appSize = this.file.filesize + (this.obb?.main?.filesize ?: 0) + (this.obb?.patch?.filesize ?: 0),
  md5 = this.file.md5sum,
  icon = this.icon!!,
  featureGraphic = this.graphic.toString(),
  isAppCoins = this.appcoins!!.billing,
  bdsFlags = this.appcoins?.flags,
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
  videos = this.media?.videos?.filter { it.type == VideoTypeJSON.YOUTUBE }?.map { it.url }
    ?: emptyList(),
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
    path = this.file.path ?: "",
    path_alt = this.file.path_alt ?: ""
  ),
  aab = mapAab(this),
  obb = mapObb(this),
  developerName = this.developer?.name,
  campaigns = this.urls.mapCampaigns(campaignRepository)
    ?.apply { this.adListId = adListId }
)

fun CampaignUrls.mapCampaigns(
  campaignRepository: CampaignRepository?,
): CampaignImpl? {
  if (campaignRepository != null) {
    return CampaignImpl(
      impressions = this.impression.toCampaignTupleList(),
      clicks = this.click.toCampaignTupleList(),
      downloads = this.download.toCampaignTupleList(),
      repository = campaignRepository,
    )
  }
  return null
}

private fun List<CampaignUrl>?.toCampaignTupleList(): List<CampaignTuple> {
  return this?.map { campaignUrl -> CampaignTuple(campaignUrl.name, campaignUrl.url) }
    ?: emptyList()
}

private fun mapObb(app: AppJSON): Obb? =
  if (app.obb != null) {
    val main = File(
      _fileName = app.obb.main.filename,
      vername = app.file.vername,
      vercode = app.file.vercode,
      md5 = app.obb.main.md5sum,
      filesize = app.obb.main.filesize,
      path = app.obb.main.path ?: "",
      path_alt = ""
    )
    if (app.obb.patch != null) {
      Obb(
        main = main,
        patch = File(
          _fileName = app.obb.patch.filename,
          vername = app.file.vername,
          vercode = app.file.vercode,
          md5 = app.obb.patch.md5sum,
          filesize = app.obb.patch.filesize,
          path = app.obb.patch.path ?: "",
          path_alt = ""
        )
      )
    } else {
      Obb(main = main, patch = null)
    }
  } else {
    null
  }

private fun mapAab(app: AppJSON) = app.aab?.let {
  Aab(
    requiredSplitTypes = it.requiredSplitTypes,
    splits = it.splits.map { split ->
      Split(
        type = split.type,
        file = File(
          _fileName = split.name,
          vername = app.file.vername,
          vercode = app.file.vercode,
          md5 = split.md5sum,
          filesize = split.filesize,
          path = split.path,
          path_alt = ""
        )
      )
    }
  )
}

fun DynamicSplitJSON.toDomainModel() = DynamicSplit(
  type = type,
  File(
    _fileName = this.name,
    vername = "",
    vercode = 0,
    md5 = this.md5sum,
    filesize = this.filesize,
    path = this.path,
    path_alt = ""
  ),
  deliveryTypes = this.deliveryTypes,
  splits = this.splits.map { split ->
    File(
      _fileName = split.name,
      vername = "",
      vercode = 0,
      md5 = split.md5sum,
      filesize = split.filesize,
      path = split.path,
      path_alt = ""
    )
  }
)
