package cm.aptoide.pt.feature_search.domain.usecase

import cm.aptoide.pt.feature_search.domain.model.SearchSuggestion
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ViewModelScoped
class GetSearchSuggestionsCase @Inject constructor(private val searchRepository: SearchRepository) {

  fun getSearchSuggestions(): Flow<List<SearchSuggestion>> {
    return searchRepository.getSearchHistory().flatMapMerge {
      if (it.isEmpty()) {
        return@flatMapMerge searchRepository.getTopSearchedApps()
      } else {
        return@flatMapMerge flow { it }
      }
    }
  }
}