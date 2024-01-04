package cm.aptoide.pt.download_view.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.download_view.domain.model.PayloadMapper
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Install
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.install_manager.InstallManager
import cm.aptoide.pt.network_listener.NetworkConnectionImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

interface InstallAppUseCaseProvider {
  val installManager: InstallManager
}

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val provider: InstallAppUseCaseProvider,
  val networkConnectionImpl: NetworkConnectionImpl,
  val installedAppOpener: InstalledAppOpener,
  val payloadMapper: PayloadMapper,
) : ViewModel()

@Composable
fun rememberDownloadState(app: App): DownloadUiState? = runPreviewable(
  preview = {
    Install(installWith = {})
  },
  real = {
    val injectionsProvider = hiltViewModel<InjectionsProvider>()
    val downloadViewViewModel: DownloadViewModel = viewModel(
      key = app.packageName,
      factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          @Suppress("UNCHECKED_CAST")
          return DownloadViewModel(
            app = app,
            installManager = injectionsProvider.provider.installManager,
            networkConnectionImpl = injectionsProvider.networkConnectionImpl,
            installedAppOpener = injectionsProvider.installedAppOpener,
            payloadMapper = injectionsProvider.payloadMapper,
          ) as T
        }
      }
    )
    val downloadUiState by downloadViewViewModel.uiState.collectAsState()

    downloadUiState
  }
)
