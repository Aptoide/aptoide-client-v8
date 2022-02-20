package cm.aptoide.pt.feature_search.domain.usecase

import cm.aptoide.pt.feature_search.domain.model.SearchSuggestion
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ViewModelScoped
class GetSearchSuggestionsCase @Inject constructor(private val searchRepository: SearchRepository) {

  fun getSearchSuggestions(): Flow<List<SearchSuggestion>> {
    return searchRepository.getSearchHistory()
  }
}