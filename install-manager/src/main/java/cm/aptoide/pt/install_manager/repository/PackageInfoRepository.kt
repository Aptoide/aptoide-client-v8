package cm.aptoide.pt.install_manager.repository

import android.content.pm.PackageInfo

/**
 * An interface abstraction for tests.
 *
 * This interface provides the system info about the package.
 */
interface PackageInfoRepository {

  /**
   * Get all installed package names.
   *
   * @returns all installed apps package names
   */
  suspend fun getAll(): Set<PackageInfo>

  /**
   * Get package info.
   *
   * @param packageName - a package name
   * @returns info about the [packageName] if any.
   */
  suspend fun get(packageName: String): PackageInfo?
}

