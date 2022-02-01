package cm.aptoide.pt.feature_search.data

import cm.aptoide.pt.feature_search.data.network.service.LocalTopDownloadsRetrofitService
import cm.aptoide.pt.feature_search.domain.model.LocalTopDownloadedApp
import cm.aptoide.pt.feature_search.domain.repository.LocalTopDownloadsRepository

class RemoteLocalTopDownloadsRepository(private val localTopDownloadsRetrofitService: LocalTopDownloadsRetrofitService) :
  LocalTopDownloadsRepository {

  override suspend fun getLocalTopDownloadedApps(): List<LocalTopDownloadedApp> {
    return localTopDownloadsRetrofitService.getLocalTopDownloadedApps().results.results.map {
      LocalTopDownloadedApp(
        it,
        it,
        it,
        0.0
      )
    }
  }

}