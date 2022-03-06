package cm.aptoide.pt.feature_search.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_search.domain.model.SearchSuggestion
import cm.aptoide.pt.feature_search.domain.model.SearchSuggestionType.TOP_APTOIDE_SEARCH
import cm.aptoide.pt.feature_search.domain.model.SearchSuggestions
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository
import cm.aptoide.pt.feature_search.domain.usecase.GetSearchAutoCompleteUseCase
import cm.aptoide.pt.feature_search.domain.usecase.GetSearchSuggestionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
  private val getSearchSuggestionsUseCase: GetSearchSuggestionsUseCase,
  private val getSearchAutoCompleteUseCase: GetSearchAutoCompleteUseCase,
) : ViewModel() {

  private val viewModelState = MutableStateFlow(
    SearchViewModelState(
      searchSuggestions = SearchSuggestions(TOP_APTOIDE_SEARCH, emptyList()),
      searchTextInput = ""
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
            searchSuggestions = searchSuggestions
          )
        }
      }
    }

  }

  fun updateSearchAppBarState(searchState: SearchAppBarState) {
    viewModelState.update {
      it.copy(
        searchAppBarState = searchState,
        searchSuggestions = SearchSuggestions(TOP_APTOIDE_SEARCH, emptyList())
      )
    }
  }

  fun onSelectSearchSuggestion(searchSuggestion: String) {
    searchApp(searchSuggestion)
  }

  fun onRemoveSearchSuggestion(searchSuggestion: String) {
    TODO("Not yet implemented")
  }

  fun onSearchInputValueChanged(input: String) {
    viewModelState.update { it.copy(searchTextInput = input) }

    viewModelScope.launch {
      getSearchAutoCompleteUseCase.getAutoCompleteSuggestions(input)
        .collect { autoCompleteSuggestions ->
          viewModelState.update {
            when (autoCompleteSuggestions) {
              is SearchRepository.AutoCompleteResult.Success -> {
                it.copy(
                  searchSuggestions = SearchSuggestions(
                    TOP_APTOIDE_SEARCH,
                    autoCompleteSuggestions.data.map { SearchSuggestion(it.appName) })
                )
              }
              is SearchRepository.AutoCompleteResult.Error -> {
                autoCompleteSuggestions.error.printStackTrace()
                it.copy()
              }
            }
          }
        }
    }
  }

  fun searchApp(query: String) {
    TODO("Not yet implemented")
  }

}

private data class SearchViewModelState(
  val searchSuggestions: SearchSuggestions,
  val searchTextInput: String,
  val isLoading: Boolean = false,
  val hasErrors: Boolean = false,
  val searchAppBarState: SearchAppBarState = SearchAppBarState.CLOSED
) {

  fun toUiState(): SearchUiState =
    //if (!hasErrors) {
    SearchUiState.HasSearchSuggestions(
      isLoading = isLoading,
      errorMessages = hasErrors,
      searchSuggestions = searchSuggestions,
      searchTextInput = searchTextInput,
      searchAppBarState = searchAppBarState
    )
  //}

}