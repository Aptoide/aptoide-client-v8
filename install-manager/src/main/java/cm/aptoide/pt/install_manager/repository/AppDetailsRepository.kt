package cm.aptoide.pt.install_manager.repository

/**
 * A storage for apps details.
 *
 * This interface represents a storage that keeps additional details about apps.
 * @param D the type of the app details.
 */
interface AppDetailsRepository<D> {

  /**
   * Get all known apps details
   *
   * @returns all apps details saved in this storage
   */
  suspend fun getAll(): Set<Pair<String, D>>

  /**
   * Get app info.
   *
   * @param packageName - a package name
   * @returns app details for the [packageName] if any.
   */
  suspend fun get(packageName: String): D?

  /**
   * Save or update app info.
   *
   * @param details - an app info to save or update
   */
  suspend fun save(packageName: String, details: D)

  /**
   * Remove app info. Essentially after doing this an app will not be known anymore.
   *
   * @param packageName - a package name
   */
  suspend fun remove(packageName: String)
}

