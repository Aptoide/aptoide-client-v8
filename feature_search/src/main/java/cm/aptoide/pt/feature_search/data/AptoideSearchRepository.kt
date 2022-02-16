package cm.aptoide.pt.feature_search.data

import cm.aptoide.pt.feature_search.data.database.LocalSearchHistoryRepository
import cm.aptoide.pt.feature_search.data.network.RemoteSearchRepository
import cm.aptoide.pt.feature_search.domain.model.SearchApp
import cm.aptoide.pt.feature_search.domain.model.SearchHistory
import cm.aptoide.pt.feature_search.domain.model.SuggestedApp
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AptoideSearchRepository(
  private val localSearchHistoryRepository: LocalSearchHistoryRepository,
  private val remoteSearchRepository: RemoteSearchRepository
) : SearchRepository {

  override suspend fun searchApp(keyword: String): List<SearchApp> {
    TODO("Not yet implemented")
  }

  override fun getSearchHistory(): Flow<List<SearchHistory>> {
    return localSearchHistoryRepository.getSearchHistory()
      .map { it.map { SearchHistory(it.appName) } }
  }

  override suspend fun addAppToSearchHistory(appName: String) {
    localSearchHistoryRepository.addAppToSearchHistory(appName)
  }

  override suspend fun getSearchAutoComplete(keyword: String): List<SuggestedApp> {
    TODO("Not yet implemented")
  }

}
