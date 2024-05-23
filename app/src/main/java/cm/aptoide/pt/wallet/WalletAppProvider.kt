package cm.aptoide.pt.wallet

import cm.aptoide.pt.AptoideApplication.APPCOINS_WALLET_PACKAGE_NAME
import cm.aptoide.pt.app.DownloadModel
import cm.aptoide.pt.app.DownloadStateParser
import cm.aptoide.pt.install.AptoideInstalledAppsRepository
import cm.aptoide.pt.install.Install
import cm.aptoide.pt.install.InstallManager
import cm.aptoide.pt.promotions.WalletApp
import cm.aptoide.pt.view.app.AppCenter
import cm.aptoide.pt.view.app.DetailedAppRequestResult
import rx.Observable

class WalletAppProvider(
  val appCenter: AppCenter, val aptoideInstalledAppsRepository: AptoideInstalledAppsRepository,
  val installManager: InstallManager,
  val downloadStateParser: DownloadStateParser
) {

  fun getWalletApp(): Observable<WalletApp> {
    return appCenter.loadDetailedApp(APPCOINS_WALLET_PACKAGE_NAME, "catappult")
      .toObservable()
      .map { app -> this.mapToWalletApp(app) }.flatMap { walletApp ->
        val walletAppObs = Observable.just<WalletApp>(walletApp)
        val isWalletInstalled = aptoideInstalledAppsRepository.isInstalled(walletApp.packageName)
        val walletDownload = installManager.getInstall(
          walletApp.md5sum, walletApp.packageName,
          walletApp.versionCode
        )
        Observable.combineLatest<WalletApp, Boolean, Install, WalletApp>(
          walletAppObs,
          isWalletInstalled, walletDownload
        ) { walletApp, isInstalled, walletDownload ->
          this.mergeToWalletApp(walletApp, isInstalled, walletDownload)
        }
      }
  }

  private fun mergeToWalletApp(
    walletApp: WalletApp, isInstalled: Boolean,
    walletDownload: Install
  ): WalletApp {
    val downloadModel = mapToDownloadModel(
      walletDownload.type, walletDownload.progress,
      walletDownload.state, walletDownload.isIndeterminate, walletDownload.appSize
    )
    walletApp.downloadModel = downloadModel
    walletApp.isInstalled = isInstalled
    return walletApp
  }

  private fun mapToDownloadModel(
    type: Install.InstallationType,
    progress: Int,
    state: Install.InstallationStatus,
    isIndeterminate: Boolean, appSize: Long
  ): DownloadModel {
    return DownloadModel(
      downloadStateParser.parseDownloadType(type, false),
      progress, downloadStateParser.parseDownloadState(state, isIndeterminate), appSize
    )
  }

  private fun mapToWalletApp(result: DetailedAppRequestResult): WalletApp {
    if (result.hasError() || result.isLoading) return WalletApp()
    val app = result.detailedApp
    return WalletApp(
      null, false, app.name, app.icon, app.id,
      app.packageName, app.md5, app.versionCode, app.versionName,
      app.path, app.pathAlt, app.obb, app.size, app.developer.name, app.stats.rating.average,
      app.splits,
      app.requiredSplits
    )
  }
}
