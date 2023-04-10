package cm.aptoide.pt.feature_categories.presentation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.feature_categories.domain.usecase.GetCategoriesListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

interface CategoriesDependenciesProvider {
  val getCategoriesListUseCase: GetCategoriesListUseCase
}

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val provider: CategoriesDependenciesProvider,
) : ViewModel()

@Composable
fun CategoriesViewModel(requestUrl: String): CategoriesViewModel {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  return viewModel(
    key = requestUrl,
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return CategoriesViewModel(
          categoriesWidgetUrl = requestUrl,
          getCategoriesListUseCase = injectionsProvider.provider.getCategoriesListUseCase,
        ) as T
      }
    }
  )
}
