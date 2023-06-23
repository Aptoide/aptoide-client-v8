package cm.aptoide.pt.feature_search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_search.domain.model.SearchSuggestion
import cm.aptoide.pt.feature_search.domain.model.SearchSuggestionType.AUTO_COMPLETE
import cm.aptoide.pt.feature_search.domain.model.SearchSuggestionType.TOP_APTOIDE_SEARCH
import cm.aptoide.pt.feature_search.domain.model.SearchSuggestions
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository
import cm.aptoide.pt.feature_search.domain.usecase.GetSearchAutoCompleteUseCase
import cm.aptoide.pt.feature_search.domain.usecase.GetSearchSuggestionsUseCase
import cm.aptoide.pt.feature_search.domain.usecase.RemoveSearchHistoryUseCase
import cm.aptoide.pt.feature_search.domain.usecase.SaveSearchHistoryUseCase
import cm.aptoide.pt.feature_search.domain.usecase.SearchAppUseCase
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
  private val getSearchSuggestionsUseCase: GetSearchSuggestionsUseCase,
  private val getSearchAutoCompleteUseCase: GetSearchAutoCompleteUseCase,
  private val searchAppUseCase: SearchAppUseCase,
  private val saveSearchHistoryUseCase: SaveSearchHistoryUseCase,
  private val removeSearchHistoryUseCase: RemoveSearchHistoryUseCase
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
      getSearchSuggestionsUseCase.getSearchSuggestions().collect { searchSuggestions ->
        if (viewModelState.value !is SearchUiState.ResultsLoading) {
          viewModelState.update {
            SearchUiState.Suggestions(searchSuggestions = searchSuggestions)
          }
        }
      }
    }
  }

  fun updateSearchAppBarState(isFocused: Boolean) {
    viewModelState.update {
      if (isFocused)
        SearchUiState.Suggestions(
          SearchSuggestions(AUTO_COMPLETE, emptyList())
        )
      else
        SearchUiState.Suggestions(
          SearchSuggestions(TOP_APTOIDE_SEARCH, emptyList())
        )
    }
  }

  fun onSelectSearchSuggestion(searchSuggestion: String) {
    searchApp(searchSuggestion)
  }

  fun onRemoveSearchSuggestion(searchSuggestion: String) {
    viewModelScope.launch {
      removeSearchHistoryUseCase.removeSearchHistoryApp(searchSuggestion)
    }
  }

  fun onSearchInputValueChanged(input: String) {
    viewModelScope.launch {
      getSearchAutoCompleteUseCase.getAutoCompleteSuggestions(input)
        .catch { throwable -> throwable.printStackTrace() }
        .collect { autoCompleteSuggestions ->
          viewModelState.update {
            when (autoCompleteSuggestions) {
              is SearchRepository.AutoCompleteResult.Success -> {
                SearchUiState.Suggestions(
                  SearchSuggestions(
                    suggestionType = AUTO_COMPLETE,
                    suggestionsList = autoCompleteSuggestions.data.map { SearchSuggestion(it.appName) })
                )
              }

              is SearchRepository.AutoCompleteResult.Error -> {
                autoCompleteSuggestions.error.printStackTrace()
                SearchUiState.Suggestions(
                  SearchSuggestions(
                    suggestionType = AUTO_COMPLETE,
                    suggestionsList = emptyList()
                  )
                )
              }
            }
          }
        }
    }
  }

  fun searchApp(query: String) {
    viewModelState.update { SearchUiState.ResultsLoading }

    viewModelScope.launch {
      saveSearchHistoryUseCase.addAppToSearchHistory(query)
      searchAppUseCase.searchApp(query)
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
                it
              }
            }
          }
        }
    }
  }
}
