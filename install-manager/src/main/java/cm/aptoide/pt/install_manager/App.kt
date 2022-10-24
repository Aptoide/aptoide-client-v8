package cm.aptoide.pt.install_manager

import cm.aptoide.pt.install_manager.dto.InstallPackageInfo


/**
 * An app in context of install and uninstall.
 *
 * This class represents an actual app that can be installed/uninstalled/removed.
 *
 * @param D the type of the app details.
 * @property packageName - an app package name.
 * @property installedVersionName - an app version nome installed. Null if not installed currently.
 * @property installedVersionCode - an app version code installed. Null if not installed currently.
 * @property details - an app details. May be null.
 */
interface App<D> {
  val packageName: String
  val installedVersionName: String?
  val installedVersionCode: Int?
  val details: D?

  /**
   * Get the current ongoing task if any
   */
  suspend fun getTask(): Task?

  /**
   * Set and save the current app info details.
   * Will be saved to the repo only for a known opp.
   */
  suspend fun setDetails(details: D)

  /**
   * Creates an installation task.
   * On installation success [packageName], [installedVersionName], [installedVersionCode] and [details]
   * will be saved to the repo and app wil become known if it wasn't.
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

  /**
   * Remove an app from a list of known apps and cancel any related task.
   */
  suspend fun remove()
}