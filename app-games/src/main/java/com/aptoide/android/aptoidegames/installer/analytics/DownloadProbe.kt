package com.aptoide.android.aptoidegames.installer.analytics

import cm.aptoide.pt.install_manager.AbortException
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.workers.PackageDownloader
import cm.aptoide.pt.installer.platform.REQUEST_INSTALL_PACKAGES_NOT_ALLOWED
import cm.aptoide.pt.installer.platform.REQUEST_INSTALL_PACKAGES_RATIONALE_REJECTED
import cm.aptoide.pt.installer.platform.WRITE_EXTERNAL_STORAGE_NOT_ALLOWED
import cm.aptoide.pt.installer.platform.WRITE_EXTERNAL_STORAGE_RATIONALE_REJECTED
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import retrofit2.HttpException

class DownloadProbe(
  private val packageDownloader: PackageDownloader,
  private val analytics: InstallAnalytics,
) : PackageDownloader {

  override fun download(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
  ): Flow<Int> {
    return packageDownloader.download(packageName, installPackageInfo)
      .onStart {
        analytics.sendDownloadStartedEvent(
          packageName = packageName,
          installPackageInfo = installPackageInfo
        )
      }
      .onCompletion {
        when (it) {
          is CancellationException -> analytics.sendDownloadCanceledEvent(
            packageName = packageName,
            installPackageInfo = installPackageInfo
          )

          is AbortException -> {
            when (it.message) {
              REQUEST_INSTALL_PACKAGES_NOT_ALLOWED,
              REQUEST_INSTALL_PACKAGES_RATIONALE_REJECTED,
              WRITE_EXTERNAL_STORAGE_NOT_ALLOWED,
              WRITE_EXTERNAL_STORAGE_RATIONALE_REJECTED -> analytics.sendDownloadAbortEvent(
                packageName = packageName,
                installPackageInfo = installPackageInfo,
                errorMessage = it.message
              )

              else -> analytics.sendDownloadErrorEvent(
                packageName = packageName,
                installPackageInfo = installPackageInfo,
                errorMessage = it.message,
                errorType = it::class.simpleName,
                errorCode = null
              )
            }
          }

          null -> analytics.sendDownloadCompletedEvent(
            packageName = packageName,
            installPackageInfo = installPackageInfo
          )

          else -> analytics.sendDownloadErrorEvent(
            packageName = packageName,
            installPackageInfo = installPackageInfo,
            errorMessage = it.message,
            errorType = it::class.simpleName,
            errorCode = (it as? HttpException)?.code()
          )
        }
      }
  }
}
