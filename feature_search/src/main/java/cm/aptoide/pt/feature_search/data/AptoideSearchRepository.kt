package cm.aptoide.pt.feature_search.data

import cm.aptoide.pt.feature_search.data.database.LocalSearchHistoryRepository
import cm.aptoide.pt.feature_search.data.database.model.SearchHistoryEntity
import cm.aptoide.pt.feature_search.data.network.RemoteSearchRepository
import cm.aptoide.pt.feature_search.domain.model.AutoCompletedApp
import cm.aptoide.pt.feature_search.domain.model.SearchApp
import cm.aptoide.pt.feature_search.domain.model.SearchSuggestion
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository.AutoCompleteResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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
