package com.aptoide.android.aptoidegames.installer.analytics

import cm.aptoide.pt.install_manager.AbortException
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.dto.InstallationFile
import cm.aptoide.pt.install_manager.workers.PackageDownloader
import cm.aptoide.pt.installer.platform.REQUEST_INSTALL_PACKAGES_NOT_ALLOWED
import cm.aptoide.pt.installer.platform.WRITE_EXTERNAL_STORAGE_NOT_ALLOWED
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import retrofit2.HttpException

class DownloadProbe(
  private val packageDownloader: PackageDownloader,
  private val analytics: InstallAnalytics,
) : PackageDownloader {
  private var cachedSizes = mutableMapOf<String, Long>()

  override fun download(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
  ): Flow<Int> {
    cachedSizes[packageName] = installPackageInfo.filesSize
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
              WRITE_EXTERNAL_STORAGE_NOT_ALLOWED -> analytics.sendDownloadAbortEvent(
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

  override fun cancel(packageName: String): Boolean {
    val cancelled = packageDownloader.cancel(packageName)

    //if it was not cancelled by the Package Downloader, it means that it is on queue,
    // so the analytics won't be called after completion on the download method.
    // We need to call it here
    if (!cancelled) analytics.sendDownloadCanceledInQueueEvent(
      packageName = packageName,
      installPackageInfo = InstallPackageInfo(
        versionCode = 0,
        installationFiles = setOf(
          InstallationFile(
            name = "",
            type = InstallationFile.Type.BASE,
            md5 = "",
            fileSize = cachedSizes[packageName] ?: 0,
            url = "",
            altUrl = "",
            localPath = ""
          )
        )
      ),
    )

    return cancelled
  }
}
