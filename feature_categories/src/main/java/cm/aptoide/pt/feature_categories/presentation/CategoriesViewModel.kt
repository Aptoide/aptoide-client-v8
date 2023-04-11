package cm.aptoide.pt.feature_categories.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_categories.domain.usecase.GetCategoriesListUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class CategoriesViewModel @Inject constructor(
  categoriesWidgetUrl: String,
  getCategoriesListUseCase: GetCategoriesListUseCase,
) : ViewModel() {

  private val viewModelState = MutableStateFlow(
    CategoriesViewUiState(
      categories = emptyList(),
      loading = true
    )
  )

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      getCategoriesListUseCase(categoriesWidgetUrl)
        .catch { e ->
          Timber.w(e)
          viewModelState.update { it.copy(loading = false) }
        }
        .collect { categoriesList ->
          viewModelState.update {
            it.copy(
              categories = categoriesList,
              loading = false
            )
          }
        }
    }
  }
}
