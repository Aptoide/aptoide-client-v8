package cm.aptoide.pt.feature_search.data

import cm.aptoide.pt.feature_apps.data.toDomainModel
import cm.aptoide.pt.feature_search.data.database.SearchHistoryRepository
import cm.aptoide.pt.feature_search.data.database.model.SearchHistoryEntity
import cm.aptoide.pt.feature_search.data.network.RemoteSearchRepository
import cm.aptoide.pt.feature_search.domain.model.AutoCompletedApp
import cm.aptoide.pt.feature_search.domain.model.SearchSuggestion
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository.AutoCompleteResult
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
  private val remoteSearchRepository: RemoteSearchRepository
) : SearchRepository {

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
      searchHistoryRepository.removeAppFromSearchHistory(SearchHistoryEntity(appName))
    }
  }

  override fun getAutoCompleteSuggestions(keyword: String): Flow<AutoCompleteResult> {
    return flow {
      val autoCompleteResponse = remoteSearchRepository.getAutoCompleteSuggestions(keyword)
      if (autoCompleteResponse.isSuccessful) {
        autoCompleteResponse.body()?.data?.let {
          emit(AutoCompleteResult.Success(it.map { suggestion -> AutoCompletedApp(suggestion) }))
        }
      } else {
        emit(AutoCompleteResult.Error(IllegalStateException()))
      }
    }.flowOn(Dispatchers.IO)
  }

  override fun getTopSearchedApps(): Flow<List<SearchSuggestion>> {
    return remoteSearchRepository.getTopSearchedApps()
      .map { it.map { topSearchApp -> SearchSuggestion(topSearchApp.appName) } }
  }

}
