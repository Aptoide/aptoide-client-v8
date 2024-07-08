package com.aptoide.android.aptoidegames.installer.analytics

import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.workers.PackageInstaller
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlin.coroutines.cancellation.CancellationException

class InstallProbe(
  private val packageInstaller: PackageInstaller,
  private val analytics: InstallAnalytics,
) : PackageInstaller {

  override fun install(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
  ): Flow<Int> {
    val analyticsPayload = installPackageInfo.payload.toAnalyticsPayload()
    return packageInstaller.install(packageName, installPackageInfo)
      .onStart {
        analytics.sendInstallStartedEvent(
          packageName = packageName,
          analyticsPayload = analyticsPayload
        )
      }
      .onCompletion {
        when (it) {
          is CancellationException -> analytics.sendInstallCancelEvent(
            packageName = packageName,
            analyticsPayload = analyticsPayload
          )

          null -> {
            analytics.sendInstallCompletedEvent(
              packageName = packageName,
              analyticsPayload = analyticsPayload
            )
          }

          else -> analytics.sendInstallErrorEvent(
            packageName = packageName,
            analyticsPayload = analyticsPayload,
            errorMessage = it.message,
            errorType = it::class.simpleName
          )
        }
      }
  }

  override fun uninstall(packageName: String): Flow<Int> = packageInstaller.uninstall(packageName)

  override fun cancel(packageName: String): Boolean = packageInstaller.cancel(packageName)
}
