package cm.aptoide.pt.feature_search.data

import cm.aptoide.pt.feature_search.data.network.service.SearchSuggestionsRetrofitService
import cm.aptoide.pt.feature_search.domain.model.SuggestedApp
import cm.aptoide.pt.feature_search.domain.repository.SearchSuggestionsRepository

class RemoteSearchSuggestionsRepository(private val searchSuggestionsService: SearchSuggestionsRetrofitService) :
  SearchSuggestionsRepository {

  override suspend fun getSearchSuggestions(keyword: String): List<SuggestedApp> {
    return searchSuggestionsService.getSearchSuggestions().results.data.map {
      SuggestedApp(
        it
      )
    }
  }
}