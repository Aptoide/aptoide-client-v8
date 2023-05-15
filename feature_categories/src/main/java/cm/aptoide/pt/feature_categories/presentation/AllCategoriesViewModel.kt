package cm.aptoide.pt.feature_categories.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_categories.data.CategoriesRepository
import cm.aptoide.pt.feature_categories.domain.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AllCategoriesViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val categoriesRepository: CategoriesRepository
) : ViewModel() {

  private val categoryBundleTag: String? = savedStateHandle.get("tag")
  private val viewModelState = MutableStateFlow(MoreCategoriesViewModelState())

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
      viewModelState.update { it.copy(type = AllCategoriesUiStateType.LOADING) }
      categoryBundleTag?.let { tag ->
        try {
          val categories = categoriesRepository.getHomeBundleActionListCategories(tag)
          viewModelState.update {
            it.copy(
              categoryList = categories,
              type = AllCategoriesUiStateType.IDLE
            )
          }
        } catch (e: Throwable) {
          Timber.w(e)
          viewModelState.update {
            it.copy(
              type = when (e) {
                is IOException -> AllCategoriesUiStateType.NO_CONNECTION
                else -> AllCategoriesUiStateType.ERROR
              }
            )
          }
        }

      }
    }
  }
}

private data class MoreCategoriesViewModelState(
  val categoryList: List<Category> = emptyList(),
  val type: AllCategoriesUiStateType = AllCategoriesUiStateType.IDLE,
) {
  fun toUiState(): AllCategoriesUiState =
    AllCategoriesUiState(
      categoryList = categoryList,
      type = type
    )
}
