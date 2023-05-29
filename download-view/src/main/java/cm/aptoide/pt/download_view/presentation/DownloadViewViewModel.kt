package cm.aptoide.pt.download_view.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.download_view.domain.model.getInstallPackageInfo
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.install_manager.InstallManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@Suppress("OPT_IN_USAGE")
class DownloadViewViewModel constructor(
  private val app: App,
  installManager: InstallManager,
  private val installedAppOpener: InstalledAppOpener
) : ViewModel() {

  private val appInstaller = installManager.getApp(app.packageName)

  private val viewModelState = MutableStateFlow(
    DownloadViewUiState()
  )

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
        .collect { pair -> viewModelState.update { it.copyWith(app, pair) } }
    }
  }

  fun downloadApp(app: App) {
    viewModelScope.launch {
      if (appInstaller.tasks.first() == null) {
        viewModelState.update { it.copy(downloadViewState = DownloadViewState.PROCESSING) }
        appInstaller.install(app.getInstallPackageInfo())
        app.campaigns?.sendClickEvent()
      }
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
