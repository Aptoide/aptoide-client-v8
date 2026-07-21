package cm.aptoide.pt.download_view.presentation

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.extensions.compatVersionCode
import cm.aptoide.pt.extensions.getSignature
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.isInCatappult
import cm.aptoide.pt.install_info_mapper.domain.InstallPackageInfoMapper
import cm.aptoide.pt.install_manager.InstallManager
import cm.aptoide.pt.install_manager.Task
import cm.aptoide.pt.install_manager.Task.Type.INSTALL
import cm.aptoide.pt.install_manager.Task.Type.UNINSTALL
import cm.aptoide.pt.install_manager.dto.Constraints
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.environment.NetworkConnection
import cm.aptoide.pt.network_listener.NetworkConnectionImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

// In case resolution vas cancelled the [onResolve] should not be called
typealias ConstraintsResolver = (missingSpace: Long, onResolve: (Constraints) -> Unit) -> Unit

data class InlineInstallLaunch(
  val intent: Intent,
  val isUpdate: Boolean,
)

@Suppress("OPT_IN_USAGE")
class DownloadViewModel(
  private val app: App,
  private val installManager: InstallManager,
  networkConnectionImpl: NetworkConnectionImpl,
  private val installedAppOpener: InstalledAppOpener,
  private val installPackageInfoMapper: InstallPackageInfoMapper,
  private val inlineInstallResolver: InlineInstallResolver? = null,
) : ViewModel() {

  private val appInstaller = installManager.getApp(app.packageName)

  private val viewModelState = MutableStateFlow<DownloadUiState?>(null)

  private val inlineInstallIntents = MutableSharedFlow<InlineInstallLaunch>(extraBufferCapacity = 1)

  private val inlineInstallOngoing = MutableStateFlow(
    inlineInstallResolver?.isInlineInstallOngoing(app.packageName) == true
  )

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  val inlineInstallEvents: Flow<InlineInstallLaunch> = inlineInstallIntents

  init {
    val packageStates = appInstaller.packageInfoFlow.map { info ->
      info?.let {
        if (it.compatVersionCode <= app.versionCode
          && app.isAppCoins
          && !app.signature.isNullOrEmpty()
          && app.signature?.lowercase() != it.getSignature()
          && app.isInCatappult() == true
        ) {
          DownloadUiState.Migrate(
            open = ::open,
            uninstall = ::uninstall,
            migrateWith = ::migrate
          )
        } else if (it.compatVersionCode < app.versionCode) {
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
      }
        ?: app.uninstallAlias
          ?.let(installManager::getApp)
          ?.packageInfo
          ?.let {
            if (it.compatVersionCode <= app.versionCode) {
              DownloadUiState.MigrateAlias(
                migrateAliasWith = ::migrateAlias
              )
            } else {
              null
            }
          }
        ?: DownloadUiState.Install(installWith = ::install)
    }

    val taskStates = appInstaller.taskFlow.flatMapConcat { task ->
      task?.stateAndProgress
        ?.map { state ->
          task to when (state) {
            Task.State.Aborted,
            Task.State.Canceled,
              -> null

            is Task.State.Pending -> DownloadUiState.Waiting(
              installPackageInfo = task.installPackageInfo,
              action = task::cancel
            )

            is Task.State.Downloading -> DownloadUiState.Downloading(
              installPackageInfo = task.installPackageInfo,
              cancel = task::cancel,
              downloadProgress = state.progress
            )

            is Task.State.Installing -> DownloadUiState.Installing(
              installPackageInfo = task.installPackageInfo,
              installProgress = state.progress
            )

            is Task.State.Uninstalling -> DownloadUiState.Uninstalling(
              installPackageInfo = task.installPackageInfo
            )

            Task.State.Completed -> DownloadUiState.Installed(
              open = ::open,
              uninstall = ::uninstall
            )

            Task.State.OutOfSpace,
            Task.State.Failed -> DownloadUiState.Error(
              retryWith = when (task.type) {
                INSTALL -> ::install
                UNINSTALL -> ::uninstall
              },
            )

            Task.State.ReadyToInstall -> DownloadUiState.ReadyToInstall(
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
      networkConnectionImpl.states,
      inlineInstallOngoing
    ) { packageState, taskState, networkState, inlineInstall ->
      Triple(
        packageState,
        taskState?.let {
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
        },
        inlineInstall
      )
    }
      .catch { throwable -> throwable.printStackTrace() }
      .onEach { (packageState, taskState, inlineInstall) ->
        val inlineInstallActive = inlineInstall && packageState !is DownloadUiState.Installed
        if (inlineInstall && !inlineInstallActive) {
          inlineInstallOngoing.value = false
        }
        viewModelState.update { state ->
          taskState
            ?: DownloadUiState.Installing(
              installPackageInfo = InstallPackageInfo(app.versionCode.toLong()),
              installProgress = -1
            ).takeIf { inlineInstallActive }
            ?: state.takeIf { state is DownloadUiState.Error }
            ?: packageState
        }
      }
      .launchIn(viewModelScope)
  }

  private fun install(resolver: ConstraintsResolver) {
    viewModelScope.launch {
      if (divertToInlineInstall()) return@launch
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

  /**
   * Emits the external install intent (e.g. Google Play inline install) instead of
   * running the regular install path. Any resolution failure falls back to the
   * regular install path.
   */
  private suspend fun divertToInlineInstall(): Boolean {
    val resolver = inlineInstallResolver ?: return false
    Timber.tag(INLINE_INSTALL_TAG).d("Resolving install diversion for ${app.packageName}")
    val intent = runCatching { resolver.resolveInlineInstall(app) }
      .onFailure {
        Timber.tag(INLINE_INSTALL_TAG).w(it, "Resolution failed for ${app.packageName}")
      }
      .getOrNull()
      ?: run {
        Timber.tag(INLINE_INSTALL_TAG)
          .d("Not diverting ${app.packageName} -> regular install path")
        return false
      }
    Timber.tag(INLINE_INSTALL_TAG).d("Diverting ${app.packageName} to inline install")
    val isUpdate = viewModelState.value is DownloadUiState.Outdated
    inlineInstallOngoing.value = true
    resolver.onInlineInstallStarted(app)
    inlineInstallIntents.emit(InlineInstallLaunch(intent = intent, isUpdate = isUpdate))
    return true
  }

  /**
   * The external install UI closing is the only cancellation signal available, so unless
   * the app got installed meanwhile the ongoing external install is considered canceled.
   */
  fun onInlineInstallClosed() {
    viewModelScope.launch {
      if (!inlineInstallOngoing.value) return@launch
      val installed = appInstaller.packageInfoFlow.first()
        ?.let { it.compatVersionCode >= app.versionCode } == true
      Timber.tag(INLINE_INSTALL_TAG)
        .d("Inline install UI closed for ${app.packageName}, installed=$installed")
      if (!installed) {
        inlineInstallOngoing.value = false
        inlineInstallResolver?.onInlineInstallCanceled(app)
      }
    }
  }

  companion object {
    internal const val INLINE_INSTALL_TAG = "InlineInstall"
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

  private fun migrate(resolver: ConstraintsResolver) {
    viewModelScope.launch {
      try {
        appInstaller.uninstall().stateAndProgress
          .last()
          .let {
            if (it is Task.State.Completed) {
              install(resolver)
            }
          }
      } catch (_: Exception) {
        viewModelState.update {
          DownloadUiState.Error(retryWith = ::migrate)
        }
      }
    }
  }

  private fun migrateAlias(resolver: ConstraintsResolver) {
    viewModelScope.launch {
      try {
        install(resolver)
        appInstaller.taskFlow.filterNotNull().first().stateAndProgress
          .last()
          .let {
            if (it is Task.State.Completed) {
              app.uninstallAlias?.let {
                installManager.getApp(it).uninstall()
              }
            }
          }
      } catch (_: Exception) {
        viewModelState.update {
          DownloadUiState.Error(retryWith = ::migrateAlias)
        }
      }
    }
  }

  private fun open() = installedAppOpener.openInstalledApp(app.packageName)
}
