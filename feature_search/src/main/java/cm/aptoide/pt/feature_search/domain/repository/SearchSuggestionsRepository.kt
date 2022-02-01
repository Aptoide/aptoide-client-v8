package cm.aptoide.pt.feature_search.domain.repository

import cm.aptoide.pt.feature_search.domain.model.SuggestedApp

interface SearchSuggestionsRepository {
  suspend fun getSearchSuggestions(keyword: String): List<SuggestedApp>
}