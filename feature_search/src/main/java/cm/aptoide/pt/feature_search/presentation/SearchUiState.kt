package cm.aptoide.pt.feature_search.presentation

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_search.domain.model.SearchSuggestions

sealed class SearchUiState {
  data class Results(
    val searchResults: List<App>,
  ) : SearchUiState()

  data class Suggestions(
    val searchSuggestions: SearchSuggestions,
  ) : SearchUiState()
}
