package cm.aptoide.pt.account.view.magiclink

import cm.aptoide.pt.presenter.Presenter
import cm.aptoide.pt.presenter.View

class CheckYourEmailPresenter(private val view: CheckYourEmailView,
                              private val navigator: CheckYourEmailNavigator) : Presenter {
  override fun present() {
    handleCheckEmailAppClick()
  }

  private fun handleCheckEmailAppClick() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> View.LifecycleEvent.CREATE == lifecycleEvent }
        .flatMap {
          view.getCheckYourEmailClick()
              .doOnNext { navigator.navigateToEmailApp() }
              .retry()
        }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, { e -> e.printStackTrace() })
  }
}