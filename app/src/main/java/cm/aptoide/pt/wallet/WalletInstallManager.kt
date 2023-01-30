package cm.aptoide.pt.wallet

import android.content.pm.PackageManager
import cm.aptoide.pt.aab.DynamicSplitsManager
import cm.aptoide.pt.aab.DynamicSplitsModel
import cm.aptoide.pt.ads.MoPubAdsManager
import cm.aptoide.pt.ads.WalletAdsOfferManager
import cm.aptoide.pt.app.DownloadModel
import cm.aptoide.pt.app.DownloadStateParser
import cm.aptoide.pt.database.room.RoomDownload
import cm.aptoide.pt.download.DownloadFactory
import cm.aptoide.pt.install.AppInstallerStatusReceiver
import cm.aptoide.pt.install.AptoideInstalledAppsRepository
import cm.aptoide.pt.install.InstallManager
import cm.aptoide.pt.packageinstaller.InstallStatus
import cm.aptoide.pt.promotions.WalletApp
import cm.aptoide.pt.utils.AptoideUtils
import hu.akarnokd.rxjava.interop.RxJavaInterop
import rx.Completable
import rx.Observable

class WalletInstallManager(
  val packageManager: PackageManager,
  val installManager: InstallManager,
  val downloadFactory: DownloadFactory,
  val downloadStateParser: DownloadStateParser,
  val moPubAdsManager: MoPubAdsManager,
  val walletInstallAnalytics: WalletInstallAnalytics,
  val aptoideInstalledAppsRepository: AptoideInstalledAppsRepository,
  val walletAppProvider: WalletAppProvider,
  val appInstallerStatusReceiver: AppInstallerStatusReceiver,
  val dynamicSplitsManager: DynamicSplitsManager
) {

  fun getAppIcon(packageName: String?): Observable<String> {
    return Observable.fromCallable {
      AptoideUtils.SystemU.getApkIconPath(
        packageManager.getPackageInfo(packageName!!, 0)
      )
    }.onErrorReturn { null }
  }

  fun shouldShowRootInstallWarningPopup(): Boolean {
    return installManager.showWarning()
  }

  fun allowRootInstall(answer: Boolean) {
    installManager.rootInstallAllowed(answer)
  }

  fun downloadApp(walletApp: WalletApp): Completable {
    return RxJavaInterop.toV1Single<DynamicSplitsModel>(
      dynamicSplitsManager.getAppSplitsByMd5(walletApp.md5sum!!)
    ).flatMapObservable {
      Observable.just(
        downloadFactory.create(
          downloadStateParser.parseDownloadAction(DownloadModel.Action.INSTALL),
          walletApp.appName,
          walletApp.packageName,
          walletApp.md5sum, walletApp.icon, walletApp.versionName, walletApp.versionCode,
          walletApp.path, walletApp.pathAlt, walletApp.obb,
          false, walletApp.size, walletApp.splits, walletApp.requiredSplits,
          walletApp.trustedBadge, walletApp.storeName, it.dynamicSplitsList
        )
      )
    }
      .flatMapSingle { download ->
        moPubAdsManager.adsVisibilityStatus.doOnSuccess { responseStatus ->
          setupDownloadEvents(
            download, DownloadModel.Action.INSTALL, walletApp.id,
            responseStatus, walletApp.packageName, walletApp.developer
          )
        }.map {
          download
        }
      }
      .flatMapCompletable { download ->
        installManager.splitInstall(download)
      }
      .toCompletable()
  }

  private fun setupDownloadEvents(
    download: RoomDownload,
    downloadAction: DownloadModel.Action?,
    appId: Long,
    offerResponseStatus: WalletAdsOfferManager.OfferResponseStatus,
    packageName: String,
    developer: String
  ) {
    walletInstallAnalytics.setupDownloadEvents(
      download, downloadAction, appId,
      offerResponseStatus
    )
    walletInstallAnalytics.sendClickOnInstallButtonEvent(
      packageName, developer,
      download.hasSplits()
    )
  }

  fun onWalletInstalled(): Observable<Boolean> {
    return aptoideInstalledAppsRepository.isInstalled("com.appcoins.wallet").filter { isInstalled ->
      isInstalled
    }
  }

  fun getWallet(): Observable<WalletApp> {
    return walletAppProvider.getWalletApp()
  }

  fun removeDownload(app: WalletApp): Completable? {
    return installManager.cancelInstall(app.md5sum, app.packageName, app.versionCode)
  }

  fun cancelDownload(app: WalletApp): Completable {
    return Completable.fromAction { removeDownload(app) }
  }

  fun loadDownloadModel(walletApp: WalletApp): Observable<DownloadModel> {
    return installManager.getInstall(walletApp.md5sum, walletApp.packageName, walletApp.versionCode)
      .map { install ->
        DownloadModel(
          downloadStateParser.parseDownloadType(install.type, false),
          install.progress, downloadStateParser.parseDownloadState(
            install.state,
            install.isIndeterminate
          ), install.appSize
        )
      }
  }

  fun pauseDownload(app: WalletApp): Completable {
    return installManager.pauseInstall(app.md5sum)
  }

  fun resumeDownload(app: WalletApp): Completable {
    return installManager.getDownload(app.md5sum)
      .flatMap { download ->
        moPubAdsManager.adsVisibilityStatus
          .doOnSuccess { responseStatus ->
            setupDownloadEvents(
              download, DownloadModel.Action.INSTALL, app.id,
              responseStatus, app.packageName, app.developer
            )
          }.map {
            download
          }
      }
      .flatMapCompletable { download ->
        installManager.splitInstall(download)
      }
  }

  fun onWalletInstallationCanceled(): Observable<Boolean> {
    return appInstallerStatusReceiver.installerInstallStatus
      .map { installStatus ->
        InstallStatus.Status.CANCELED.equals(installStatus.status)
      }.filter { isCanceled -> isCanceled }
  }

  fun setupAnalyticsHistoryTracker() {
    walletInstallAnalytics.setupHistoryTracker()
  }
}