package cm.aptoide.pt.feature_search.domain.usecase

import cm.aptoide.pt.feature_search.domain.model.SearchSuggestionType
import cm.aptoide.pt.feature_search.domain.model.SearchSuggestions
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ViewModelScoped
class GetSearchSuggestionsUseCase @Inject constructor(private val searchRepository: SearchRepository) {

  fun getSearchSuggestions(): Flow<SearchSuggestions> {
    return searchRepository.getSearchHistory().flatMapMerge {
      if (it.isEmpty()) {
        return@flatMapMerge searchRepository.getTopSearchedApps().map { topSearchedApps ->
          SearchSuggestions(
            SearchSuggestionType.TOP_APTOIDE_SEARCH, topSearchedApps
          )
        }
      } else {
        return@flatMapMerge flow {
          it.map { searchHistory ->
            SearchSuggestions(
              SearchSuggestionType.SEARCH_HISTORY, it
            )
          }
        }
      }
    }
  }
}