package cm.aptoide.pt.feature_search.domain.usecase

import cm.aptoide.pt.feature_search.domain.Result
import cm.aptoide.pt.feature_search.domain.model.SuggestedApp
import cm.aptoide.pt.feature_search.domain.repository.SearchSuggestionsRepository


class GetSearchSuggestionsUseCase(private val searchSuggestionsRepository: SearchSuggestionsRepository) {

  suspend fun getSearchSuggestions(keyword: String): Result<List<SuggestedApp>> {
    return try {
      Result.Success(searchSuggestionsRepository.getSearchSuggestions(keyword))
    } catch (e: Exception) {
      Result.Error(e)
    }
  }
}