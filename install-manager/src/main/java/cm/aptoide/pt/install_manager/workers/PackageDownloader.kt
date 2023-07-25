package cm.aptoide.pt.install_manager.workers

import cm.aptoide.pt.install_manager.AbortException
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
   * Flow throws anything except [CancellationException], signalling about download failure
   * Or [AbortException] if download was aborted with the reason in message
   */
  suspend fun download(packageName: String, installPackageInfo: InstallPackageInfo): Flow<Int>

  /**
   * Cancel package files download if active.
   * Downloaded files caching is responsibility of this downloader.
   * The [download] Flow for the [packageName] must throw [CancellationException] on this call.
   *
   * @param packageName - a package name
   * @returns true there was something to cancel and it was cancelled.
   */
  fun cancel(packageName: String): Boolean
}
