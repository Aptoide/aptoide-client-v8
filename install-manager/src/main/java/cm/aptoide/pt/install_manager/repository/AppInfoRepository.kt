package cm.aptoide.pt.install_manager.repository

import cm.aptoide.pt.install_manager.dto.AppInfo

/**
 * A storage for apps info.
 *
 * This interface represents a storage that keeps info about known apps.
 * @param D the type of the app info details.
 */
interface AppInfoRepository<D> {

  /**
   * Get all known apps info
   *
   * @returns all apps info saved in this storage
   */
  suspend fun getAll(): Set<AppInfo<D>>

  /**
   * Get app info.
   *
   * @param packageName - a package name
   * @returns app info for the [packageName] if any.
   */
  suspend fun get(packageName: String): AppInfo<D>?

  /**
   * Save or update app info.
   *
   * @param appInfo - an app info to save or update
   */
  suspend fun save(appInfo: AppInfo<D>)

  /**
   * Remove app info. Essentially after doing this an app will not be known anymore.
   *
   * @param packageName - a package name
   */
  suspend fun remove(packageName: String)
}

