package cm.aptoide.pt.download_view.presentation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.install_manager.InstallManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

interface InstallAppUseCaseProvider {
  val installManager: InstallManager
}

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val provider: InstallAppUseCaseProvider,
  val installedAppOpener: InstalledAppOpener,
) : ViewModel()

@Composable
fun perAppViewModel(app: App): DownloadViewModel {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  return viewModel(
    key = app.packageName,
    factory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return DownloadViewModel(
          app = app,
          installManager = injectionsProvider.provider.installManager,
          installedAppOpener = injectionsProvider.installedAppOpener,
        ) as T
      }
    }
  )
}
