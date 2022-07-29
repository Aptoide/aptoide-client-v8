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
  @RetrofitV7 private val appsService: AppsRemoteService
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

  override fun getApp(packageName: String): Flow<AppResult> {
    return flow {
      val getAppResponse = appsService.getApp(packageName)
      if (getAppResponse.isSuccessful) {
        getAppResponse.body()?.nodes?.meta?.data?.let {
          emit(AppResult.Success(it.toDomainModel()))
        }
      } else {
        emit(AppResult.Error(IllegalStateException()))
      }
    }.flowOn(Dispatchers.IO)
  }

  override fun getRecommended(url: String): Flow<AppsResult> {
    return flow {
      val getRecommendedResponse = appsService.getRecommended(url)
      if (getRecommendedResponse.isSuccessful) {
        getRecommendedResponse.body()?.datalist?.list?.let {
          emit(AppsResult.Success(it.map { appJSON -> appJSON.toDomainModel() }))
        }
      } else {
        emit(AppsResult.Error(IllegalStateException()))
      }
    }
  }

  override fun getAppVersions(packageName: String): Flow<AppsResult> {
    return flow {
      val getAppVersionsResponse = appsService.getAppVersionsList(packageName)
      if (getAppVersionsResponse.isSuccessful) {
        getAppVersionsResponse.body()?.list?.let {
          emit(AppsResult.Success(it.map { appJSON -> appJSON.toDomainModel() }))
        }
      } else {
        emit(AppsResult.Error(IllegalStateException()))
      }
    }
  }

  private fun AppJSON.toDomainModel(): App {
    return App(
      name = this.name!!,
      packageName = this.packageName!!,
      appSize = this.file.filesize,
      md5 = this.file.md5sum,
      icon = this.icon!!,
      featureGraphic = this.graphic.toString(),
      isAppCoins = this.appcoins!!.billing,
      malware = this.file.malware?.rank,
      rating = Rating(
        this.stats.rating.avg,
        this.stats.rating.total,
        this.stats.rating.votes?.map { Votes(it.value, it.count) }),
      downloads = this.stats.downloads,
      versionName = this.file.vername,
      versionCode = this.file.vercode,
      screenshots = this.media?.screenshots?.map { it.url },
      description = this.media?.description,
      store = Store(
        this.store.name,
        this.store.avatar,
        this.store.stats?.apps,
        this.store.stats?.subscribers,
        this.store.stats?.downloads
      ),
      releaseDate = this.added,
      updateDate = this.updated,
      website = this.developer?.website,
      email = this.developer?.email,
      privacyPolicy = this.developer?.privacy, permissions = this.file.used_permissions,
      file = File(
        this.file.vername,
        this.file.vercode,
        this.file.md5sum,
        this.file.filesize,
        this.file.path,
        this.file.path_alt
      ), obb = mapObb(this)
    )
  }

  private fun mapObb(app: AppJSON): Obb? {
    if (app.obb != null) {
      val main = File(
        app.file.vername,
        app.file.vercode,
        app.obb.main.md5sum,
        app.obb.main.filesize,
        app.obb.main.path,
        ""
      )
      return if (app.obb.patch != null) {
        Obb(
          main, File(
            app.file.vername,
            app.file.vercode,
            app.obb.patch.md5sum,
            app.obb.patch.filesize,
            app.obb.patch.path,
            ""
          )
        )
      } else {
        Obb(main, null)
      }
    } else {
      return null
    }
  }
}