package cm.aptoide.pt.feature_search.domain.usecase

import cm.aptoide.pt.feature_search.domain.model.SearchHistory
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow

class GetSearchSuggestionsCase(private val searchRepository: SearchRepository) {

  fun getSearchSuggestions(): Flow<List<SearchHistory>> {
    return searchRepository.getSearchHistory()
  }
}