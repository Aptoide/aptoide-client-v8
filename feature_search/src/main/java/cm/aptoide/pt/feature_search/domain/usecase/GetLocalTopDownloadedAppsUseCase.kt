package cm.aptoide.pt.feature_search.domain.usecase

import cm.aptoide.pt.feature_search.domain.Result
import cm.aptoide.pt.feature_search.domain.model.LocalTopDownloadedApp
import java.util.*

//class GetLocalTopDownloadedAppsUseCas(private val topDownloadsRepository: LocalTopDownloadsRepository) {
class GetLocalTopDownloadedAppsUseCas {

  suspend fun getTopDownloadsRepository(): Result<List<LocalTopDownloadedApp>> {
    return try {
      //Result.Success(topDownloadsRepository.getLocalTopDownloadedApps())
      Result.Success(Collections.emptyList())
    } catch (e: Exception) {
      Result.Error(e)
    }
  }
}