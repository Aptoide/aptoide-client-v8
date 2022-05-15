package cm.aptoide.pt.feature_appview.domain.usecase

import cm.aptoide.pt.feature_appview.domain.repository.AppViewRepository
import cm.aptoide.pt.feature_appview.domain.repository.OtherVersionsResult
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class GetAppOtherVersionsUseCase @Inject constructor(private val appViewRepository: AppViewRepository) {

  fun getOtherVersions(packageName: String): Flow<OtherVersionsResult> {
    return appViewRepository.getOtherVersions(packageName)
  }
}