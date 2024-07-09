package cm.aptoide.pt.feature_apps.domain

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppsRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
@Suppress("unused")
class AppInfoUseCase @Inject constructor(private val appsRepository: AppsRepository) {

  suspend fun getAppInfo(packageName: String): App =
    appsRepository.getApp(packageName = packageName)
}
