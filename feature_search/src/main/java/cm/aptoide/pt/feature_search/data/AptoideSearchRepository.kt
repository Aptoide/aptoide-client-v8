package cm.aptoide.pt.feature_search.data

import cm.aptoide.pt.feature_search.data.database.LocalSearchHistoryRepository
import cm.aptoide.pt.feature_search.data.database.model.SearchHistoryEntity
import cm.aptoide.pt.feature_search.data.network.RemoteSearchRepository
import cm.aptoide.pt.feature_search.domain.model.AutoCompletedApp
import cm.aptoide.pt.feature_search.domain.model.SearchApp
import cm.aptoide.pt.feature_search.domain.model.SearchSuggestion
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository.AutoCompleteResult
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository.SearchAppResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AptoideSearchRepository @Inject constructor(
  private val localSearchHistoryRepository: LocalSearchHistoryRepository,
  private val remoteSearchRepository: RemoteSearchRepository
) : SearchRepository {

  override fun searchApp(keyword: String): Flow<SearchAppResult> {
    return flow {
      val searchResponse = remoteSearchRepository.searchApp(keyword)
      if (searchResponse.isSuccessful) {
        searchResponse.body()?.datalist?.list?.let {
          emit(SearchAppResult.Success(it.map { searchAppJsonList ->
            SearchApp(
              searchAppJsonList.name,
              searchAppJsonList.icon,
              searchAppJsonList.stats.rating.avg,
              searchAppJsonList.stats.downloads,
              searchAppJsonList.file.malware.rank
            )
          }))
        }
      } else {
        emit(SearchAppResult.Error(IllegalStateException()))
      }
    }.flowOn(Dispatchers.IO).catch { throwable -> throwable.printStackTrace() }
  }

  override fun getSearchHistory(): Flow<List<SearchSuggestion>> {
    return localSearchHistoryRepository.getSearchHistory()
      .map { it.map { historyApp -> SearchSuggestion(historyApp.appName) } }
  }

  override suspend fun addAppToSearchHistory(appName: String) {
    withContext(Dispatchers.IO) {
      localSearchHistoryRepository.addAppToSearchHistory(SearchHistoryEntity(appName))
    }
  }

  override suspend fun removeAppFromSearchHistory(appName: String) {
    withContext(Dispatchers.IO) {
      localSearchHistoryRepository.removeAppFromSearchHistory(SearchHistoryEntity(appName))
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
