package cm.aptoide.pt.feature_search.domain.usecase

import cm.aptoide.pt.feature_search.domain.Result
import cm.aptoide.pt.feature_search.domain.model.LocalTopDownloadedApp
import dagger.hilt.android.scopes.ViewModelScoped
import java.util.*
import javax.inject.Inject

@ViewModelScoped
class GetLocalTopDownloadedAppsUseCase @Inject constructor() {

  suspend fun getTopDownloadsRepository(): Result<List<LocalTopDownloadedApp>> {
    return try {
      Result.Success(Collections.emptyList())
    } catch (e: Exception) {
      Result.Error(e)
    }
  }
}