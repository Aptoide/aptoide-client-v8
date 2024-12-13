package cm.aptoide.pt.download_view.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.extensions.compatVersionCode
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.install_info_mapper.domain.InstallPackageInfoMapper
import cm.aptoide.pt.install_manager.InstallManager
import cm.aptoide.pt.install_manager.Task
import cm.aptoide.pt.install_manager.Task.Type.INSTALL
import cm.aptoide.pt.install_manager.Task.Type.UNINSTALL
import cm.aptoide.pt.install_manager.dto.Constraints
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.environment.NetworkConnection
import cm.aptoide.pt.network_listener.NetworkConnectionImpl
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

// In case resolution vas cancelled the [onResolve] should not be called
typealias ConstraintsResolver = (missingSpace: Long, onResolve: (Constraints) -> Unit) -> Unit

@Suppress("OPT_IN_USAGE")
class DownloadViewModel(
  private val app: App,
  private val installManager: InstallManager,
  networkConnectionImpl: NetworkConnectionImpl,
  private val installedAppOpener: InstalledAppOpener,
  private val installPackageInfoMapper: InstallPackageInfoMapper,
) : ViewModel() {

  private val appInstaller = installManager.getApp(app.packageName)

  private val viewModelState = MutableStateFlow<DownloadUiState?>(null)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    val packageStates = appInstaller.packageInfoFlow.map { info ->
      info?.let {
        if (it.compatVersionCode < app.versionCode) {
          DownloadUiState.Outdated(
            open = ::open,
            updateWith = ::install,
            uninstall = ::uninstall
          )
        } else {
          DownloadUiState.Installed(
            open = ::open,
            uninstall = ::uninstall
          )
        }
      } ?: DownloadUiState.Install(installWith = ::install)
    }

    val taskStates = appInstaller.taskFlow.flatMapConcat { task ->
      task?.stateAndProgress
        ?.map { (state, progress) ->
          task to when (state) {
            Task.State.ABORTED,
            Task.State.CANCELED,
              -> null

            Task.State.PENDING -> DownloadUiState.Waiting(
              installPackageInfo = task.installPackageInfo,
              action = task::cancel
            )

            Task.State.DOWNLOADING -> DownloadUiState.Downloading(
              installPackageInfo = task.installPackageInfo,
              cancel = task::cancel,
              downloadProgress = progress
            )

            Task.State.INSTALLING -> DownloadUiState.Installing(
              installPackageInfo = task.installPackageInfo,
              installProgress = progress
            )

            Task.State.UNINSTALLING -> DownloadUiState.Uninstalling(
              installPackageInfo = task.installPackageInfo
            )

            Task.State.COMPLETED -> DownloadUiState.Installed(
              open = ::open,
              uninstall = ::uninstall
            )

            Task.State.OUT_OF_SPACE,
            Task.State.FAILED -> DownloadUiState.Error(
              retryWith = when (task.type) {
                INSTALL -> ::install
                UNINSTALL -> ::uninstall
              },
            )

            Task.State.READY_TO_INSTALL -> DownloadUiState.ReadyToInstall(
              installPackageInfo = task.installPackageInfo,
              cancel = task::cancel
            )
          }
        }
        ?: flowOf(null)
    }

    combine(
      packageStates,
      taskStates,
      networkConnectionImpl.states
    ) { packageState, taskState, networkState ->
      packageState to taskState?.let {
        val (task, state) = it
        when (state) {
          is DownloadUiState.Waiting -> {
            val networkConstraint = task.constraints.networkType

            val (blocker, action) = when {
              networkState == NetworkConnection.State.METERED
                && networkConstraint == Constraints.NetworkType.UNMETERED
                -> ExecutionBlocker.UNMETERED to task::allowDownloadOnMetered

              networkState == NetworkConnection.State.GONE
                && networkConstraint != Constraints.NetworkType.NOT_REQUIRED
                -> ExecutionBlocker.CONNECTION to task::cancel

              else -> ExecutionBlocker.QUEUE to task::cancel
            }

            DownloadUiState.Waiting(
              installPackageInfo = task.installPackageInfo,
              blocker = blocker,
              action = action
            )
          }

          else -> state
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
  }

  private fun install(resolver: ConstraintsResolver) {
    viewModelScope.launch {
      try {
        installPackageInfoMapper.map(app)
      } catch (_: Exception) {
        viewModelState.update {
          DownloadUiState.Error(retryWith = ::install)
        }
        null
      }?.let {
        resolver(installManager.getMissingFreeSpaceFor(it)) { constraints ->
          install(it, constraints)
        }
      }
    }
  }

  private fun install(
    installPackageInfo: InstallPackageInfo,
    constraints: Constraints,
  ) {
    try {
      appInstaller.install(
        installPackageInfo = installPackageInfo,
        constraints = constraints,
      )
    } catch (_: Exception) {
      viewModelState.update {
        DownloadUiState.Error(retryWith = ::install)
      }
    }
  }

  @Suppress("UNUSED_PARAMETER", "unused")
  private fun uninstall(resolver: ConstraintsResolver) = uninstall()

  private fun uninstall() {
    try {
      appInstaller.uninstall()
    } catch (_: Exception) {
      viewModelState.update {
        DownloadUiState.Error(retryWith = ::uninstall)
      }
    }
  }

  private fun open() = installedAppOpener.openInstalledApp(app.packageName)
}
