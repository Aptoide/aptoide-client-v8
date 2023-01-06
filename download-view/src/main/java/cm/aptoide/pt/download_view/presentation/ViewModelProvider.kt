package cm.aptoide.pt.download_view.presentation

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.download_view.domain.usecase.InstallAppUseCase
import cm.aptoide.pt.feature_apps.data.App
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

interface InstallAppUseCaseProvider {
  val installAppUseCase: InstallAppUseCase
}

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val provider: InstallAppUseCaseProvider,
  val installedAppOpener: InstalledAppOpener,
) : ViewModel()

@Composable
fun perAppViewModel(app: App): DownloadViewViewModel {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  return viewModel(
    viewModelStoreOwner = LocalContext.current as AppCompatActivity,
    key = app.packageName,
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return DownloadViewViewModel(
          app = app,
          installAppUseCaseInstance = injectionsProvider.provider.installAppUseCase,
          installedAppOpener = injectionsProvider.installedAppOpener,
        ) as T
      }
    }
  )
}
