package cm.aptoide.pt.feature_categories.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_categories.data.CategoriesRepository
import cm.aptoide.pt.feature_categories.domain.randomCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val categoriesRepository: CategoriesRepository,
) : ViewModel()

@Composable
fun rememberCategoriesViewModel(requestUrl: String): CategoriesViewUiState =
  runPreviewable(
    preview = {
    CategoriesViewUiState(
      loading = false,
      categories = List((0..50).random()) { randomCategory }
    )
  }, real = {
    val injectionsProvider = hiltViewModel<InjectionsProvider>()
    val vm: CategoriesViewModel = viewModel(
      key = requestUrl,
      factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          @Suppress("UNCHECKED_CAST")
          return CategoriesViewModel(
            categoriesWidgetUrl = requestUrl,
            categoriesRepository = injectionsProvider.categoriesRepository,
          ) as T
        }
      }
    )
    val uiState by vm.uiState.collectAsState()
    uiState
  })
