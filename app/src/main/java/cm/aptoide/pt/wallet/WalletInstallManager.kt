package cm.aptoide.pt.wallet

import android.content.pm.PackageManager
import cm.aptoide.pt.ads.MoPubAdsManager
import cm.aptoide.pt.ads.WalletAdsOfferManager
import cm.aptoide.pt.app.DownloadModel
import cm.aptoide.pt.app.DownloadStateParser
import cm.aptoide.pt.database.realm.Download
import cm.aptoide.pt.download.DownloadFactory
import cm.aptoide.pt.install.AppInstallerStatusReceiver
import cm.aptoide.pt.install.InstallManager
import cm.aptoide.pt.install.InstalledRepository
import cm.aptoide.pt.packageinstaller.InstallStatus
import cm.aptoide.pt.promotions.WalletApp
import cm.aptoide.pt.utils.AptoideUtils
import rx.Completable
import rx.Observable

class WalletInstallManager(val configuration: WalletInstallConfiguration,
                           val packageManager: PackageManager, val installManager: InstallManager,
                           val downloadFactory: DownloadFactory,
                           val downloadStateParser: DownloadStateParser,
                           val moPubAdsManager: MoPubAdsManager,
                           val walletInstallAnalytics: WalletInstallAnalytics,
                           val installedRepository: InstalledRepository,
                           val walletAppProvider: WalletAppProvider,
                           val appInstallerStatusReceiver: AppInstallerStatusReceiver) {

  fun getAppIcon(): Observable<String> {
    return Observable.fromCallable {
      AptoideUtils.SystemU.getApkIconPath(
          packageManager.getPackageInfo(configuration.appPackageName, 0))
    }.onErrorReturn { null }

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
            setupDownloadEvents(download, DownloadModel.Action.INSTALL, walletApp.id,
                it)
          }.map { download }
        }.flatMapCompletable { download -> installManager.splitInstall(download) }.toCompletable()
  }

  private fun setupDownloadEvents(download: Download, downloadAction: DownloadModel.Action?,
                                  appId: Long,
                                  offerResponseStatus: WalletAdsOfferManager.OfferResponseStatus) {
    walletInstallAnalytics.setupDownloadEvents(download, downloadAction, appId, offerResponseStatus)
  }

  fun onWalletInstalled(): Observable<Boolean> {
    return installedRepository.isInstalled("com.appcoins.wallet").filter {
      it
    }
  }

  fun getWallet(): Observable<WalletApp> {
    return walletAppProvider.getWalletApp()
  }

  fun removeDownload(app: WalletApp) {
    return installManager.removeInstallationFile(app.md5sum, app.packageName, app.versionCode)

  }

  fun loadDownloadModel(walletApp: WalletApp): Observable<DownloadModel> {
    return installManager.getInstall(walletApp.md5sum, walletApp.packageName, walletApp.versionCode)
        .map {
          DownloadModel(downloadStateParser.parseDownloadType(it.type, false, false, false),
              it.progress, downloadStateParser.parseDownloadState(it.state), null)
        }
  }

  fun onWalletInstallationCanceled(): Observable<Boolean> {
    return appInstallerStatusReceiver.installerInstallStatus
        .map {
          InstallStatus.Status.CANCELED.equals(it.status)
        }.filter { it }
  }
}