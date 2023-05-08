package cm.aptoide.pt.feature_apps.domain

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppsRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class AppVersionsUseCase @Inject constructor(private val appsRepository: AppsRepository) {

  suspend fun getAppVersions(packageName: String): List<App> =
    appsRepository.getAppVersions(packageName = packageName)
}
