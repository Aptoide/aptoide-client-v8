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
   * @returns all installed apps package info
   */
  fun getAll(): Set<PackageInfo>

  /**
   * Get package info.
   *
   * @param packageName - a package name
   * @returns info about the [packageName] if app is installed. Should return null in case of error.
   */
  fun get(packageName: String): PackageInfo?

  /**
   * Sets a listener for changes.
   *
   * @param onChange - a callback to signal about changes
   */
  fun setOnChangeListener(onChange: (String) -> Unit)
}

