package cm.aptoide.pt.feature_appview.domain.usecase

import cm.aptoide.pt.feature_appview.domain.repository.AppViewRepository
import cm.aptoide.pt.feature_appview.domain.repository.AppViewResult
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class GetAppInfoUseCase @Inject constructor(private val appViewRepository: AppViewRepository) {

  fun getAppInfo(packageName: String): Flow<AppViewResult> {
    return appViewRepository.getAppInfo(packageName = packageName)
  }
}