package cm.aptoide.pt.wallet

import cm.aptoide.analytics.AnalyticsManager
import cm.aptoide.pt.ads.WalletAdsOfferManager
import cm.aptoide.pt.app.DownloadModel
import cm.aptoide.pt.database.realm.Download
import cm.aptoide.pt.download.DownloadAnalytics
import cm.aptoide.pt.download.InstallType

class WalletInstallAnalytics(val downloadAnalytics: DownloadAnalytics) {

  fun setupDownloadEvents(download: Download, campaignId: Int, abTestGroup: String,
                          downloadAction: DownloadModel.Action?, action: AnalyticsManager.Action,
                          trustedValue: String?,
                          editorsChoice: String?,
                          offerResponseStatus: WalletAdsOfferManager.OfferResponseStatus) {
    downloadAnalytics.downloadStartEvent(download, campaignId, abTestGroup,
        DownloadAnalytics.AppContext.APPVIEW, action)
    if (downloadAction == DownloadModel.Action.INSTALL) {
      downloadAnalytics.installClicked(download.md5, download.packageName, trustedValue,
          editorsChoice, InstallType.INSTALL, action, offerResponseStatus)
    }
    if (DownloadModel.Action.MIGRATE == downloadAction) {
      downloadAnalytics.migrationClicked(download.md5, download.packageName, trustedValue,
          editorsChoice, InstallType.UPDATE_TO_APPC, action, offerResponseStatus)
    }
  }
}