package cm.aptoide.pt.feature_appview.domain

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppsRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class AppInfoUseCase @Inject constructor(private val appsRepository: AppsRepository) {

  fun getAppInfo(packageName: String): Flow<App> =
    appsRepository.getApp(packageName = packageName)
}
