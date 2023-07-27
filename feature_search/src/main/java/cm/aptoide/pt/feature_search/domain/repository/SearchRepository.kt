package cm.aptoide.pt.feature_search.domain.repository

import cm.aptoide.pt.feature_apps.data.App
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
  fun searchApp(keyword: String): Flow<SearchAppResult>

  fun getSearchHistory(): Flow<List<String>>

  suspend fun addAppToSearchHistory(appName: String)

  suspend fun removeAppFromSearchHistory(appName: String)

  fun getAutoCompleteSuggestions(keyword: String): Flow<AutoCompleteResult>

  fun getTopSearchedApps(): Flow<PopularAppSearchResult>

  sealed interface AutoCompleteResult {
    data class Success(val data: List<String>) : AutoCompleteResult
    data class Error(val error: Throwable) : AutoCompleteResult
  }

  sealed interface SearchAppResult {
    data class Success(val data: List<App>) : SearchAppResult
    data class Error(val error: Throwable) : SearchAppResult
  }

  sealed interface PopularAppSearchResult {
    data class Success(val data: List<String>) : PopularAppSearchResult
    data class Error(val error: Throwable) : PopularAppSearchResult
  }
}
