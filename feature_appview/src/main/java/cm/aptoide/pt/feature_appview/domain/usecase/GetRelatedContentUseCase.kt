package cm.aptoide.pt.feature_appview.domain.usecase

import cm.aptoide.pt.feature_appview.domain.repository.AppViewRepository
import cm.aptoide.pt.feature_appview.domain.repository.RelatedContentResult
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class GetRelatedContentUseCase @Inject constructor(private val appViewRepository: AppViewRepository) {

  fun getRelatedContent(packageName: String): Flow<RelatedContentResult> {
    return appViewRepository.getRelatedContent(packageName)
  }
}
