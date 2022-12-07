package cm.aptoide.pt.feature_apps.data

import cm.aptoide.pt.aptoide_network.di.RetrofitV7
import cm.aptoide.pt.feature_apps.data.network.model.AppJSON
import cm.aptoide.pt.feature_apps.data.network.service.AppsRemoteService
import cm.aptoide.pt.feature_apps.domain.Rating
import cm.aptoide.pt.feature_apps.domain.Store
import cm.aptoide.pt.feature_apps.domain.Votes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

internal class AptoideAppsRepository @Inject constructor(
  @RetrofitV7 private val appsService: AppsRemoteService,
) :
  AppsRepository {

  override fun getAppsList(url: String): Flow<AppsResult> = flow {
    if (url.isEmpty()) {
      emit(AppsResult.Error(IllegalStateException()))
    }
    var query = ""
    try {
      query = url.split("listApps/")[1]
    } catch (e: IndexOutOfBoundsException) {
      emit(AppsResult.Error(IllegalStateException()))
    }

    val appsListResponse = appsService.getAppsList(query)
    if (appsListResponse.isSuccessful) {
      appsListResponse.body()?.datalist?.list?.let {
        emit(AppsResult.Success(it.map { appJSON -> appJSON.toDomainModel() }))
      }
    } else {
      emit(AppsResult.Error(IllegalStateException()))
    }
  }.flowOn(Dispatchers.IO)

  override fun getAppsList(groupId: Long): Flow<AppsResult> = flow {
    val appsListResponse = appsService.getAppsList(groupId)
    if (appsListResponse.isSuccessful) {
      appsListResponse.body()?.datalist?.list?.let {
        emit(AppsResult.Success(it.map { appJSON -> appJSON.toDomainModel() }))
      }
    } else {
      emit(AppsResult.Error(IllegalStateException()))
    }
  }

  override fun getApp(packageName: String): Flow<AppResult> = flow {
    val getAppResponse = appsService.getApp(packageName)
    if (getAppResponse.isSuccessful) {
      getAppResponse.body()?.nodes?.meta?.data?.let {
        emit(AppResult.Success(it.toDomainModel()))
      }
    } else {
      emit(AppResult.Error(IllegalStateException()))
    }
  }.flowOn(Dispatchers.IO)

  override fun getRecommended(url: String): Flow<AppsResult> = flow {
    val getRecommendedResponse = appsService.getRecommended(url)
    if (getRecommendedResponse.isSuccessful) {
      getRecommendedResponse.body()?.datalist?.list?.let {
        emit(AppsResult.Success(it.map { appJSON -> appJSON.toDomainModel() }))
      }
    } else {
      emit(AppsResult.Error(IllegalStateException()))
    }
  }

  override fun getAppVersions(packageName: String): Flow<AppsResult> = flow {
    val getAppVersionsResponse = appsService.getAppVersionsList(packageName)
    if (getAppVersionsResponse.isSuccessful) {
      getAppVersionsResponse.body()?.list?.let {
        emit(AppsResult.Success(it.map { appJSON -> appJSON.toDomainModel() }))
      }
    } else {
      emit(AppsResult.Error(IllegalStateException()))
    }
  }

  private fun AppJSON.toDomainModel() = App(
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
    developerName = this.developer?.name
  )

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
}
