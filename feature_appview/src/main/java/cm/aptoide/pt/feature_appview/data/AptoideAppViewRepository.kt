package cm.aptoide.pt.feature_appview.data

import cm.aptoide.pt.feature_apps.data.AppResult
import cm.aptoide.pt.feature_apps.data.AppsRepository
import cm.aptoide.pt.feature_apps.data.AppsResult
import cm.aptoide.pt.feature_appview.domain.repository.AppViewRepository
import cm.aptoide.pt.feature_appview.domain.repository.AppViewResult
import cm.aptoide.pt.feature_appview.domain.repository.SimilarAppsResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOf
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

  override fun getSimilarApps(packageName: String): Flow<SimilarAppsResult> {
    return appsRepository.getRecommended("packageName=$packageName/section=appc")
      .flatMapMerge { appcResult ->
        when (appcResult) {
          is AppsResult.Success -> {
            return@flatMapMerge appsRepository.getRecommended("packageName=$packageName").map {
              when (it) {
                is AppsResult.Success -> {
                  return@map SimilarAppsResult.Success(it.data, appcResult.data)
                }
                is AppsResult.Error -> {
                  return@map SimilarAppsResult.Error(it.e)
                }
              }
            }
          }
          is AppsResult.Error -> {
            return@flatMapMerge flowOf(SimilarAppsResult.Error(appcResult.e))
          }
        }
      }
  }
}