package com.aptoide.android.aptoidegames.apkfy

import cm.aptoide.pt.install_manager.AbortException
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.workers.PackageDownloader
import cm.aptoide.pt.installer.platform.REQUEST_INSTALL_PACKAGES_NOT_ALLOWED
import cm.aptoide.pt.installer.platform.REQUEST_INSTALL_PACKAGES_RATIONALE_REJECTED
import cm.aptoide.pt.installer.platform.WRITE_EXTERNAL_STORAGE_NOT_ALLOWED
import cm.aptoide.pt.installer.platform.WRITE_EXTERNAL_STORAGE_RATIONALE_REJECTED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach

sealed interface DownloadPermissionState {
  val packageName: String

  data class Allowed(override val packageName: String) : DownloadPermissionState
  data class RationaleRejected(override val packageName: String) : DownloadPermissionState
  data class SystemNotAllowed(override val packageName: String) : DownloadPermissionState
}

class DownloadPermissionStateProbe(
  private val packageDownloader: PackageDownloader,
) : PackageDownloader {

  private val _permissionsResult = MutableStateFlow<DownloadPermissionState?>(null)

  val permissionsResult: Flow<DownloadPermissionState?> = _permissionsResult

  override fun download(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
  ): Flow<Int> = packageDownloader
    .download(packageName, installPackageInfo)
    .onEach {
      if (it >= 0) _permissionsResult.emit(DownloadPermissionState.Allowed(packageName))
    }
    .onCompletion {
      when ((it as? AbortException)?.message) {
        REQUEST_INSTALL_PACKAGES_NOT_ALLOWED,
        WRITE_EXTERNAL_STORAGE_NOT_ALLOWED ->
          _permissionsResult.emit(DownloadPermissionState.SystemNotAllowed(packageName))

        REQUEST_INSTALL_PACKAGES_RATIONALE_REJECTED,
        WRITE_EXTERNAL_STORAGE_RATIONALE_REJECTED ->
          _permissionsResult.emit(DownloadPermissionState.RationaleRejected(packageName))
      }
    }
}
