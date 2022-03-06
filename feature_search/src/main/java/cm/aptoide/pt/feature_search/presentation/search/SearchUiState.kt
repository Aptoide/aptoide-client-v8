package cm.aptoide.pt.feature_search.presentation.search

import cm.aptoide.pt.feature_search.domain.model.SearchSuggestions

sealed interface SearchUiState {

  val isLoading: Boolean
  val errorMessages: Boolean
  val searchSuggestions: SearchSuggestions
  val searchTextInput: String
  val searchAppBarState: SearchAppBarState

  data class HasSearchSuggestions(
    override val isLoading: Boolean,
    override val errorMessages: Boolean,
    override val searchSuggestions: SearchSuggestions,
    override val searchTextInput: String,
    override val searchAppBarState: SearchAppBarState
  ) : SearchUiState
}

