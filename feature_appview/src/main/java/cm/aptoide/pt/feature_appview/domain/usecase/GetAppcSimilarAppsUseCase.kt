package cm.aptoide.pt.feature_appview.domain.usecase

import cm.aptoide.pt.feature_appview.domain.repository.AppViewRepository
import cm.aptoide.pt.feature_appview.domain.repository.SimilarAppsResult
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class GetAppcSimilarAppsUseCase @Inject constructor(val appViewRepository: AppViewRepository){

  fun getAppcSimilarApps(packageName: String): Flow<SimilarAppsResult> {
    return appViewRepository.getSimilarApps(packageName)
  }
}