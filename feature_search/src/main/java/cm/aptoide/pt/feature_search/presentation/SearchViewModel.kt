package cm.aptoide.pt.feature_search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository
import cm.aptoide.pt.feature_search.domain.usecase.SearchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
  private val searchUseCase: SearchUseCase,
) : ViewModel() {

  private val viewModelState = MutableStateFlow<SearchUiState>(SearchUiState.FirstLoading)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      searchUseCase.getSearchSuggestions()
        .catch { viewModelState.update { SearchUiState.Error } }
        .collect { searchSuggestions ->
          if (viewModelState.value !is SearchUiState.ResultsLoading) {
            viewModelState.update {
              SearchUiState.Suggestions(searchSuggestions = searchSuggestions)
            }
          }
        }
    }
  }

  fun onSelectSearchSuggestion(searchSuggestion: String) {
    searchApp(searchSuggestion)
  }

  fun onRemoveSearchSuggestion(searchSuggestion: String) {
    viewModelScope.launch {
      searchUseCase.removeSearchHistoryApp(searchSuggestion)
    }
  }

  fun onSearchInputValueChanged(input: String) {
    viewModelScope.launch {
      if (input.isNotEmpty()) {
        searchUseCase.getAutoCompleteSuggestions(input)
          .catch { viewModelState.update { SearchUiState.Error } }
          .collect { autoCompleteSuggestions ->
            viewModelState.update {
              SearchUiState.Suggestions(searchSuggestions = autoCompleteSuggestions)
            }
          }
      } else {
        searchUseCase.getSearchSuggestions()
          .catch { viewModelState.update { SearchUiState.Error } }
          .collect { historySuggestions ->
            viewModelState.update {
              SearchUiState.Suggestions(searchSuggestions = historySuggestions)
            }
          }
      }
    }
  }

  fun searchApp(query: String) {
    viewModelState.update { SearchUiState.ResultsLoading }
    viewModelScope.launch {
      searchUseCase.searchApp(query)
        .catch { throwable ->
          throwable.printStackTrace()
          viewModelState.update {
            when (throwable) {
              is IOException -> SearchUiState.NoConnection
              else -> SearchUiState.Error
            }
          }
        }
        .collect { searchAppResult ->
          viewModelState.update {
            when (searchAppResult) {
              is SearchRepository.SearchAppResult.Success -> {
                SearchUiState.Results(searchAppResult.data)
              }

              is SearchRepository.SearchAppResult.Error -> {
                searchAppResult.error.printStackTrace()
                SearchUiState.Error
              }
            }
          }
        }
    }
  }
}
