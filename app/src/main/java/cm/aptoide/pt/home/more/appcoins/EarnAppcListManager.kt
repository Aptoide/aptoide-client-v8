package cm.aptoide.pt.home.more.appcoins

import cm.aptoide.pt.app.DownloadModel
import cm.aptoide.pt.promotions.WalletApp
import cm.aptoide.pt.wallet.WalletAppProvider
import cm.aptoide.pt.wallet.WalletInstallManager
import rx.Completable
import rx.Observable

class EarnAppcListManager(private val walletAppProvider: WalletAppProvider,
                          private val walletInstallManager: WalletInstallManager) {

  private var cachedWalletApp: WalletApp? = null

  fun getWalletApp(): Observable<WalletApp> {
    return if (cachedWalletApp != null)
      Observable.just(cachedWalletApp)
    else walletAppProvider.getWalletApp()
        .doOnNext { walletApp -> cachedWalletApp = walletApp }
  }

  fun observeWalletApp(): Observable<WalletApp> {
    return walletAppProvider.getWalletApp()
  }

  fun shouldShowRootInstallWarningPopup(): Boolean {
    return walletInstallManager.shouldShowRootInstallWarningPopup()
  }

  fun allowRootInstall(answer: Boolean) {
    return walletInstallManager.allowRootInstall(answer)
  }

  fun downloadApp(): Completable {
    return getWalletApp()
        .flatMapCompletable { app -> walletInstallManager.downloadApp(app) }
        .toCompletable()
  }

  fun loadWalletDownloadModel(): Observable<DownloadModel> {
    return getWalletApp()
        .flatMap { app -> walletInstallManager.loadDownloadModel(app) }
  }

  fun pauseWalletDownload(): Completable {
    return getWalletApp()
        .flatMapCompletable { app -> walletInstallManager.pauseDownload(app) }
        .toCompletable()
  }

  fun cancelWalletDownload(): Completable {
    return getWalletApp()
        .flatMapCompletable { app -> walletInstallManager.cancelDownload(app) }
        .toCompletable()
  }

  fun resumeWalletDownload(): Completable {
    return getWalletApp()
        .flatMapCompletable { app -> walletInstallManager.resumeDownload(app) }
        .toCompletable()
  }

  fun onWalletInstalled(): Observable<Boolean> {
    return walletInstallManager.onWalletInstalled()
  }
}