package cm.aptoide.pt.install_manager.dto

/**
 * This interface represents the install package info sizes.
 */
interface SizeEstimator {

  /**
   * Estimates the download size of the provided [InstallPackageInfo].
   *
   * @param ipInfo - install package info to extract data from
   * @returns how much space is required to download the installation files.
   */
  fun getDownloadSize(ipInfo: InstallPackageInfo): Long

  /**
   * Estimates the installation size of the provided [InstallPackageInfo].
   *
   * @param ipInfo - install package info to extract data from
   * @returns how much space is required to install the downloaded installation files.
   */
  fun getInstallSize(ipInfo: InstallPackageInfo): Long

  /**
   * Estimates the total download and install size of the provided [InstallPackageInfo].
   *
   * @param ipInfo - install package info to extract data from
   * @returns how much space is required to download and install the installation files.
   */
  fun getTotalInstallationSize(ipInfo: InstallPackageInfo): Long

  /**
   * Estimates the total installed size of the provided [InstallPackageInfo].
   *
   * @param ipInfo - install package info to extract data from
   * @returns how much space will be used after download and install the installation files.
   */
  fun installedSize(ipInfo: InstallPackageInfo): Long
}
