package cm.aptoide.pt.feature_search.domain.usecase

import cm.aptoide.pt.feature_search.domain.Result
import cm.aptoide.pt.feature_search.domain.model.SuggestedApp
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class GetSearchAutoCompleteUseCase @Inject constructor(private val searchRepository: SearchRepository) {

  suspend fun getSearchSuggestions(keyword: String): Result<List<SuggestedApp>> {
    return try {
      Result.Success(searchRepository.getSearchAutoComplete(keyword))
    } catch (e: Exception) {
      Result.Error(e)
    }
  }
}