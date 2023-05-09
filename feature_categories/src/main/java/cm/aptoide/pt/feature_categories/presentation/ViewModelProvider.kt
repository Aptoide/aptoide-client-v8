package cm.aptoide.pt.feature_categories.presentation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.feature_categories.data.CategoriesRepository
import cm.aptoide.pt.feature_categories.domain.CategoryAppsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val categoriesRepository: CategoriesRepository,
  val categoryAppsUseCase: CategoryAppsUseCase,
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
          categoriesRepository = injectionsProvider.categoriesRepository,
        ) as T
      }
    }
  )
}

@Composable
fun categoryAppsViewModel(categoryName: String): CategoryAppsViewModel {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  return viewModel(
    key = categoryName,
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return CategoryAppsViewModel(
          categoryName = categoryName,
          categoryAppsUseCase = injectionsProvider.categoryAppsUseCase,
        ) as T
      }
    }
  )
}
