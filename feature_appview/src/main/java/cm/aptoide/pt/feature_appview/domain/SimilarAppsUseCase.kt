package cm.aptoide.pt.feature_appview.domain

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppsRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class SimilarAppsUseCase @Inject constructor(private val appsRepository: AppsRepository) {

  fun getSimilarApps(packageName: String): Flow<List<App>> =
    appsRepository.getRecommended(url = "package_name=$packageName")
}
