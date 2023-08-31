package cm.aptoide.pt.download_view.presentation

import androidx.core.content.pm.PackageInfoCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.download_view.domain.model.PayloadMapper
import cm.aptoide.pt.download_view.domain.model.getInstallPackageInfo
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.install_manager.InstallManager
import cm.aptoide.pt.install_manager.Task
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@Suppress("OPT_IN_USAGE")
class DownloadViewModel constructor(
  private val app: App,
  installManager: InstallManager,
  private val installedAppOpener: InstalledAppOpener,
  private val payloadMapper: PayloadMapper
) : ViewModel() {

  private val appInstaller = installManager.getApp(app.packageName)

  private val viewModelState = MutableStateFlow<DownloadUiState>(DownloadUiState.Install)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      combine(
        appInstaller.packageInfo,
        appInstaller.tasks.flatMapConcat { it?.stateAndProgress ?: flowOf(null) }
      ) { packageInfo, task -> Pair(packageInfo, task) }
        .catch { throwable -> throwable.printStackTrace() }
        .collect { status ->
          viewModelState.update { state ->
            when (status.second?.first) {
              null -> if (state == DownloadUiState.Error) {
                state
              } else {
                status.first?.let {
                  if (PackageInfoCompat.getLongVersionCode(it) < app.versionCode) {
                    DownloadUiState.Outdated
                  } else {
                    DownloadUiState.Installed
                  }
                } ?: DownloadUiState.Install
              }

              Task.State.ABORTED,
              Task.State.CANCELED,
              -> DownloadUiState.Install

              Task.State.PENDING -> DownloadUiState.Processing
              Task.State.DOWNLOADING -> DownloadUiState.Downloading(status.second?.second ?: 0)
              Task.State.INSTALLING,
              Task.State.UNINSTALLING,
              -> DownloadUiState.Installing(status.second?.second ?: 0)

              Task.State.COMPLETED -> DownloadUiState.Installed
              Task.State.FAILED -> DownloadUiState.Error
              Task.State.READY_TO_INSTALL -> DownloadUiState.ReadyToInstall
            }
          }
        }
    }
  }

  fun downloadApp(app: App) {
    viewModelScope.launch {
      viewModelState.update { DownloadUiState.Processing }
      appInstaller.install(app.getInstallPackageInfo(payloadMapper))
      app.campaigns?.sendInstallClickEvent()
    }
  }

  fun cancelDownload() {
    viewModelScope.launch {
      appInstaller.tasks.first()?.cancel()
    }
  }

  fun openApp() {
    installedAppOpener.openInstalledApp(app.packageName)
  }
}
