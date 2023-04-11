package cm.aptoide.pt.feature_categories.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_categories.data.CategoriesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

class CategoriesViewModel constructor(
  categoriesWidgetUrl: String,
  categoriesRepository: CategoriesRepository
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
      categoriesRepository.getCategoriesList(categoriesWidgetUrl)
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
