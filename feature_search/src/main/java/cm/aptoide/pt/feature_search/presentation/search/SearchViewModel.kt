package cm.aptoide.pt.feature_search.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_search.domain.model.SearchSuggestionType
import cm.aptoide.pt.feature_search.domain.usecase.GetSearchAutoCompleteUseCase
import cm.aptoide.pt.feature_search.domain.usecase.GetSearchSuggestionsUseCase
import cm.aptoide.pt.feature_search.domain.usecase.GetTopSearchedAppsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
  getSearchSuggestionsUseCase: GetSearchSuggestionsUseCase,
  getSearchAutoCompleteUseCase: GetSearchAutoCompleteUseCase,
  getTopSearchedAppsUseCase: GetTopSearchedAppsUseCase,
) : ViewModel() {

  private val viewModelState = MutableStateFlow(
    SearchViewModelState(
      isLoading = true,
      searchSuggestionType = SearchSuggestionType.TOP_APTOIDE_SEARCH
    )
  )

  val uiState = viewModelState.map { it.toUiState() }
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value.toUiState()
    )

  init {
    viewModelScope.launch {
      getSearchSuggestionsUseCase.getSearchSuggestions().collect { searchSuggestions ->
        viewModelState.update {
          it.copy(
            searchSuggestions = searchSuggestions.suggestionsList.map { it.appName },
            searchSuggestionType = searchSuggestions.suggestionType
          )
        }
      }
    }

  }

}

private data class SearchViewModelState(
  val searchSuggestions: List<String> = emptyList(),
  val searchSuggestionType: SearchSuggestionType,
  val isLoading: Boolean = false,
  val hasErrors: Boolean = false
) {

  fun toUiState(): SearchUiState =
    //if (!hasErrors) {
    SearchUiState.HasSearchSuggestions(
      isLoading = isLoading,
      errorMessages = hasErrors,
      searchSuggestions = searchSuggestions,
      searchSuggestionType = searchSuggestionType
    )
  //}

}