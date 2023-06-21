package cm.aptoide.pt.feature_search.presentation

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_search.domain.model.SearchSuggestions

sealed interface SearchUiState {

  val isLoading: Boolean
  val errorMessages: Boolean
  val searchSuggestions: SearchSuggestions
  val searchTextInput: String
  val searchAppBarState: SearchAppBarState
  val searchResults: List<App>

  data class HasSearchSuggestions(
    override val isLoading: Boolean,
    override val errorMessages: Boolean,
    override val searchSuggestions: SearchSuggestions,
    override val searchTextInput: String,
    override val searchAppBarState: SearchAppBarState,
    override val searchResults: List<App>
  ) : SearchUiState
}

