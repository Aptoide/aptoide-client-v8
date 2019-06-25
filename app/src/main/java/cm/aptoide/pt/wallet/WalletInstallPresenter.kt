package cm.aptoide.pt.wallet

import cm.aptoide.pt.actions.PermissionManager
import cm.aptoide.pt.actions.PermissionService
import cm.aptoide.pt.presenter.Presenter
import cm.aptoide.pt.presenter.View
import cm.aptoide.pt.promotions.WalletApp
import rx.Completable
import rx.Observable
import rx.Scheduler
import rx.schedulers.Schedulers

class WalletInstallPresenter(val view: WalletInstallView,
                             val walletInstallManager: WalletInstallManager,
                             val navigator: WalletInstallNavigator,
                             val permissionManager: PermissionManager,
                             val permissionService: PermissionService,
                             val viewScheduler: Scheduler, val io: Scheduler) : Presenter {

  override fun present() {
    loadWalletInstall()
    handleCloseButtonClick()
    handleWalletInstalled()
    handleCancelDownloadButton()
  }

  private fun handleCancelDownloadButton() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> View.LifecycleEvent.CREATE == lifecycleEvent }
        .flatMap { view.cancelDownloadButtonClicked() }
        .flatMap { walletInstallManager.getWallet() }.first()
        .doOnNext { walletInstallManager.removeDownload(it) }
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

  private fun handleWalletInstalled() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> View.LifecycleEvent.CREATE == lifecycleEvent }
        .flatMap { walletInstallManager.onWalletInstalled() }
        .observeOn(viewScheduler)
        .doOnNext { view.showInstallationSuccessView() }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, {
          it.printStackTrace()
          view.dismissDialog()
        })
  }

  private fun loadWalletInstall() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> View.LifecycleEvent.CREATE == lifecycleEvent }
        .flatMap {
          showWalletInitialState()
        }.observeOn(viewScheduler)
        .doOnNext { view.showIndeterminateDownload() }.flatMapCompletable {
          handleWalletDownload(it.second)
        }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, {
          it.printStackTrace()
          view.dismissDialog()
        })
  }

  private fun handleWalletDownload(walletApp: WalletApp): Completable? {

    return Observable.defer {
      if (walletInstallManager.shouldShowRootInstallWarningPopup()) {
        view.showRootInstallWarningPopup()
            ?.doOnNext { answer -> walletInstallManager.allowRootInstall(answer) }
      }
      Observable.just(null)
    }.observeOn(viewScheduler)
        .flatMap {
          permissionManager.requestDownloadAccessValidatingMobileData(permissionService)
              .flatMap { success ->
                permissionManager.requestExternalStoragePermission(permissionService)
              }
              .observeOn(Schedulers.io())
              .flatMapCompletable { void ->
                walletInstallManager.downloadApp(walletApp)
              }
        }.toCompletable()
  }

  private fun showWalletInitialState(): Observable<Pair<String?, WalletApp>>? {
    return Observable.zip(walletInstallManager.getAppIcon(),
        walletInstallManager.getWallet()) { appIcon, walletApp ->
      Pair<String?, WalletApp>(appIcon, walletApp)
    }.first().observeOn(viewScheduler)
        .doOnNext { pair ->
          view.showWalletInstallationView(pair.first, pair.second)
        }
  }


  private fun handleCloseButtonClick() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> View.LifecycleEvent.CREATE == lifecycleEvent }
        .flatMap { view.closeButtonClicked() }
        .doOnNext { view.dismissDialog() }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, {
          it.printStackTrace()
          view.dismissDialog()
        })
  }
}