package cm.aptoide.pt.feature_search.data

import cm.aptoide.pt.feature_search.data.database.LocalSearchHistoryRepository
import cm.aptoide.pt.feature_search.data.database.model.SearchHistoryEntity
import cm.aptoide.pt.feature_search.data.network.RemoteSearchRepository
import cm.aptoide.pt.feature_search.domain.model.SearchApp
import cm.aptoide.pt.feature_search.domain.model.SearchSuggestion
import cm.aptoide.pt.feature_search.domain.model.SuggestedApp
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AptoideSearchRepository @Inject constructor(
  private val localSearchHistoryRepository: LocalSearchHistoryRepository,
  private val remoteSearchRepository: RemoteSearchRepository
) : SearchRepository {

  override suspend fun searchApp(keyword: String): List<SearchApp> {
    TODO("Not yet implemented")
  }

  override fun getSearchHistory(): Flow<List<SearchSuggestion>> {
    return localSearchHistoryRepository.getSearchHistory()
      .map { it.map { historyApp -> SearchSuggestion(historyApp.appName) } }
  }

  override suspend fun addAppToSearchHistory(appName: String) {
    localSearchHistoryRepository.addAppToSearchHistory(SearchHistoryEntity(appName))
  }

  override suspend fun getSearchAutoComplete(keyword: String): List<SuggestedApp> {
    TODO("Not yet implemented")
  }

  override fun getTopSearchedApps(): Flow<List<SearchSuggestion>> {
    return remoteSearchRepository.getTopSearchedApps()
      .map { it.map { topSearchApp -> SearchSuggestion(topSearchApp.appName) } }
  }

}
