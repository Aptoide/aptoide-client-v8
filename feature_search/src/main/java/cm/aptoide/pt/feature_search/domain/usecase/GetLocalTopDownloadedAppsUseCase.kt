package cm.aptoide.pt.feature_search.domain.usecase

import cm.aptoide.pt.feature_search.domain.Result
import cm.aptoide.pt.feature_search.domain.model.LocalTopDownloadedApp
import cm.aptoide.pt.feature_search.domain.repository.LocalTopDownloadsRepository

class GetLocalTopDownloadedAppsUseCas(private val topDownloadsRepository: LocalTopDownloadsRepository) {

  suspend fun getTopDownloadsRepository(): Result<List<LocalTopDownloadedApp>> {
    return try {
      Result.Success(topDownloadsRepository.getLocalTopDownloadedApps())
    } catch (e: Exception) {
      Result.Error(e)
    }
  }
}