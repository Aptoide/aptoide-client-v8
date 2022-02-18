package cm.aptoide.pt.feature_search.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_search.domain.usecase.GetSearchAutoCompleteUseCase
import cm.aptoide.pt.feature_search.domain.usecase.GetSearchSuggestionsCase
import cm.aptoide.pt.feature_search.domain.usecase.GetTopSearchedAppsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
  getSearchSuggestionsCase: GetSearchSuggestionsCase,
  getSearchAutoCompleteUseCase: GetSearchAutoCompleteUseCase,
  getTopSearchedAppsUseCase: GetTopSearchedAppsUseCase,
) : ViewModel() {

  private val viewModelState = MutableStateFlow(SearchViewModelState(isLoading = true))

  val uiState = viewModelState.map { it.toUiState() }
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value.toUiState()
    )

  init {
    viewModelScope.launch {
      getSearchSuggestionsCase.getSearchSuggestions().collect { searchSuggestions ->
        viewModelState.update { it.copy(searchSuggestions = searchSuggestions.map { it.appName }) }
      }
    }

  }

  /*val searchHistory: Flow<List<String>> =
    flow {
      val searchHistory =
        getSearchSuggestionsCase.getSearchHistory().also { result ->
          val history = when (result) {
            is Result.Success ->
              result.data.map { it.appName }
            is Result.Error -> Collections.emptyList()
            is Result.Loading -> Collections.emptyList()
          }
          emit(history)
        }
    }*/


}

private data class SearchViewModelState(
  val searchSuggestions: List<String> = emptyList(),
  val isLoading: Boolean = false,
  val hasErrors: Boolean = false
) {

  fun toUiState(): SearchUiState =
    //if (!hasErrors) {
    SearchUiState.HasSearchSuggestions(
      isLoading = isLoading,
      errorMessages = hasErrors,
      searchSuggestions = searchSuggestions
    )
  //}

}