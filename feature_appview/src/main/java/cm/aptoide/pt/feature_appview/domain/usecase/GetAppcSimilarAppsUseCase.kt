package cm.aptoide.pt.feature_appview.domain.usecase

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_appview.domain.repository.AppViewRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class GetAppcSimilarAppsUseCase @Inject constructor(private val appViewRepository: AppViewRepository) {

  fun getAppcSimilarApps(packageName: String): Flow<List<App>> =
    appViewRepository.getAppcSimilarApps(packageName)
}
