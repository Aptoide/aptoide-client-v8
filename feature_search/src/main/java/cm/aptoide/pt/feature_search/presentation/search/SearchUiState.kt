package cm.aptoide.pt.feature_search.presentation.search

sealed interface SearchUiState {

  val isLoading: Boolean
  val errorMessages: Boolean
  val searchSuggestions: List<String>

  data class HasSearchSuggestions(
    override val isLoading: Boolean,
    override val errorMessages: Boolean,
    override val searchSuggestions: List<String>
  ) : SearchUiState
}

