package cm.aptoide.pt.install_manager

import android.content.pm.PackageInfo
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo


/**
 * An app in context of install and uninstall.
 *
 * This class represents an actual app that can be installed/uninstalled/removed.
 *
 * @property packageName - an app package name.
 * @property packageInfo - an app package info installed. Null if not installed currently.
 */
interface App {
  val packageName: String
  val packageInfo: PackageInfo?

  /**
   * Get the current ongoing task if any
   */
  suspend fun getTask(): Task?

  /**
   * Creates an installation task.
   *
   * @param installPackageInfo - a package info to use for the installation
   * @return the installation task to enqueue, watch or cancel
   * @throws IllegalStateException if another task is already running
   * @throws IllegalArgumentException if same or newer version is already known to be installed
   */
  suspend fun install(installPackageInfo: InstallPackageInfo): Task

  /**
   * Creates an uninstallation task.
   *
   * @return the uninstallation task to enqueue, watch or cancel
   * @throws IllegalStateException if another task is already created
   * @throws IllegalStateException if app is not known as installed. Means that this can uninstall
   * only app was installed by this.
   */
  suspend fun uninstall(): Task
}