package cm.aptoide.pt.feature_search.data


import cm.aptoide.pt.feature_search.domain.repository.SearchRepository.AutoCompleteResult
import kotlinx.coroutines.flow.Flow

interface AutoCompleteSuggestionsRepository {
  fun getAutoCompleteSuggestions(keyword: String): Flow<AutoCompleteResult>
}
