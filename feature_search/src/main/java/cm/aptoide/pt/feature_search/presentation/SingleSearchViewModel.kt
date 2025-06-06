package cm.aptoide.pt.feature_search.presentation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository
import cm.aptoide.pt.feature_search.domain.usecase.SearchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

class SingleSearchViewModel(
  private val query: String? = null,
  private val searchUseCase: SearchUseCase,
) : ViewModel() {

  private var resultsState: SearchUiState? = null

  private val viewModelState = MutableStateFlow<SearchUiState>(SearchUiState.FirstLoading)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    loadDefaultSuggestions()
  }

  private fun loadDefaultSuggestions() {
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
        loadDefaultSuggestions()
      }
    }
  }

  fun hasResults() = resultsState != null

  fun loadResults() {
    viewModelState.update { SearchUiState.ResultsLoading }
    resultsState
      ?.let { it as? SearchUiState.Results }
      ?.searchResults
      ?.takeIf { it.isNotEmpty() }
      ?.let { apps ->
        viewModelState.update { SearchUiState.Results(apps) }
      }
      ?: fetchResults()
  }

  private fun fetchResults() {
    if (query != null) {
      viewModelState.update { SearchUiState.ResultsLoading }
      viewModelScope.launch {
        searchUseCase.searchApp(query)
          .catch { throwable ->
            throwable.printStackTrace()
            viewModelState.updateAndGet {
              when (throwable) {
                is IOException -> SearchUiState.NoConnection
                else -> SearchUiState.Error
              }
            }.also { resultsState = it }
          }
          .collect { searchAppResult ->
            viewModelState.updateAndGet {
              when (searchAppResult) {
                is SearchRepository.SearchAppResult.Success -> {
                  SearchUiState.Results(searchAppResult.data)
                }

                is SearchRepository.SearchAppResult.Error -> {
                  searchAppResult.error.printStackTrace()
                  SearchUiState.Error
                }
              }.also { resultsState = it }
            }
          }
      }
    }
  }
}

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val searchUseCase: SearchUseCase,
) : ViewModel()

@Composable
fun singleSearchViewModel(query: String? = null): SingleSearchViewModel {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  return viewModel(
    key = "search/$query",
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SingleSearchViewModel(
          query = query,
          searchUseCase = injectionsProvider.searchUseCase,
        ) as T
      }
    }
  )
}
