package cm.aptoide.pt.install_manager.dto

class SizeEstimatorImpl() : SizeEstimator {
  override fun getDownloadSize(ipInfo: InstallPackageInfo) = ipInfo.filesSize

  override fun getInstallSize(ipInfo: InstallPackageInfo) = ipInfo.filesSize

  override fun getTotalInstallationSize(ipInfo: InstallPackageInfo) =
    ipInfo.filesSize + ipInfo.filesSize

  override fun installedSize(ipInfo: InstallPackageInfo) = ipInfo.filesSize
}
