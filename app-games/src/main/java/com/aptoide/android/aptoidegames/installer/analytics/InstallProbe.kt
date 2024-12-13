package com.aptoide.android.aptoidegames.installer.analytics

import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.workers.PackageInstaller
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

class InstallProbe(
  private val packageInstaller: PackageInstaller,
  private val analytics: InstallAnalytics,
) : PackageInstaller {

  override fun install(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
  ): Flow<Int> {
    return packageInstaller.install(packageName, installPackageInfo)
      .onStart {
        analytics.sendInstallStartedEvent(
          packageName = packageName,
          installPackageInfo = installPackageInfo
        )
      }
      .onCompletion {
        when (it) {
          is CancellationException -> analytics.sendInstallCancelEvent(
            packageName = packageName,
            installPackageInfo = installPackageInfo
          )

          null -> {
            analytics.sendInstallCompletedEvent(
              packageName = packageName,
              installPackageInfo = installPackageInfo
            )
          }

          else -> analytics.sendInstallErrorEvent(
            packageName = packageName,
            installPackageInfo = installPackageInfo,
            errorMessage = it.message,
            errorType = it::class.simpleName
          )
        }
      }
  }

  override fun uninstall(packageName: String): Flow<Int> = packageInstaller.uninstall(packageName)
}
