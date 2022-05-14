package cm.aptoide.pt.feature_appview.data

import cm.aptoide.pt.feature_apps.data.AppResult
import cm.aptoide.pt.feature_apps.data.AppsRepository
import cm.aptoide.pt.feature_appview.domain.repository.AppViewRepository
import cm.aptoide.pt.feature_appview.domain.repository.AppViewResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AptoideAppViewRepository @Inject constructor(val appsRepository: AppsRepository) :
  AppViewRepository {

  override fun getAppInfo(packageName: String): Flow<AppViewResult> {
    return appsRepository.getApp(packageName = packageName).map {
      when (it) {
        is AppResult.Success -> {
          AppViewResult.Success(it.data)
        }
        is AppResult.Error -> {
          AppViewResult.Error(it.e)
        }
      }
    }
  }
}