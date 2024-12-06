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
  private var cachedSizes = mutableMapOf<String, Long>()

  override fun install(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
  ): Flow<Int> {
    cachedSizes[packageName] = installPackageInfo.filesSize
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
            analyticsPayload = analyticsPayload,
            appSizeSegment = calcAppSizeSegment(bytes = installPackageInfo.filesSize)
          )

          null -> {
            analytics.sendInstallCompletedEvent(
              packageName = packageName,
              analyticsPayload = analyticsPayload,
              appSizeSegment = calcAppSizeSegment(bytes = installPackageInfo.filesSize)
            )
          }

          else -> analytics.sendInstallErrorEvent(
            packageName = packageName,
            analyticsPayload = analyticsPayload,
            appSizeSegment = calcAppSizeSegment(bytes = installPackageInfo.filesSize),
            errorMessage = it.message,
            errorType = it::class.simpleName
          )
        }
      }
  }

  override fun uninstall(packageName: String): Flow<Int> = packageInstaller.uninstall(packageName)

  override fun cancel(packageName: String): Boolean = packageInstaller.cancel(packageName)
}
