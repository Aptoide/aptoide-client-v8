package cm.aptoide.pt.feature_categories.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_categories.domain.GetCategoryAppsListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class CategoryDetailViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val getCategoryAppsUseCase: GetCategoryAppsListUseCase
) : ViewModel() {

  private val categoryName: String? = savedStateHandle["name"]
  private val viewModelState = MutableStateFlow(CategoryDetailViewModelState())

  val uiState = viewModelState.map { it.toUiState() }
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value.toUiState()
    )

  init {
    reload()
  }

  fun reload() {
    viewModelScope.launch {
      viewModelState.update { it.copy(type = CategoryDetailViewUiStateType.LOADING) }
      categoryName?.let {
        getCategoryAppsUseCase(categoryName)
          .catch { e ->
            Timber.w(e)
            viewModelState.update {
              it.copy(
                type = when (e) {
                  is IOException -> CategoryDetailViewUiStateType.NO_CONNECTION
                  else -> CategoryDetailViewUiStateType.ERROR
                }
              )
            }
          }
          .collect { categoryApps ->
            if (categoryApps.isEmpty()) {
              viewModelState.update { it.copy(type = CategoryDetailViewUiStateType.EMPTY) }
            } else {
              viewModelState.update {
                it.copy(
                  appList = categoryApps,
                  categoryName = categoryName,
                  type = CategoryDetailViewUiStateType.IDLE
                )
              }
            }
          }
      }
    }
  }
}

private data class CategoryDetailViewModelState(
  val appList: List<App> = emptyList(),
  val categoryName: String = "",
  val type: CategoryDetailViewUiStateType = CategoryDetailViewUiStateType.IDLE,
) {
  fun toUiState(): CategoryDetailViewUiState =
    CategoryDetailViewUiState(
      appList = appList,
      categoryName = categoryName,
      type = type
    )
}
