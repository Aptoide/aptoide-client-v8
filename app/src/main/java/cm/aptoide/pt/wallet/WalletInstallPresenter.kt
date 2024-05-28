package cm.aptoide.pt.wallet

import android.os.Build
import cm.aptoide.pt.actions.PermissionManager
import cm.aptoide.pt.actions.PermissionService
import cm.aptoide.pt.ads.MoPubAdsManager
import cm.aptoide.pt.app.DownloadModel
import cm.aptoide.pt.presenter.Presenter
import cm.aptoide.pt.presenter.View
import cm.aptoide.pt.promotions.WalletApp
import rx.Completable
import rx.Observable
import rx.Scheduler
import rx.schedulers.Schedulers

class WalletInstallPresenter(
  val view: WalletInstallView,
  val walletInstallManager: WalletInstallManager,
  val navigator: WalletInstallNavigator,
  val permissionManager: PermissionManager,
  val permissionService: PermissionService,
  val viewScheduler: Scheduler,
  val io: Scheduler,
  val configuration: WalletInstallConfiguration,
  val walletInstallAnalytics: WalletInstallAnalytics,
  val moPubAdsManager: MoPubAdsManager
) : Presenter {

  override fun present() {
    loadWalletInstall()
    handleCloseButtonClick()
    handleCancelDownloadButton()
    handleAnalyticsContextSetup()
  }

  private fun handleAnalyticsContextSetup() {
    view.lifecycleEvent
      .filter { lifecycleEvent -> View.LifecycleEvent.CREATE == lifecycleEvent }
      .doOnNext { walletInstallManager.setupAnalyticsHistoryTracker() }
      .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
      .subscribe({}, {
        it.printStackTrace()
        view.dismissDialog()
      })
  }

  private fun handleCancelDownloadButton() {
    view.lifecycleEvent
      .filter { lifecycleEvent -> View.LifecycleEvent.CREATE == lifecycleEvent }
      .flatMap { view.cancelDownloadButtonClicked() }
      .flatMap { walletInstallManager.getWallet() }.first()
      .doOnNext { walletApp -> walletInstallManager.removeDownload(walletApp) }
      .observeOn(viewScheduler)
      .doOnCompleted {
        view.dismissDialog()
      }
      .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
      .subscribe({}, {
        it.printStackTrace()
        view.dismissDialog()
      })
  }

  private fun handleWalletInstallation(): Observable<Boolean> {
    return walletInstallManager.onWalletInstalled().first()
      .observeOn(viewScheduler)
      .doOnNext { view.showInstallationSuccessView() }
  }

  private fun loadWalletInstall() {
    view.lifecycleEvent
      .filter { lifecycleEvent -> View.LifecycleEvent.CREATE == lifecycleEvent }
      .doOnNext {
        if (!hasMinimumSdk())
          view.showSdkErrorView()
      }
      .filter { hasMinimumSdk() }
      .flatMap {
        showWalletInitialState()
      }
      .filter { walletInitialState -> !walletInitialState.second.isInstalled }
      .observeOn(viewScheduler)
      .doOnNext { view.showIndeterminateDownload() }
      .flatMap { walletInitialState ->
        startWalletDownload(walletInitialState.second).andThen(
          Observable.merge(
            handleWalletInstallation(),
            observeDownloadProgress(walletInitialState.second),
            handleInstallDialogCancelButtonPress()
          )
        )
      }
      .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
      .subscribe({}, {
        it.printStackTrace()
        view.dismissDialog()
      })
  }

  private fun observeDownloadProgress(walletApp: WalletApp): Observable<WalletApp> {
    return walletInstallManager.loadDownloadModel(walletApp)
      .flatMap { downloadModel -> verifyNotEnoughSpaceError(downloadModel, walletApp) }
      .observeOn(viewScheduler)
      .doOnNext { view.showDownloadState(it) }.map { walletApp }
  }

  private fun verifyNotEnoughSpaceError(
    downloadModel: DownloadModel,
    walletApp: WalletApp
  ): Observable<DownloadModel> {
    if (downloadModel.downloadState == DownloadModel.DownloadState.NOT_ENOUGH_STORAGE_ERROR) {
      Observable.just(downloadModel).doOnNext { _ ->
        walletInstallAnalytics.sendNotEnoughSpaceErrorEvent(
          walletApp.md5sum
        )
      }
    }
    return Observable.just(downloadModel)
  }

  private fun startWalletDownload(walletApp: WalletApp): Completable {

    return Observable.defer {
      if (walletInstallManager.shouldShowRootInstallWarningPopup()) {
        view.showRootInstallWarningPopup()
          .doOnNext { answer -> walletInstallManager.allowRootInstall(answer) }
      }
      Observable.just(walletApp)
    }.observeOn(viewScheduler)
      .flatMap {
        permissionManager.requestDownloadAllowingMobileData(permissionService)
          .flatMap {
            permissionManager.requestExternalStoragePermission(permissionService)
          }
          .observeOn(Schedulers.io())
          .flatMapCompletable {
            walletInstallManager.downloadApp(walletApp)
          }
      }.toCompletable()
  }

  private fun showWalletInitialState(): Observable<Pair<String?, WalletApp>>? {
    return Observable.zip(
      walletInstallManager.getAppIcon(configuration.appPackageName),
      walletInstallManager.getWallet()
    ) { appIcon, walletApp ->
      Pair<String?, WalletApp>(appIcon, walletApp)
    }.first().observeOn(viewScheduler)
      .doOnNext { pair ->
        if (pair.second.isInstalled) {
          view.showWalletInstalledAlreadyView()
        } else {
          view.showWalletInstallationView(pair.first, pair.second)
        }
      }
  }

  private fun hasMinimumSdk(): Boolean {
    return Build.VERSION.SDK_INT >= 21
  }

  private fun handleCloseButtonClick() {
    view.lifecycleEvent
      .filter { lifecycleEvent -> View.LifecycleEvent.CREATE == lifecycleEvent }
      .flatMap { view.closeButtonClicked() }
      .doOnNext { view.dismissDialog() }
      .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
      .subscribe({}, { error ->
        error.printStackTrace()
        view.dismissDialog()
      })
  }

  private fun handleInstallDialogCancelButtonPress(): Observable<Boolean> {
    return walletInstallManager.onWalletInstallationCanceled().first()
      .doOnNext { view.dismissDialog() }
  }
}