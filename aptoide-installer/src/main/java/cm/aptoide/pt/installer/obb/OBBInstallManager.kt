package cm.aptoide.pt.installer.obb

import android.app.ActivityManager
import android.app.Application.ACTIVITY_SERVICE
import android.content.Context
import android.os.Process
import cm.aptoide.pt.install_manager.App
import cm.aptoide.pt.install_manager.InstallManager
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import kotlinx.coroutines.flow.Flow

class OBBInstallManager(
  private val context: Context,
  private val installManager: InstallManager
) : InstallManager {
  override fun getApp(packageName: String) = installManager.getApp(packageName)

  override val installedApps: Set<App>
    get() = installManager.installedApps
  override val workingAppInstallers: Flow<App?>
    get() = installManager.workingAppInstallers
  override val scheduledApps: List<App>
    get() = installManager.scheduledApps
  override val appsChanges: Flow<App>
    get() = installManager.appsChanges

  override fun getMissingFreeSpaceFor(installPackageInfo: InstallPackageInfo) =
    installManager.getMissingFreeSpaceFor(installPackageInfo)

  override suspend fun restore() {
    if (getProcessName(
        context,
        Process.myPid()
      ) != "${context.packageName}:obbMoverProcess"
    ) {
      installManager.restore()
    }
  }
}

fun getProcessName(cxt: Context, pid: Int): String? {
  val am = cxt.getSystemService(ACTIVITY_SERVICE) as ActivityManager
  val runningApps = am.runningAppProcesses ?: return null
  for (procInfo in runningApps) {
    if (procInfo.pid == pid) {
      return procInfo.processName
    }
  }
  return null
}
