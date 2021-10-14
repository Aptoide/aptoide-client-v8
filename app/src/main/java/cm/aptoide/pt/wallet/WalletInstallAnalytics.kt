package cm.aptoide.pt.wallet

import cm.aptoide.analytics.AnalyticsManager
import cm.aptoide.analytics.implementation.navigation.NavigationTracker
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory
import cm.aptoide.pt.ads.WalletAdsOfferManager
import cm.aptoide.pt.app.DownloadModel
import cm.aptoide.pt.app.DownloadStateParser
import cm.aptoide.pt.database.room.RoomDownload
import cm.aptoide.pt.download.DownloadAnalytics
import cm.aptoide.pt.download.InstallType
import cm.aptoide.pt.install.InstallAnalytics
import cm.aptoide.pt.notification.NotificationAnalytics
import java.util.*

class WalletInstallAnalytics(val downloadAnalytics: DownloadAnalytics,
                             val notificationAnalytics: NotificationAnalytics,
                             val installAnalytics: InstallAnalytics,
                             val downloadStateParser: DownloadStateParser,
                             val analyticsManager: AnalyticsManager,
                             val navigationTracker: NavigationTracker) {

  private var shouldRegister: Boolean = true
  private val APP_BUNDLE = "app_bundle"
  private val TYPE = "type"
  private val APPLICATION_NAME = "Application Name"
  private val APPLICATION_PUBLISHER = "Application Publisher"
  private val CLICK_INSTALL = "Clicked on install button"
  private val VIEW_CONTEXT = "WalletInstallActivity"


  private fun setupDownloadAnalyticsEvents(download: RoomDownload, campaignId: Int,
                                           abTestGroup: String,
                                           downloadAction: DownloadModel.Action?,
                                           action: AnalyticsManager.Action,
                                           offerResponseStatus: WalletAdsOfferManager.OfferResponseStatus,
                                           hasObbs: Boolean) {
    downloadAnalytics.downloadStartEvent(download, campaignId, abTestGroup,
        DownloadAnalytics.AppContext.WALLET_INSTALL_ACTIVITY, action, false, false)
    if (downloadAction == DownloadModel.Action.INSTALL) {
      downloadAnalytics.installClicked(
          download.md5, download.packageName,
          download.versionCode, action, offerResponseStatus, false, download.hasAppc(),
          download.hasSplits(), download.trustedBadge, null, download.storeName, action.toString(),
          hasObbs
      )
    }
    if (DownloadModel.Action.MIGRATE == downloadAction) {
      downloadAnalytics.migrationClicked(
          download.md5, download.packageName, download.versionCode,
          action, offerResponseStatus, download.hasSplits(), download.trustedBadge,
          null,
          download.storeName, hasObbs
      )
    }
  }

  fun setupDownloadEvents(download: RoomDownload, downloadAction: DownloadModel.Action?,
                          appId: Long,
                          offerResponseStatus: WalletAdsOfferManager.OfferResponseStatus) {

    val campaignId = notificationAnalytics.getCampaignId(download.packageName, appId)
    val abTestGroup = notificationAnalytics.getAbTestingGroup(download.packageName, appId)
    setupDownloadAnalyticsEvents(
        download, campaignId, abTestGroup, downloadAction,
        AnalyticsManager.Action.CLICK, offerResponseStatus, download.hasObbs()
    )
    installAnalytics.installStarted(
        download.packageName, download.versionCode,
        AnalyticsManager.Action.INSTALL, DownloadAnalytics.AppContext.WALLET_INSTALL_ACTIVITY,
        downloadStateParser.getOrigin(download.action), campaignId, abTestGroup,
        downloadAction != null && downloadAction == DownloadModel.Action.MIGRATE,
        download.hasAppc(), download.hasSplits(), offerResponseStatus.toString(),
        download.trustedBadge,
        download.storeName,
        false, download.hasObbs()
    )

  }

  fun sendClickOnInstallButtonEvent(packageName: String, applicationPublisher: String,
                                    hasSplits: Boolean) {
    val map = HashMap<String, Any>()
    map[TYPE] = "Install"
    map[APPLICATION_NAME] = packageName
    map[APPLICATION_PUBLISHER] = applicationPublisher
    map[APP_BUNDLE] = hasSplits
    analyticsManager.logEvent(map, CLICK_INSTALL, AnalyticsManager.Action.CLICK,
        VIEW_CONTEXT)
  }

  fun setupHistoryTracker() {
    val historyTracker = getHistoryTracker()
    if (shouldRegister) {
      if (historyTracker == null) {
        throw RuntimeException("If "
            + this.javaClass
            .simpleName
            + " should be logged to screen history, it has to return a value on method NavigationTrackFragment#getHistoryTracker")
      }
      navigationTracker.registerScreen(historyTracker)
    }
    shouldRegister = false
  }

  private fun getHistoryTracker(): ScreenTagHistory? {
    return ScreenTagHistory.Builder.build(VIEW_CONTEXT)
  }

  fun sendNotEnoughSpaceErrorEvent(packageName: String?,
                                   versionCode: Int,
                                   downloadAction: DownloadModel.Action,
                                   offerResponseStatus: WalletAdsOfferManager.OfferResponseStatus?,
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