package cm.aptoide.pt.feature_search.domain.usecase

import cm.aptoide.pt.feature_search.domain.repository.SearchRepository
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository.AutoCompleteResult
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class GetSearchAutoCompleteUseCase @Inject constructor(private val searchRepository: SearchRepository) {

  fun getAutoCompleteSuggestions(query: String): Flow<AutoCompleteResult> {
    return searchRepository.getAutoCompleteSuggestions(query)
  }
}