package cm.aptoide.pt.home.more.appcoins

import cm.aptoide.pt.ads.WalletAdsOfferManager.OfferResponseStatus
import cm.aptoide.pt.app.DownloadModel
import cm.aptoide.pt.download.DownloadAnalytics
import cm.aptoide.pt.download.InstallType

class EarnAppcListAnalytics(private val downloadAnalytics: DownloadAnalytics) {

  fun sendNotEnoughSpaceErrorEvent(packageName: String?,
                                   versionCode: Int,
                                   downloadAction: DownloadModel.Action,
                                   offerResponseStatus: OfferResponseStatus?,
                                   isMigration: Boolean, isAppBundle: Boolean,
                                   hasAppc: Boolean, trustedBadge: String?,
                                   storeName: String?,
                                   isApkfy: Boolean,
                                   hasObbs: Boolean) {
    downloadAnalytics.sendNotEnoughSpaceError(
        packageName, versionCode,
        mapDownloadAction(downloadAction), offerResponseStatus, isMigration, isAppBundle, hasAppc,
        trustedBadge, storeName, isApkfy, hasObbs
    )
  }

  private fun mapDownloadAction(downloadAction: DownloadModel.Action): InstallType? {
    var installType = InstallType.INSTALL
    installType = when (downloadAction) {
      DownloadModel.Action.DOWNGRADE -> InstallType.DOWNGRADE
      DownloadModel.Action.INSTALL -> InstallType.INSTALL
      DownloadModel.Action.UPDATE -> InstallType.UPDATE
      DownloadModel.Action.MIGRATE, DownloadModel.Action.OPEN -> throw IllegalStateException(
          "Mapping an invalid download action " + downloadAction.name)
    }
    return installType
  }
}