package cm.aptoide.pt.feature_search.presentation.search

import cm.aptoide.pt.feature_search.domain.model.SearchSuggestionType

sealed interface SearchUiState {

  val isLoading: Boolean
  val errorMessages: Boolean
  val searchSuggestions: List<String>
  val searchSuggestionType: SearchSuggestionType
  val searchTextInput: String

  data class HasSearchSuggestions(
    override val isLoading: Boolean,
    override val errorMessages: Boolean,
    override val searchSuggestions: List<String>,
    override val searchSuggestionType: SearchSuggestionType,
    override val searchTextInput: String
  ) : SearchUiState
}

