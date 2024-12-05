package cm.aptoide.pt.install_manager.repository

import android.content.pm.InstallSourceInfo
import android.content.pm.PackageInfo

/**
 * An interface abstraction for tests.
 *
 * This interface provides the system info about the package.
 */
interface AppInfoRepository {

  /**
   * Get all installed package names.
   *
   * @returns all installed apps package info
   */
  fun getAllPackageInfos(): Set<PackageInfo>

  /**
   * Get package info.
   *
   * @param packageName - a package name
   * @returns info about the [packageName] if app is installed. Should return null in case of error.
   */
  fun getPackageInfo(packageName: String): PackageInfo?

  /**
   * Get install source info.
   *
   * @param packageName - a package name
   * @returns info about the install source of [packageName] if app is installed. Should return null in case of error.
   */
  fun getInstallSourceInfo(packageName: String): InstallSourceInfo?

  /**
   * Sets a listener for changes.
   *
   * @param onChange - a callback to signal about changes
   */
  fun setOnChangeListener(onChange: (String) -> Unit)
}
