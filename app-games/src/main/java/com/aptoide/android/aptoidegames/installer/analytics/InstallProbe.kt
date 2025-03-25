package com.aptoide.android.aptoidegames.installer.analytics

import cm.aptoide.pt.install_manager.AbortException
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.workers.PackageInstaller
import cm.aptoide.pt.installer.platform.REQUEST_INSTALL_PACKAGES_NOT_ALLOWED
import cm.aptoide.pt.installer.platform.REQUEST_INSTALL_PACKAGES_RATIONALE_REJECTED
import cm.aptoide.pt.installer.platform.WRITE_EXTERNAL_STORAGE_NOT_ALLOWED
import cm.aptoide.pt.installer.platform.WRITE_EXTERNAL_STORAGE_RATIONALE_REJECTED
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

          is AbortException -> {
            when (it.message) {
              REQUEST_INSTALL_PACKAGES_NOT_ALLOWED,
              REQUEST_INSTALL_PACKAGES_RATIONALE_REJECTED,
              WRITE_EXTERNAL_STORAGE_NOT_ALLOWED,
              WRITE_EXTERNAL_STORAGE_RATIONALE_REJECTED -> analytics.sendInstallAbortEvent(
                packageName = packageName,
                installPackageInfo = installPackageInfo,
                errorMessage = it.message
              )

              else -> analytics.sendInstallErrorEvent(
                packageName = packageName,
                installPackageInfo = installPackageInfo,
                errorMessage = it.message,
                errorType = it::class.simpleName
              )
            }
          }

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
