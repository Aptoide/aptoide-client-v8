package cm.aptoide.pt.feature_appview.data

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppsRepository
import cm.aptoide.pt.feature_appview.domain.repository.AppViewRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AptoideAppViewRepository @Inject constructor(
  private val appsRepository: AppsRepository,
) :
  AppViewRepository {

  override fun getAppInfo(packageName: String): Flow<App> =
    appsRepository.getApp(packageName = packageName)

  override fun getSimilarApps(packageName: String): Flow<List<App>> =
    appsRepository.getRecommended("package_name=$packageName")

  override fun getAppcSimilarApps(packageName: String): Flow<List<App>> =
    appsRepository.getRecommended("package_name=$packageName/section=appc")

  override fun getOtherVersions(packageName: String): Flow<List<App>> =
    appsRepository.getAppVersions(packageName)
}
