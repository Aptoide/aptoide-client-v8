package cm.aptoide.pt.download_view.presentation

import androidx.core.content.pm.PackageInfoCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.download_view.domain.model.PayloadMapper
import cm.aptoide.pt.download_view.domain.model.getInstallPackageInfo
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.install_manager.InstallManager
import cm.aptoide.pt.install_manager.Task
import cm.aptoide.pt.install_manager.Task.Type.INSTALL
import cm.aptoide.pt.install_manager.Task.Type.UNINSTALL
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

@Suppress("OPT_IN_USAGE")
class DownloadViewModel constructor(
  private val app: App,
  installManager: InstallManager,
  private val installedAppOpener: InstalledAppOpener,
  payloadMapper: PayloadMapper,
  automaticInstall: Boolean,
) : ViewModel() {

  private val appInstaller = installManager.getApp(app.packageName)

  private val installPackageInfo = app.getInstallPackageInfo(payloadMapper)

  private val campaigns = app.campaigns

  private val viewModelState = MutableStateFlow<DownloadUiState>(DownloadUiState.Install(::install))

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    val packageStates = appInstaller.packageInfoFlow.map { info ->
      info?.let {
        if (PackageInfoCompat.getLongVersionCode(it) < app.versionCode) {
          DownloadUiState.Outdated(
            open = ::open,
            update = ::install,
            uninstall = ::uninstall
          )
        } else {
          DownloadUiState.Installed(
            open = ::open,
            uninstall = ::uninstall
          )
        }
      } ?: DownloadUiState.Install(::install)
    }

    val taskStates = appInstaller.taskFlow.flatMapConcat { task ->
      task?.stateAndProgress
        ?.map { (state, progress) ->
          when (state) {
            Task.State.ABORTED,
            Task.State.CANCELED,
            -> null

            Task.State.PENDING -> DownloadUiState.Processing(
              cancel = task::cancel
            )

            Task.State.DOWNLOADING -> DownloadUiState.Downloading(
              cancel = task::cancel,
              downloadProgress = progress
            )

            Task.State.INSTALLING -> DownloadUiState.Installing(progress)

            Task.State.UNINSTALLING -> DownloadUiState.Uninstalling
            Task.State.COMPLETED -> DownloadUiState.Installed(
              open = ::open,
              uninstall = ::uninstall
            )

            Task.State.FAILED -> DownloadUiState.Error(
              retry = when (task.type) {
                INSTALL -> ::install
                UNINSTALL -> ::uninstall
              },
              clear = {}
            )

            Task.State.READY_TO_INSTALL -> DownloadUiState.ReadyToInstall(
              cancel = task::cancel
            )
          }
        }
        ?: flowOf(null)
    }

    combine(packageStates, taskStates) { packageState, taskState ->
      packageState to taskState.let {
        if (it is DownloadUiState.Error) {
          DownloadUiState.Error(
            retry = it.retry,
            clear = { viewModelState.update { packageState } },
          )
        } else {
          it
        }
      }
    }
      .catch { throwable -> throwable.printStackTrace() }
      .onEach { (packageState, taskState) ->
        viewModelState.update { state ->
          taskState
            ?: state.takeIf { state is DownloadUiState.Error }
            ?: packageState
        }
      }
      .launchIn(viewModelScope)

    if (automaticInstall) {
      install()
    }
  }

  private fun install() {
    viewModelScope.launch {
      try {
        viewModelState.update { DownloadUiState.Processing(null) }
        appInstaller.install(installPackageInfo)
        campaigns?.sendInstallClickEvent()
      } catch (e: Exception) {
        Timber.e(e.message)
      }
    }
  }

  private fun uninstall() {
    viewModelState.update { DownloadUiState.Processing(null) }
    appInstaller.uninstall()
  }

  private fun open() = installedAppOpener.openInstalledApp(app.packageName)
}
