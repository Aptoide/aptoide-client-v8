package cm.aptoide.pt.feature_search.data

import cm.aptoide.pt.feature_search.data.network.service.TopSearchAppsRetrofitService
import cm.aptoide.pt.feature_search.domain.model.TopSearchApp
import cm.aptoide.pt.feature_search.domain.repository.TopSearchRepository

class RemoteTopSearchAppsRepository(private val topSearchAppsRetrofitService: TopSearchAppsRetrofitService) :
  TopSearchRepository {

  override suspend fun getTopSearchedApps(): List<TopSearchApp> {
    return topSearchAppsRetrofitService.getTopSearchApps().topSearchApps.map { TopSearchApp(it.appName) }
  }
}