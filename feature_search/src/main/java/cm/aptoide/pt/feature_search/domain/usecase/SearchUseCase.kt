package cm.aptoide.pt.feature_search.domain.usecase

import cm.aptoide.pt.feature_search.domain.model.SearchSuggestionType.AUTO_COMPLETE
import cm.aptoide.pt.feature_search.domain.model.SearchSuggestionType.SEARCH_HISTORY
import cm.aptoide.pt.feature_search.domain.model.SearchSuggestions
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository.AutoCompleteResult
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository.PopularAppSearchResult
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository.SearchAppResult
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.util.Collections
import javax.inject.Inject

@ViewModelScoped
class SearchUseCase @Inject constructor(private val searchRepository: SearchRepository) {

  fun searchApp(keyword: String): Flow<SearchAppResult> {
    return searchRepository.searchApp(keyword).onStart { addAppToSearchHistory(keyword) }
  }

  private suspend fun addAppToSearchHistory(appName: String) {
    searchRepository.addAppToSearchHistory(appName)
  }

  suspend fun removeSearchHistoryApp(appName: String) {
    searchRepository.removeAppFromSearchHistory(appName)
  }

  fun getSearchSuggestions(): Flow<SearchSuggestions> {
    return combine(
      searchRepository.getTopSearchedApps()
        .map {
          if (it is PopularAppSearchResult.Success) {
            return@map it.data
          } else {
            return@map Collections.emptyList()
          }
        }
        .catch {
          it.printStackTrace()
          emit(Collections.emptyList())
        },
      searchRepository.getSearchHistory()
        .catch { emit(Collections.emptyList()) },
    ) { popularApps, searchHistory ->
      SearchSuggestions(
        suggestionType = SEARCH_HISTORY,
        suggestionsList = searchHistory,
        popularSearchList = popularApps
      )
    }
  }

  fun getAutoCompleteSuggestions(query: String): Flow<SearchSuggestions> {
    return combine(
      searchRepository.getTopSearchedApps()
        .map {
          if (it is PopularAppSearchResult.Success) {
            return@map it.data
          } else {
            return@map Collections.emptyList()
          }
        }
        .catch {
          it.printStackTrace()
          emit(Collections.emptyList())
        },
      searchRepository.getAutoCompleteSuggestions(query)
        .map {
          if (it is AutoCompleteResult.Success) {
            return@map it.data
          } else {
            return@map Collections.emptyList()
          }
        }
        .catch {
          it.printStackTrace()
          emit(Collections.emptyList())
        },
    ) { popularApps, autoCompleteSuggestions ->
      SearchSuggestions(
        suggestionType = AUTO_COMPLETE,
        suggestionsList = autoCompleteSuggestions,
        popularSearchList = popularApps
      )
    }
  }
}
