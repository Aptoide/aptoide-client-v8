package cm.aptoide.pt.feature_appview.domain

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppsRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class AppVersionsUseCase @Inject constructor(private val appsRepository: AppsRepository) {

  fun getAppVersions(packageName: String): Flow<List<App>> =
    appsRepository.getAppVersions(packageName = packageName)
}
