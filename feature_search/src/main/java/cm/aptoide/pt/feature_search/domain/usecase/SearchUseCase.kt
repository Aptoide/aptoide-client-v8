package cm.aptoide.pt.feature_search.domain.usecase

import cm.aptoide.pt.feature_search.domain.model.SearchSuggestionType.AUTO_COMPLETE
import cm.aptoide.pt.feature_search.domain.model.SearchSuggestionType.SEARCH_HISTORY
import cm.aptoide.pt.feature_search.domain.model.SearchSuggestions
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository.AutoCompleteResult
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository.PopularAppSearchResult
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository.SearchAppResult
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ViewModelScoped
class SearchUseCase @Inject constructor(private val searchRepository: SearchRepository) {

  fun searchApp(keyword: String): Flow<SearchAppResult> {
    return searchRepository.searchApp(keyword)
  }

  suspend fun addAppToSearchHistory(appName: String) {
    searchRepository.addAppToSearchHistory(appName)
  }

  suspend fun removeSearchHistoryApp(appName: String) {
    searchRepository.removeAppFromSearchHistory(appName)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  fun getSearchSuggestions(): Flow<SearchSuggestions> {
    return searchRepository.getTopSearchedApps().flatMapMerge {
      when (it) {
        is PopularAppSearchResult.Success -> {
          return@flatMapMerge searchRepository.getSearchHistory().map { searchHistoryList ->
            SearchSuggestions(
              suggestionType = SEARCH_HISTORY,
              suggestionsList = searchHistoryList,
              popularSearchList = it.data
            )
          }
        }

        is PopularAppSearchResult.Error -> {
          throw it.error
        }
      }
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  fun getAutoCompleteSuggestions(query: String): Flow<SearchSuggestions> {
    return searchRepository.getAutoCompleteSuggestions(query)
      .flatMapMerge { autoComplete ->
        when (autoComplete) {
          is AutoCompleteResult.Success -> {
            return@flatMapMerge searchRepository.getTopSearchedApps().map { result ->
              when (result) {
                is PopularAppSearchResult.Success -> {
                  SearchSuggestions(
                    suggestionType = AUTO_COMPLETE,
                    suggestionsList = autoComplete.data,
                    popularSearchList = result.data
                  )
                }

                is PopularAppSearchResult.Error -> {
                  throw result.error
                }
              }
            }
          }

          is AutoCompleteResult.Error -> {
            throw autoComplete.error
          }
        }
      }
  }
}
