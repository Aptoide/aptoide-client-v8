package cm.aptoide.pt.install_manager.workers

import cm.aptoide.pt.install_manager.AbortException
import cm.aptoide.pt.install_manager.OutOfSpaceException
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow

/**
 * This interface represents download logic for the package files.
 * Files clean up should be controlled by implementation, it is not this module responsibility.
 */
interface PackageDownloader {

  /**
   * Download all package files.
   * Downloaded files caching is responsibility of this downloader.
   * Returned Flow completes normally when everything is downloaded successfully.
   *
   * @param packageName - a package name
   * @param installPackageInfo - a package info
   * @returns Flow of progress values between 0 and 100.
   * Flow throws [CancellationException] if download was canceled by user
   * Or [AbortException] if download was aborted with the reason in message
   * Or [OutOfSpaceException] if there is not enough space to download
   * Or anything else, signalling about download failure
   */
  fun download(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
  ): Flow<Int>
}
