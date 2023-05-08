package cm.aptoide.pt.feature_appview.domain.usecase

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_appview.domain.repository.AppViewRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class GetSimilarAppsUseCase @Inject constructor(private val appViewRepository: AppViewRepository) {

  fun getSimilarApps(packageName: String): Flow<List<App>> =
    appViewRepository.getSimilarApps(packageName)
}
