package com.aptoide.android.aptoidegames.installer.analytics

import cm.aptoide.pt.install_manager.AbortException
import cm.aptoide.pt.install_manager.DownloadInfo
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.workers.PackageDownloader
import cm.aptoide.pt.installer.DownloadException
import cm.aptoide.pt.installer.platform.REQUEST_INSTALL_PACKAGES_NOT_ALLOWED
import cm.aptoide.pt.installer.platform.REQUEST_INSTALL_PACKAGES_RATIONALE_REJECTED
import cm.aptoide.pt.installer.platform.WRITE_EXTERNAL_STORAGE_NOT_ALLOWED
import cm.aptoide.pt.installer.platform.WRITE_EXTERNAL_STORAGE_RATIONALE_REJECTED
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import retrofit2.HttpException

class DownloadProbe(
  private val packageDownloader: PackageDownloader,
  private val analytics: InstallAnalytics,
) : PackageDownloader {

  override fun download(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
  ): Flow<DownloadInfo> {
    var initialTimestamp = 0L
    var totalDownloadedBytes = 0L
    return packageDownloader.download(packageName, installPackageInfo)
      .onStart {
        analytics.sendDownloadStartedEvent(
          packageName = packageName,
          installPackageInfo = installPackageInfo
        )
      }
      .onEach {
        if (initialTimestamp == 0L && it.progress >= 0) {
          initialTimestamp = System.currentTimeMillis()
        }
        totalDownloadedBytes = it.downloadedBytes
      }
      .onCompletion {
        val totalTime = (System.currentTimeMillis() - initialTimestamp) / 1000.0
        val downloadSpeed = totalDownloadedBytes / totalTime

        when (it) {
          is CancellationException -> analytics.sendDownloadCanceledEvent(
            packageName = packageName,
            installPackageInfo = installPackageInfo,
            downloadedBytesPerSecond = downloadSpeed
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
                downloadedBytesPerSecond = downloadSpeed,
                errorMessage = it.message,
                errorType = it::class.simpleName,
                errorCode = null,
                errorUrl = null,
              )
            }
          }

          is DownloadException -> analytics.sendDownloadErrorEvent(
            packageName = packageName,
            installPackageInfo = installPackageInfo,
            downloadedBytesPerSecond = downloadSpeed,
            errorMessage = it.cause.message,
            errorType = it.cause::class.simpleName,
            errorCode = (it.cause as? HttpException)?.code(),
            errorUrl = it.url,
          )

          null -> analytics.sendDownloadCompletedEvent(
            packageName = packageName,
            installPackageInfo = installPackageInfo,
            downloadedBytesPerSecond = downloadSpeed
          )

          else -> analytics.sendDownloadErrorEvent(
            packageName = packageName,
            installPackageInfo = installPackageInfo,
            downloadedBytesPerSecond = downloadSpeed,
            errorMessage = it.message,
            errorType = it::class.simpleName,
            errorCode = (it as? HttpException)?.code(),
            errorUrl = null
          )
        }
      }
  }
}
