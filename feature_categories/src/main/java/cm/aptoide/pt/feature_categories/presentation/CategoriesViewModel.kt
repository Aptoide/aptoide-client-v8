package cm.aptoide.pt.feature_categories.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_categories.data.CategoriesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
      try {
        val categoriesList = categoriesRepository.getCategoriesList(categoriesWidgetUrl)
        viewModelState.update {
          it.copy(
            categories = categoriesList,
            loading = false
          )
        }
      } catch (e: Throwable) {
        Timber.w(e)
        viewModelState.update { it.copy(loading = false) }
      }
    }
  }
}
