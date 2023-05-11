package cm.aptoide.pt.feature_apps.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_apps.domain.CategoryAppsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException

class CategoryAppsViewModel constructor(
  private val categoryName: String,
  private val categoryAppsUseCase: CategoryAppsUseCase
) : ViewModel() {

  private val viewModelState = MutableStateFlow<CategoryAppsUiState>(CategoryAppsUiState.Loading)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    reload()
  }

  fun reload() {
    viewModelScope.launch {
      viewModelState.update { CategoryAppsUiState.Loading }
      categoryAppsUseCase.getApps(categoryName)
        .catch { e ->
          Timber.w(e)
          viewModelState.update {
            when (e) {
              is IOException -> CategoryAppsUiState.NoConnection
              else -> CategoryAppsUiState.Error
            }
          }
        }
        .collect { categoryApps ->
          if (categoryApps.isEmpty()) {
            viewModelState.update { CategoryAppsUiState.Empty }
          } else {
            viewModelState.update { CategoryAppsUiState.Idle(categoryApps) }
          }
        }
    }
  }
}
