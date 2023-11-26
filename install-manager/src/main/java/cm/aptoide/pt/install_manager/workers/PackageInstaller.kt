package cm.aptoide.pt.install_manager.workers

import cm.aptoide.pt.install_manager.AbortException
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow

/**
 * This interface represents install logic for the package files.
 */
interface PackageInstaller {

  /**
   * Install package files.
   * Returned Flow completes normally when everything is installed successfully.
   *
   * @param packageName - a package name
   * @param installPackageInfo - a package info
   * @returns Flow of progress values between 0 and 100.
   * Flow throws anything except [CancellationException], signalling about installation failure
   * Or [AbortException] if installation was aborted with the reason in message
   */
  fun install(packageName: String, installPackageInfo: InstallPackageInfo): Flow<Int>

  /**
   * Uninstall package files.
   * Returned Flow completes normally when everything is uninstalled successfully.
   *
   * @param packageName - a package name
   * @returns Flow of progress values between 0 and 100.
   * Flow throws anything except [CancellationException], signalling about uninstallation failure
   * Or [AbortException] if uninstallation was aborted with the reason in message
   */
  fun uninstall(packageName: String): Flow<Int>

  /**
   * Cancel package files installation/uninstallation if active.
   * The [install]/[uninstall] Flow for the [packageName] must throw [CancellationException] on this call.
   *
   * @param packageName - a package name
   * @returns true there was something to cancel and it was cancelled.
   */
  fun cancel(packageName: String): Boolean
}
