package cm.aptoide.pt.feature_apps.data

import cm.aptoide.pt.aptoide_network.di.RetrofitV7
import cm.aptoide.pt.feature_apps.data.network.model.AppJSON
import cm.aptoide.pt.feature_apps.data.network.service.AppsRemoteService
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

  private fun AppJSON.toDomainModel(): App {
    return App(
      name = this.name!!,
      icon = this.icon!!,
      featureGraphic = this.graphic.toString(),
      isAppCoins = this.appcoins!!.billing,
      malware = this.file.malware.rank,
      rating = this.stats.rating.avg,
      downloads = this.stats.downloads,
      versionName = this.file.vername,
      screenshots = this.media.screenshots.map { it.url },
      description = this.media.description
    )
  }

}