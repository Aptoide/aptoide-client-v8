package cm.aptoide.pt.wallet

import cm.aptoide.analytics.AnalyticsManager
import cm.aptoide.pt.ads.WalletAdsOfferManager
import cm.aptoide.pt.app.DownloadModel
import cm.aptoide.pt.app.DownloadStateParser
import cm.aptoide.pt.database.realm.Download
import cm.aptoide.pt.download.AppContext
import cm.aptoide.pt.download.DownloadAnalytics
import cm.aptoide.pt.install.InstallAnalytics
import cm.aptoide.pt.notification.NotificationAnalytics
import java.util.*

class WalletInstallAnalytics(val downloadAnalytics: DownloadAnalytics,
                             val notificationAnalytics: NotificationAnalytics,
                             val installAnalytics: InstallAnalytics,
                             val downloadStateParser: DownloadStateParser,
                             val analyticsManager: AnalyticsManager) {

  private val TYPE = "type"
  private val APPLICATION_NAME = "Application Name"
  private val APPLICATION_PUBLISHER = "Application Publisher"
  private val CLICK_INSTALL = "Clicked on install button"


  fun setupDownloadAnalyticsEvents(download: Download, campaignId: Int, abTestGroup: String,
                                   downloadAction: DownloadModel.Action?,
                                   action: AnalyticsManager.Action,
                                   offerResponseStatus: WalletAdsOfferManager.OfferResponseStatus) {
    downloadAnalytics.downloadStartEvent(download, campaignId, abTestGroup,
        DownloadAnalytics.AppContext.APPVIEW, action)
    if (downloadAction == DownloadModel.Action.INSTALL) {
      downloadAnalytics.installClicked(download.md5, download.packageName,
          action, offerResponseStatus, false)
    }
    if (DownloadModel.Action.MIGRATE == downloadAction) {
      downloadAnalytics.migrationClicked(download.md5, download.packageName, action,
          offerResponseStatus)
    }
  }

  fun setupDownloadEvents(download: Download, downloadAction: DownloadModel.Action?, appId: Long,
                          offerResponseStatus: WalletAdsOfferManager.OfferResponseStatus) {

    val campaignId = notificationAnalytics.getCampaignId(download.packageName, appId)
    val abTestGroup = notificationAnalytics.getAbTestingGroup(download.packageName, appId)
    setupDownloadAnalyticsEvents(download, campaignId, abTestGroup, downloadAction,
        AnalyticsManager.Action.CLICK, offerResponseStatus)
    installAnalytics.installStarted(download.packageName, download.versionCode,
        AnalyticsManager.Action.INSTALL, AppContext.APPVIEW,
        downloadStateParser.getOrigin(download.action), campaignId, abTestGroup,
        downloadAction != null && downloadAction == DownloadModel.Action.MIGRATE)

  }

  fun sendClickOnInstallButtonEvent(packageName: String, applicationPublisher: String) {
    val map = HashMap<String, Any>()
    map[TYPE] = "Install"
    map[APPLICATION_NAME] = packageName
    map[APPLICATION_PUBLISHER] = applicationPublisher
    analyticsManager.logEvent(map, CLICK_INSTALL, AnalyticsManager.Action.CLICK,
        "WalletInstallActivity")
  }
}