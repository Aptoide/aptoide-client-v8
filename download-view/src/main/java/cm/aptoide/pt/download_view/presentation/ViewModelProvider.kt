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
private fun rememberDownloadViewModel(app: App): DownloadViewModel {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  return viewModel(
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
}

/**
 * Returns a one-shot check for whether the next regular install start is the automatic
 * continuation of an inline install whose start was already reported - callers use it to
 * skip repeating install-start side effects (analytics, callbacks). Backed by the same
 * per-package [DownloadViewModel] as [rememberDownloadState].
 */
@Composable
fun rememberConsumeInlineFallback(app: App): () -> Boolean = runPreviewable(
  preview = { { false } },
  real = { rememberDownloadViewModel(app)::consumeFallbackContinuation }
)

@Composable
fun rememberDownloadState(
  app: App,
  onInlineInstallLaunched: (isUpdate: Boolean) -> Unit = {},
): DownloadUiState? = runPreviewable(
  preview = { downloadUiStates.random() },
  real = {
    val downloadViewViewModel = rememberDownloadViewModel(app)

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
          .onSuccess {
            // A fallback stage continues the same user action - the click/campaign
            // analytics already fired on the primary launch
            if (!launch.isFallback) onInlineInstallLaunched(launch.isUpdate)
          }
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
