package com.aptoide.android.aptoidegames.installer

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.install_manager.InstallManager
import com.aptoide.android.aptoidegames.installer.analytics.InstallAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Singleton

interface InstallerQueueHandler {
  //Used to clear the remaining queue after a package download in the queue is cancelled directly by the user.
  fun clearRemainingQueue(canceledPackageName: String) {}
}

@Singleton
class InstallerQueueHandlerImpl @Inject constructor(
  private val installManager: InstallManager,
  private val installAnalytics: InstallAnalytics
) : InstallerQueueHandler {

  override fun clearRemainingQueue(canceledPackageName: String) {
    installManager.scheduledApps.forEach { app ->
      app.takeIf { it.packageName != canceledPackageName }
        ?.task
        ?.run {
          cancel()
          installAnalytics.sendAutomaticQueueDownloadCancelEvent(packageName, installPackageInfo)
        }
    }
  }
}

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val installerQueueHandler: InstallerQueueHandlerImpl,
) : ViewModel()

@Composable
fun rememberInstallerQueueHandler(): InstallerQueueHandler = runPreviewable(
  preview = { object : InstallerQueueHandler {} },
  real = {
    val injectionsProvider = hiltViewModel<InjectionsProvider>()
    injectionsProvider.installerQueueHandler
  }
)
