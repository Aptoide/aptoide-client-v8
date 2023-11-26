package cm.aptoide.pt.install_manager

import android.content.pm.PackageInfo
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import kotlinx.coroutines.flow.Flow

/**
 * An app in context of install and uninstall.
 *
 * This class represents an actual app that can be installed/uninstalled/removed.
 *
 * @property packageName - an app package name.
 * @property packageInfo - current app package info or null if not installed currently.
 * @property packageInfoFlow - a flow that contains app package info or null if not installed currently.
 * @property task - current ongoing task if any or null.
 * @property taskFlow - a flow that contains ongoing task if any or null.
 */
interface App {
  val packageName: String
  val packageInfo: PackageInfo?
  val packageInfoFlow: Flow<PackageInfo?>
  val task: Task?
  val taskFlow: Flow<Task?>

  /**
   * Checks if can install.
   *
   * @param installPackageInfo - a package info to use for the installation
   * @return null if can
   * @return [IllegalStateException] if another task is already running
   * @return [IllegalArgumentException] if same or newer version is already known to be installed
   */
  fun canInstall(installPackageInfo: InstallPackageInfo): Throwable?

  /**
   * Checks if can uninstall.
   *
   * @return null if can
   * @return [IllegalStateException] if another task is already created
   * @return [IllegalStateException] if app is not installed
   */
  fun canUninstall(): Throwable?

  /**
   * Creates an installation task.
   *
   * @param installPackageInfo - a package info to use for the installation
   * @return the installation task to enqueue, watch or cancel
   * @throws IllegalStateException if another task is already running
   * @throws IllegalArgumentException if same or newer version is already known to be installed
   */
  fun install(installPackageInfo: InstallPackageInfo): Task

  /**
   * Creates an uninstallation task.
   *
   * @return the uninstallation task to enqueue, watch or cancel
   * @throws IllegalStateException if another task is already created
   * @throws IllegalStateException if app is not installed
   */
  fun uninstall(): Task
}
