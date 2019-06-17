package cm.aptoide.pt.wallet

import cm.aptoide.pt.presenter.Presenter
import cm.aptoide.pt.presenter.View
import cm.aptoide.pt.promotions.PromotionsManager
import cm.aptoide.pt.promotions.WalletApp
import rx.Observable
import rx.Scheduler

class WalletInstallPresenter(val view: WalletInstallView,
                             val walletInstallManager: WalletInstallManager,
                             val navigator: WalletInstallNavigator,
                             val promotionsManager: PromotionsManager,
                             val viewScheduler: Scheduler) : Presenter {

  override fun present() {
    loadWalletInstall()
  }

  private fun loadWalletInstall() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> View.LifecycleEvent.CREATE == lifecycleEvent }
        .flatMap {
          showWalletInitialState()
        }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, {
          view.dismissDialog()
        })
  }

  private fun showWalletInitialState(): Observable<Pair<String, WalletApp>>? {
    return Observable.zip(walletInstallManager.getAppIcon(),
        promotionsManager.walletApp) { appIcon, walletApp ->
      Pair<String, WalletApp>(appIcon, walletApp)
    }.first().observeOn(viewScheduler)
        .doOnNext { pair ->
          view.showWalletInstallationView(pair.first, pair.second)
        }
  }


}