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
  private val installManager: InstallManager,
  private val installedAppOpener: InstalledAppOpener
) : ViewModel() {

  private val viewModelState =
    MutableStateFlow(DownloadViewUiState(app = app, downloadViewType = app.getDownloadViewType()))

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.WhileSubscribed(),
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      installManager.getApp(app.packageName).run {
        combine(
          packageInfo,
          tasks.flatMapConcat { it?.stateAndProgress ?: flowOf(null) }
        ) { packageInfo, task -> Pair(packageInfo, task) }
      }
        .catch { throwable -> throwable.printStackTrace() }
        .collect { pair -> viewModelState.update { it.copyWith(app, pair) } }
    }
  }

  fun downloadApp(app: App) {
    viewModelScope.launch {
      app.campaigns?.sendClickEvent()
      installManager.getApp(app.packageName)
        .install(app.getInstallPackageInfo())
    }
  }

  fun cancelDownload() {
    viewModelScope.launch {
      installManager.getApp(app.packageName)
        .tasks
        .first()
        ?.cancel()
    }
  }

  fun openApp() {
    installedAppOpener.openInstalledApp(app.packageName)
  }
}
