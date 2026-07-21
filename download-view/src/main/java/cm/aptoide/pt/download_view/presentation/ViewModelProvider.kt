package cm.aptoide.pt.download_view.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.download_view.di.UIInstallPackageInfoMapper
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.install_info_mapper.domain.InstallPackageInfoMapper
import cm.aptoide.pt.install_manager.InstallManager
import cm.aptoide.pt.network_listener.NetworkConnectionImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import java.util.Optional
import javax.inject.Inject

interface InstallAppUseCaseProvider {
  val installManager: InstallManager
}

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val provider: InstallAppUseCaseProvider,
  val networkConnectionImpl: NetworkConnectionImpl,
  val installedAppOpener: InstalledAppOpener,
  @UIInstallPackageInfoMapper val installPackageInfoMapper: InstallPackageInfoMapper,
  val inlineInstallResolver: Optional<InlineInstallResolver>,
) : ViewModel()

@Composable
fun rememberDownloadState(
  app: App,
  onInlineInstallLaunched: (isUpdate: Boolean) -> Unit = {},
): DownloadUiState? = runPreviewable(
  preview = { downloadUiStates.random() },
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
            installPackageInfoMapper = injectionsProvider.installPackageInfoMapper,
            inlineInstallResolver = injectionsProvider.inlineInstallResolver.orElse(null)
          ) as T
        }
      }
    )

    // Installation completion is observed through the package updates already feeding uiState;
    // the activity result only signals the external install UI being closed
    val inlineInstallLauncher = rememberLauncherForActivityResult(
      contract = ActivityResultContracts.StartActivityForResult()
    ) {
      downloadViewViewModel.onInlineInstallClosed()
    }
    LaunchedEffect(downloadViewViewModel) {
      downloadViewViewModel.inlineInstallEvents.collect { launch ->
        runCatching {
          Timber.tag(DownloadViewModel.INLINE_INSTALL_TAG)
            .d("Launching inline install intent: ${launch.intent.data}")
          inlineInstallLauncher.launch(launch.intent)
        }
          .onSuccess { onInlineInstallLaunched(launch.isUpdate) }
          .onFailure {
            Timber.tag(DownloadViewModel.INLINE_INSTALL_TAG)
              .w(it, "Inline install intent launch failed")
            downloadViewViewModel.onInlineInstallClosed()
          }
      }
    }

    val downloadUiState by downloadViewViewModel.uiState.collectAsState()

    downloadUiState
  }
)
