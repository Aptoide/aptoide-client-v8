package cm.aptoide.pt.download_view.presentation

import androidx.core.content.pm.PackageInfoCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.download_view.domain.model.PayloadMapper
import cm.aptoide.pt.download_view.domain.model.getInstallPackageInfo
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.install_manager.InstallManager
import cm.aptoide.pt.install_manager.OutOfSpaceException
import cm.aptoide.pt.install_manager.Task
import cm.aptoide.pt.install_manager.Task.Type.INSTALL
import cm.aptoide.pt.install_manager.Task.Type.UNINSTALL
import cm.aptoide.pt.install_manager.dto.Constraints
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Suppress("OPT_IN_USAGE")
class DownloadViewModel(
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
      install(
        previousState = viewModelState.value,
        constraints = Constraints(
          checkForFreeSpace = false,
          networkType = Constraints.NetworkType.ANY,
        )
      )
    }
  }

  private fun install() {
    val previousState = viewModelState.value
    tryExecute(previousState) {
      appInstaller.canInstall(installPackageInfo = installPackageInfo)
        ?.let { throw it }
      viewModelState.update {
        DownloadUiState.WifiPrompt { decision ->
          decision?.let {
            install(
              previousState = previousState,
              constraints = Constraints(
                checkForFreeSpace = true,
                networkType = if (it) {
                  Constraints.NetworkType.ANY
                } else {
                  Constraints.NetworkType.UNMETERED
                }
              )
            )
          }
            ?: viewModelState.update { previousState }
        }
      }
    }
  }

  private fun install(
    previousState: DownloadUiState,
    constraints: Constraints = Constraints(
      checkForFreeSpace = true,
      networkType = Constraints.NetworkType.ANY
    ),
  ) {
    tryExecute(previousState) {
      viewModelState.update { DownloadUiState.Processing(null) }
      appInstaller.install(
        installPackageInfo = installPackageInfo,
        constraints = constraints,
      )
      viewModelScope.launch { campaigns?.sendInstallClickEvent() }
    }
  }

  private fun tryExecute(previousState: DownloadUiState, block: () -> Unit) {
    try {
      block()
    } catch (e: OutOfSpaceException) {
      viewModelState.update {
        DownloadUiState.OutOfSpaceError(
          clear = { viewModelState.update { previousState } }
        )
      }
    } catch (e: Exception) {
      viewModelState.update {
        DownloadUiState.Error(
          retry = ::install,
          clear = { viewModelState.update { previousState } },
        )
      }
    }
  }

  private fun uninstall() {
    val previousState = viewModelState.value
    viewModelState.update { DownloadUiState.Processing(null) }
    try {
      appInstaller.uninstall()
    } catch (e: Exception) {
      viewModelState.update {
        DownloadUiState.Error(
          retry = ::uninstall,
          clear = { viewModelState.update { previousState } },
        )
      }
    }
  }

  private fun open() = installedAppOpener.openInstalledApp(app.packageName)
}
