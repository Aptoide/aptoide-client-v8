package cm.aptoide.pt.feature_apps.presentation

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.feature_apps.domain.CategoryAppsUseCase
import cm.aptoide.pt.feature_apps.domain.AppInfoUseCase
import cm.aptoide.pt.feature_apps.domain.AppVersionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val appInfoUseCase: AppInfoUseCase,
  val appVersionsUseCase: AppVersionsUseCase,
  val categoryAppsUseCase: CategoryAppsUseCase,
) : ViewModel()

@Composable
fun appViewModel(packageName: String, adListId: String?): AppViewModel {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  return viewModel(
    viewModelStoreOwner = LocalContext.current as AppCompatActivity,
    key = packageName,
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return AppViewModel(
          appInfoUseCase = injectionsProvider.appInfoUseCase,
          packageName = packageName,
          adListId = adListId
        ) as T
      }
    }
  )
}

@Composable
fun appVersionsViewModel(packageName: String): AppVersionsViewModel {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  return viewModel(
    viewModelStoreOwner = LocalContext.current as AppCompatActivity,
    key = packageName,
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return AppVersionsViewModel(
          appVersionsUseCase = injectionsProvider.appVersionsUseCase,
          packageName = packageName,
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
