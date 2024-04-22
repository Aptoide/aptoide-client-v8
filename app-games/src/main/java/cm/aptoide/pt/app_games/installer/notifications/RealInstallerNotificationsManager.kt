package cm.aptoide.pt.app_games.installer.notifications

import cm.aptoide.pt.app_games.installer.AppDetailsUseCase
import cm.aptoide.pt.install_manager.App
import cm.aptoide.pt.install_manager.InstallManager
import cm.aptoide.pt.install_manager.Task.State
import cm.aptoide.pt.install_manager.dto.Constraints.NetworkType.UNMETERED
import cm.aptoide.pt.install_manager.environment.NetworkConnection.State.GONE
import cm.aptoide.pt.install_manager.environment.NetworkConnection.State.METERED
import cm.aptoide.pt.network_listener.NetworkConnectionImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class RealInstallerNotificationsManager @Inject constructor(
  private val installManager: InstallManager,
  private val installerNotificationsManager: InstallerNotificationsBuilder,
  private val appDetailsUseCase: AppDetailsUseCase,
  private val networkConnection: NetworkConnectionImpl,
) : InstallerNotificationsManager, CoroutineScope {

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO + Job()

  override suspend fun initialize() {
    installManager.workingAppInstallers.firstOrNull()?.let { onInstallationQueued(it) }
    installManager.scheduledApps.forEach { onInstallationQueued(it) }
  }

  override fun onInstallationQueued(packageName: String) {
    launch {
      onInstallationQueued(installManager.getApp(packageName))
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  private suspend fun onInstallationQueued(app: App) {
    app.task?.let {
      it.stateAndProgress.flatMapLatest { (state, progress) ->
        val appDetails = appDetailsUseCase.getAppDetails(app)

        if (state == State.PENDING) {
          networkConnection.states.map { networkState ->
            val networkConstraint = it.constraints.networkType
            if (networkState == GONE || (networkState == METERED && networkConstraint == UNMETERED)) {
              installerNotificationsManager.showWaitingForWifiNotification(
                packageName = app.packageName,
                appDetails = appDetails,
              )
            } else {
              installerNotificationsManager.showWaitingForDownloadNotification(
                packageName = app.packageName,
                appDetails = appDetails,
              )
            }
          }
        } else {
          installerNotificationsManager.showInstallationStateNotification(
            packageName = app.packageName,
            appDetails = appDetails,
            state = state,
            progress = progress,
            size = it.installPackageInfo.downloadSize,
          )
          flowOf(Unit)
        }
      }.launchIn(this)
    }
  }
}
