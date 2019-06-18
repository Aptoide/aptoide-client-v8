package cm.aptoide.pt.wallet

import android.content.pm.PackageManager
import cm.aptoide.analytics.AnalyticsManager
import cm.aptoide.pt.ads.MoPubAdsManager
import cm.aptoide.pt.ads.WalletAdsOfferManager
import cm.aptoide.pt.app.DownloadModel
import cm.aptoide.pt.app.DownloadStateParser
import cm.aptoide.pt.database.realm.Download
import cm.aptoide.pt.download.AppContext
import cm.aptoide.pt.download.DownloadFactory
import cm.aptoide.pt.install.InstallAnalytics
import cm.aptoide.pt.install.InstallManager
import cm.aptoide.pt.install.InstalledRepository
import cm.aptoide.pt.notification.NotificationAnalytics
import cm.aptoide.pt.promotions.WalletApp
import cm.aptoide.pt.utils.AptoideUtils
import rx.Completable
import rx.Observable

class WalletInstallManager(val configuration: WalletInstallConfiguration,
                           val walletAppProvider: WalletAppProvider,
                           val packageManager: PackageManager, val installManager: InstallManager,
                           val downloadFactory: DownloadFactory,
                           val downloadStateParser: DownloadStateParser,
                           val moPubAdsManager: MoPubAdsManager,
                           val notificationAnalytics: NotificationAnalytics,
                           val installAnalytics: InstallAnalytics,
                           val walletInstallAnalytics: WalletInstallAnalytics,
                           val installedRepository: InstalledRepository) {

  fun getAppIcon(): Observable<String> {
    return Observable.just(AptoideUtils.SystemU.getApkIconPath(
        packageManager.getPackageInfo(configuration.appPackageName, 0)))
  }

  fun shouldShowRootInstallWarningPopup(): Boolean {
    return installManager.showWarning()
  }

  fun allowRootInstall(answer: Boolean) {
    installManager.rootInstallAllowed(answer)
  }

  fun downloadApp(walletApp: WalletApp): Completable {
    return Observable.just(
        downloadFactory.create(
            downloadStateParser.parseDownloadAction(DownloadModel.Action.INSTALL),
            walletApp.appName,
            walletApp.packageName,
            walletApp.md5sum, walletApp.icon, walletApp.versionName, walletApp.versionCode,
            walletApp.path, walletApp.pathAlt, walletApp.obb,
            false, walletApp.size!!))
        .flatMapSingle { download ->
          moPubAdsManager.adsVisibilityStatus.doOnSuccess {
            setupDownloadEvents(download, DownloadModel.Action.INSTALL, walletApp.id, "no", "no",
                it)
          }.map { download }
        }.flatMapCompletable { download -> installManager.install(download) }.toCompletable()
  }

  private fun setupDownloadEvents(download: Download, downloadAction: DownloadModel.Action?,
                                  appId: Long, malwareRank: String?, editorsChoice: String?,
                                  offerResponseStatus: WalletAdsOfferManager.OfferResponseStatus) {
    val campaignId = notificationAnalytics.getCampaignId(download.packageName, appId)
    val abTestGroup = notificationAnalytics.getAbTestingGroup(download.packageName, appId)
    walletInstallAnalytics.setupDownloadEvents(download, campaignId, abTestGroup, downloadAction,
        AnalyticsManager.Action.CLICK, malwareRank, editorsChoice, offerResponseStatus)
    installAnalytics.installStarted(download.packageName, download.versionCode,
        AnalyticsManager.Action.INSTALL, AppContext.APPVIEW,
        downloadStateParser.getOrigin(download.action), campaignId, abTestGroup,
        downloadAction != null && downloadAction == DownloadModel.Action.MIGRATE)
  }

  fun onWalletInstalled(): Observable<Boolean> {
    return installedRepository.isInstalled("com.appcoins.wallet").filter {
      it
    }
  }
}