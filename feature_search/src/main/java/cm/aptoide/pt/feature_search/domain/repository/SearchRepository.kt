package cm.aptoide.pt.feature_search.domain.repository

import cm.aptoide.pt.feature_search.domain.model.SearchApp
import cm.aptoide.pt.feature_search.domain.model.SearchSuggestion
import cm.aptoide.pt.feature_search.domain.model.SuggestedApp
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
  suspend fun searchApp(keyword: String): List<SearchApp>

  fun getSearchHistory(): Flow<List<SearchSuggestion>>

  suspend fun addAppToSearchHistory(appName: String)

  suspend fun getSearchAutoComplete(keyword: String): List<SuggestedApp>

  fun getTopSearchedApps(): Flow<List<SearchSuggestion>>

}