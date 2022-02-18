package cm.aptoide.pt.feature_search.domain.usecase

import cm.aptoide.pt.feature_search.domain.model.SearchHistory
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class GetSearchSuggestionsCase @Inject constructor(private val searchRepository: SearchRepository) {

  fun getSearchSuggestions(): Flow<List<SearchHistory>> {
    return searchRepository.getSearchHistory()
  }
}