package cm.aptoide.pt.wallet

import cm.aptoide.analytics.AnalyticsManager
import cm.aptoide.analytics.implementation.navigation.NavigationTracker
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory
import cm.aptoide.pt.app.DownloadModel
import cm.aptoide.pt.app.DownloadStateParser
import cm.aptoide.pt.database.room.RoomDownload
import cm.aptoide.pt.download.DownloadAnalytics
import cm.aptoide.pt.download.SplitAnalyticsMapper
import cm.aptoide.pt.install.InstallAnalytics
import cm.aptoide.pt.notification.NotificationAnalytics

class WalletInstallAnalytics(
  val downloadAnalytics: DownloadAnalytics,
  val notificationAnalytics: NotificationAnalytics,
  val installAnalytics: InstallAnalytics,
  val downloadStateParser: DownloadStateParser,
  val analyticsManager: AnalyticsManager,
  val navigationTracker: NavigationTracker,
  val splitAnalyticsMapper: SplitAnalyticsMapper
) {

  private var shouldRegister: Boolean = true
  private val APP_BUNDLE = "app_bundle"
  private val TYPE = "type"
  private val APPLICATION_NAME = "Application Name"
  private val APPLICATION_PUBLISHER = "Application Publisher"
  private val CLICK_INSTALL = "Clicked on install button"
  private val VIEW_CONTEXT = "WalletInstallActivity"

  private fun setupDownloadAnalyticsEvents(
    download: RoomDownload, campaignId: Int,
    abTestGroup: String,
    downloadAction: DownloadModel.Action?,
    action: AnalyticsManager.Action,
    hasObbs: Boolean
  ) {
    downloadAnalytics.downloadStartEvent(
      download, campaignId, abTestGroup,
      DownloadAnalytics.AppContext.WALLET_INSTALL_ACTIVITY, action, false, false
    )
    if (downloadAction == DownloadModel.Action.INSTALL) {
      downloadAnalytics.installClicked(
        download.md5, download.packageName,
        download.versionCode, action, false, download.hasAppc(),
        download.hasSplits(), download.trustedBadge, null, download.storeName, action.toString(),
        hasObbs, splitAnalyticsMapper.getSplitTypesAsString(download.splits), true, ""
      )
    }
    if (DownloadModel.Action.MIGRATE == downloadAction) {
      downloadAnalytics.migrationClicked(
        download.md5, download.packageName, download.versionCode,
        action, download.hasSplits(), download.trustedBadge,
        null,
        download.storeName, hasObbs,
        splitAnalyticsMapper.getSplitTypesAsString(download.splits), true, ""
      )
    }
  }

  fun setupDownloadEvents(
    download: RoomDownload, downloadAction: DownloadModel.Action?,
    appId: Long
  ) {
    val campaignId = notificationAnalytics.getCampaignId(download.packageName, appId)
    val abTestGroup = notificationAnalytics.getAbTestingGroup(download.packageName, appId)
    setupDownloadAnalyticsEvents(
      download, campaignId, abTestGroup, downloadAction,
      AnalyticsManager.Action.CLICK, download.hasObbs()
    )
    installAnalytics.installStarted(
      download.packageName, download.versionCode,
      AnalyticsManager.Action.INSTALL, DownloadAnalytics.AppContext.WALLET_INSTALL_ACTIVITY,
      downloadStateParser.getOrigin(download.action), campaignId, abTestGroup,
      downloadAction != null && downloadAction == DownloadModel.Action.MIGRATE,
      download.hasAppc(), download.hasSplits(),
      download.trustedBadge,
      download.storeName,
      false, download.hasObbs(), splitAnalyticsMapper.getSplitTypesAsString(download.splits),
      true, ""
    )
  }

  fun sendClickOnInstallButtonEvent(
    packageName: String, applicationPublisher: String,
    hasSplits: Boolean
  ) {
    val map = HashMap<String, Any>()
    map[TYPE] = "Install"
    map[APPLICATION_NAME] = packageName
    map[APPLICATION_PUBLISHER] = applicationPublisher
    map[APP_BUNDLE] = hasSplits
    analyticsManager.logEvent(
      map, CLICK_INSTALL, AnalyticsManager.Action.CLICK,
      VIEW_CONTEXT
    )
  }

  fun setupHistoryTracker() {
    val historyTracker = getHistoryTracker()
    if (shouldRegister) {
      if (historyTracker == null) {
        throw RuntimeException(
          "If "
            + this.javaClass
            .simpleName
            + " should be logged to screen history, it has to return a value on method NavigationTrackFragment#getHistoryTracker"
        )
      }
      navigationTracker.registerScreen(historyTracker)
    }
    shouldRegister = false
  }

  private fun getHistoryTracker(): ScreenTagHistory? {
    return ScreenTagHistory.Builder.build(VIEW_CONTEXT)
  }

  fun sendNotEnoughSpaceErrorEvent(md5: String?) {
    downloadAnalytics.sendNotEnoughSpaceError(
      md5
    )
  }
}