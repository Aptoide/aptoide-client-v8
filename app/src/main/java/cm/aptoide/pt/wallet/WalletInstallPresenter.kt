package cm.aptoide.pt.wallet

import cm.aptoide.pt.presenter.Presenter
import cm.aptoide.pt.presenter.View

class WalletInstallPresenter(val view: WalletInstallView,
                             val walletInstallManager: WalletInstallManager,
                             val navigator: WalletInstallNavigator) : Presenter {

  override fun present() {
    loadWalletInstall()
  }

  private fun loadWalletInstall() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> View.LifecycleEvent.CREATE == lifecycleEvent }
        .flatMap {
          walletInstallManager.getAppIcon()
        }
        .doOnNext { appIcon ->
          view.showWalletInstallationView(appIcon)
        }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, {
          view.dismissDialog()
        })
  }
}