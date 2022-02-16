package cm.aptoide.pt.feature_apps.data

import cm.aptoide.pt.feature_apps.data.network.model.AppJSON
import cm.aptoide.pt.feature_apps.data.network.service.AppsRemoteService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

internal class AptoideAppsRepository @Inject constructor(private val appsService: AppsRemoteService) :
  AppsRepository {
  override fun getAppsList(url: String): Flow<AppsResult> = flow {
    val appsListResponse = appsService.getAppsList()
    if (appsListResponse.isSuccessful) {
      appsListResponse.body()?.datalist?.list?.let {
        emit(AppsResult.Success(it.map { appJSON -> appJSON.toDomainModel() }))
      }
    } else {
      emit(AppsResult.Error(IllegalStateException()))
    }
  }.flowOn(Dispatchers.IO)

  private fun AppJSON.toDomainModel(): App {
    return App(
      name = this.name!!,
      icon = this.icon!!
    )
  }

}