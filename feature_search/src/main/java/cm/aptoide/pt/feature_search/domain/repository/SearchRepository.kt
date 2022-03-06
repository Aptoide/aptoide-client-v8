package cm.aptoide.pt.feature_search.domain.repository

import cm.aptoide.pt.feature_search.domain.model.AutoCompletedApp
import cm.aptoide.pt.feature_search.domain.model.SearchApp
import cm.aptoide.pt.feature_search.domain.model.SearchSuggestion
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
  fun searchApp(keyword: String): Flow<SearchAppResult>

  fun getSearchHistory(): Flow<List<SearchSuggestion>>

  suspend fun addAppToSearchHistory(appName: String)

  fun getAutoCompleteSuggestions(keyword: String): Flow<AutoCompleteResult>

  fun getTopSearchedApps(): Flow<List<SearchSuggestion>>


  sealed interface AutoCompleteResult {
    data class Success(val data: List<AutoCompletedApp>) : AutoCompleteResult
    data class Error(val error: Throwable) : AutoCompleteResult
  }

  sealed interface SearchAppResult {
    data class Success(val data: List<SearchApp>) : SearchAppResult
    data class Error(val error: Throwable) : SearchAppResult
  }
}