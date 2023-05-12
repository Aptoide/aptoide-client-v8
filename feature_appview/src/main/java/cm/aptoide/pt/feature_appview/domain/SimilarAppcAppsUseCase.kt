package cm.aptoide.pt.feature_appview.domain

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_appview.domain.repository.AppViewRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class SimilarAppcAppsUseCase @Inject constructor(private val appViewRepository: AppViewRepository) {

  fun getSimilarApps(packageName: String): Flow<List<App>> =
    appViewRepository.getAppcSimilarApps(packageName)
}
