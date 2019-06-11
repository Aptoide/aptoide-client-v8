package cm.aptoide.pt.wallet

import cm.aptoide.pt.presenter.Presenter
import cm.aptoide.pt.presenter.View
import rx.exceptions.OnErrorNotImplementedException

class WalletInstallPresenter(val view: WalletInstallView, val navigator: WalletInstallNavigator) :
    Presenter {

  override fun present() {
    showDialog()
  }

  private fun showDialog() {
    view.getLifecycleEvent()
        .filter { lifecycleEvent -> View.LifecycleEvent.CREATE == lifecycleEvent }
        .doOnNext {
          navigator.navigateToWalletInstallView()
        }
        .compose(view.bindUntilEvent<View.LifecycleEvent>(View.LifecycleEvent.DESTROY))
        .subscribe({}, { throwable -> throw OnErrorNotImplementedException(throwable) })
  }
}