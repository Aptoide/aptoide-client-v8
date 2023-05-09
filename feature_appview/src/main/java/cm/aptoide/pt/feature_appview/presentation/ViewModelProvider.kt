package cm.aptoide.pt.feature_appview.presentation

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.feature_appview.domain.AppInfoUseCase
import cm.aptoide.pt.feature_appview.domain.AppVersionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val appInfoUseCase: AppInfoUseCase,
  val appVersionsUseCase: AppVersionsUseCase,
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
          getOtherVersionsUseCase = injectionsProvider.appVersionsUseCase,
          packageName = packageName,
          adListId = adListId
        ) as T
      }
    }
  )
}
