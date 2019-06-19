package cm.aptoide.pt.wallet

import cm.aptoide.analytics.AnalyticsManager
import cm.aptoide.pt.ads.WalletAdsOfferManager
import cm.aptoide.pt.app.DownloadModel
import cm.aptoide.pt.app.DownloadStateParser
import cm.aptoide.pt.database.realm.Download
import cm.aptoide.pt.download.AppContext
import cm.aptoide.pt.download.DownloadAnalytics
import cm.aptoide.pt.download.InstallType
import cm.aptoide.pt.install.InstallAnalytics
import cm.aptoide.pt.notification.NotificationAnalytics

class WalletInstallAnalytics(val downloadAnalytics: DownloadAnalytics,
                             val notificationAnalytics: NotificationAnalytics,
                             val installAnalytics: InstallAnalytics,
                             val downloadStateParser: DownloadStateParser) {

  fun setupDownloadAnalyticsEvents(download: Download, campaignId: Int, abTestGroup: String,
                                   downloadAction: DownloadModel.Action?,
                                   action: AnalyticsManager.Action,
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

  fun setupDownloadEvents(download: Download, downloadAction: DownloadModel.Action?, appId: Long,
                          malwareRank: String?, editorsChoice: String?,
                          offerResponseStatus: WalletAdsOfferManager.OfferResponseStatus) {

    val campaignId = notificationAnalytics.getCampaignId(download.packageName, appId)
    val abTestGroup = notificationAnalytics.getAbTestingGroup(download.packageName, appId)
    setupDownloadAnalyticsEvents(download, campaignId, abTestGroup, downloadAction,
        AnalyticsManager.Action.CLICK, malwareRank, editorsChoice, offerResponseStatus)
    installAnalytics.installStarted(download.packageName, download.versionCode,
        AnalyticsManager.Action.INSTALL, AppContext.APPVIEW,
        downloadStateParser.getOrigin(download.action), campaignId, abTestGroup,
        downloadAction != null && downloadAction == DownloadModel.Action.MIGRATE)

  }
}