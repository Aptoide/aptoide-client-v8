package cm.aptoide.pt.feature_search.domain.repository

import cm.aptoide.pt.feature_search.domain.model.LocalTopDownloadedApp

interface LocalTopDownloadsRepository {

  suspend fun getLocalTopDownloadedApps(): List<LocalTopDownloadedApp>
}