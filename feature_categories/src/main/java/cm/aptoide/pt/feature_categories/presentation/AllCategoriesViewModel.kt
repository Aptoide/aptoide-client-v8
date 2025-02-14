package cm.aptoide.pt.feature_categories.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_categories.domain.CategoriesUseCase
import cm.aptoide.pt.feature_categories.domain.randomCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AllCategoriesViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val categoriesUseCase: CategoriesUseCase,
) : ViewModel() {

  private val categoryBundleTag: String? = savedStateHandle.get("tag")
  private val viewModelState = MutableStateFlow<AllCategoriesUiState>(AllCategoriesUiState.Loading)

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
      viewModelState.update { AllCategoriesUiState.Loading }
      try {
        val categories = categoriesUseCase.getCategories(categoryBundleTag ?: "")
        viewModelState.update {
          AllCategoriesUiState.Idle(categories = categories)
        }
      } catch (e: Throwable) {
        Timber.w(e)
        viewModelState.update {
          when (e) {
            is IOException -> AllCategoriesUiState.NoConnection
            else -> AllCategoriesUiState.Error
          }
        }
      }
    }
  }
}

@Composable
fun rememberAllCategories(): Pair<AllCategoriesUiState, () -> Unit> = runPreviewable(
  preview = {
    AllCategoriesUiState.Idle(List((0..50).random()) { randomCategory }) to {}
  }, real = {
    val vm = hiltViewModel<AllCategoriesViewModel>()
    val uiState by vm.uiState.collectAsState()

    uiState to vm::reload
  }
)
