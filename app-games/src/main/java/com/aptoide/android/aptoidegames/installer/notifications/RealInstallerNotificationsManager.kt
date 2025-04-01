package com.aptoide.android.aptoidegames.installer.notifications

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import cm.aptoide.pt.install_manager.App
import cm.aptoide.pt.install_manager.InstallManager
import cm.aptoide.pt.install_manager.Task.State
import cm.aptoide.pt.install_manager.dto.Constraints.NetworkType.UNMETERED
import cm.aptoide.pt.install_manager.environment.NetworkConnection.State.GONE
import cm.aptoide.pt.install_manager.environment.NetworkConnection.State.METERED
import cm.aptoide.pt.installer.platform.UserActionHandler
import cm.aptoide.pt.installer.platform.UserActionRequest
import cm.aptoide.pt.network_listener.NetworkConnectionImpl
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.installer.AppDetailsUseCase
import com.aptoide.android.aptoidegames.installer.isInstallationIntent
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
  private val userActionHandler: UserActionHandler,
  private val imageDownloader: ImageDownloader,
) : InstallerNotificationsManager, CoroutineScope, LifecycleObserver {

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO + Job()

  var isOnForeground = false

  init {
    val lifecycle = ProcessLifecycleOwner.get().lifecycle

    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_RESUME) {
        isOnForeground = true
      } else if (event == Lifecycle.Event.ON_PAUSE) {
        isOnForeground = false
      }
    }

    lifecycle.addObserver(observer)
  }

  override suspend fun initialize() {
    installManager.workingAppInstallers.firstOrNull()?.let { onInstallationQueued(it) }
    installManager.scheduledApps.forEach { onInstallationQueued(it) }

    userActionHandler.requests.collect {
      if (isOnForeground == false
        && it is UserActionRequest.InstallationAction
        && it.intent.isInstallationIntent()
      ) {
        val packageName = it.intent
          .getStringExtra("${BuildConfig.APPLICATION_ID}.pn") ?: "NaN"
        onReadyToInstall(packageName)
      }
    }
  }

  override fun onInstallationQueued(packageName: String) {
    launch {
      onInstallationQueued(installManager.getApp(packageName))
    }
  }

  override fun onReadyToInstall(packageName: String) {
    launch {
      onReadyToInstall(installManager.getApp(packageName))
    }
  }

  private suspend fun onReadyToInstall(app: App) {
    app.task?.let {
      val appDetails = appDetailsUseCase.getAppDetails(app)
      val appIcon = imageDownloader.downloadImageFrom(appDetails?.iconUrl)

      installerNotificationsManager.showReadyToInstallNotification(
        packageName = app.packageName,
        appDetails = appDetails,
        appIcon = appIcon
      )
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  private suspend fun onInstallationQueued(app: App) {
    app.task?.let {
      val appDetails = appDetailsUseCase.getAppDetails(app)
      val appIcon = imageDownloader.downloadImageFrom(appDetails?.iconUrl)

      it.stateAndProgress.flatMapLatest { state ->
        if (state is State.Pending) {
          networkConnection.states.map { networkState ->
            val networkConstraint = it.constraints.networkType
            if (networkState == GONE || (networkState == METERED && networkConstraint == UNMETERED)) {
              installerNotificationsManager.showWaitingForWifiNotification(
                packageName = app.packageName,
                appDetails = appDetails,
                appIcon = appIcon
              )
            } else {
              installerNotificationsManager.showWaitingForDownloadNotification(
                packageName = app.packageName,
                appDetails = appDetails,
                appIcon = appIcon
              )
            }
          }
        } else {
          installerNotificationsManager.showInstallationStateNotification(
            packageName = app.packageName,
            appDetails = appDetails,
            state = state,
            size = it.installPackageInfo.filesSize,
            appIcon = appIcon
          )
          flowOf(Unit)
        }
      }.launchIn(this)
    }
  }
}
