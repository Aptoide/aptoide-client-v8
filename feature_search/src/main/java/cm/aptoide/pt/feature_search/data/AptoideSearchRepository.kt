package cm.aptoide.pt.feature_search.data

import cm.aptoide.pt.feature_apps.data.toDomainModel
import cm.aptoide.pt.feature_search.data.database.SearchHistoryRepository
import cm.aptoide.pt.feature_search.data.database.model.SearchHistoryEntity
import cm.aptoide.pt.feature_search.data.network.RemoteSearchRepository
import cm.aptoide.pt.feature_search.domain.model.SearchSuggestion
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository.PopularAppSearchResult
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository.SearchAppResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AptoideSearchRepository @Inject constructor(
  private val searchHistoryRepository: SearchHistoryRepository,
  private val remoteSearchRepository: RemoteSearchRepository,
  private val autoCompleteSuggestionsRepository: AutoCompleteSuggestionsRepository,
) : SearchRepository {

  private var popularSearchApps: List<SearchSuggestion> = emptyList()

  override fun searchApp(keyword: String): Flow<SearchAppResult> {
    return flow {
      val searchResponse = remoteSearchRepository.searchApp(keyword)
      if (searchResponse.isSuccessful) {
        searchResponse.body()?.datalist?.list?.let {
          emit(SearchAppResult.Success(it.map { appJSON ->
            appJSON.toDomainModel()
          }))
        }
      } else {
        emit(SearchAppResult.Error(IllegalStateException()))
      }
    }.flowOn(Dispatchers.IO)
  }

  override fun getSearchHistory(): Flow<List<SearchSuggestion>> {
    return searchHistoryRepository.getSearchHistory()
      .map { it.map { historyApp -> SearchSuggestion(historyApp.appName) } }
  }

  override suspend fun addAppToSearchHistory(appName: String) {
    withContext(Dispatchers.IO) {
      searchHistoryRepository.addAppToSearchHistory(SearchHistoryEntity(appName))
    }
  }

  override suspend fun removeAppFromSearchHistory(appName: String) {
    withContext(Dispatchers.IO) {
      searchHistoryRepository.removeAppFromSearchHistory(appName)
    }
  }

  override fun getAutoCompleteSuggestions(keyword: String) =
    autoCompleteSuggestionsRepository.getAutoCompleteSuggestions(keyword)

  override fun getTopSearchedApps(): Flow<PopularAppSearchResult> {
    return flow {
      if (popularSearchApps.isNotEmpty()) {
        emit(PopularAppSearchResult.Success(popularSearchApps))
      } else {
        val topSearchAppsResponse = remoteSearchRepository.getTopSearchedApps()
        if (topSearchAppsResponse.isSuccessful) {
          topSearchAppsResponse.body()?.datalist?.list?.let {
            popularSearchApps =
              it.map { topSearchApp -> SearchSuggestion(topSearchApp.toDomainModel().name) }
            emit(PopularAppSearchResult.Success(popularSearchApps))
          }
        } else {
          emit(PopularAppSearchResult.Error(IllegalStateException()))
        }
      }
    }
  }
}
