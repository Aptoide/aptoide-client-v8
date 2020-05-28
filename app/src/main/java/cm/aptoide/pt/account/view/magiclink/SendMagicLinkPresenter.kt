package cm.aptoide.pt.account.view.magiclink

import cm.aptoide.accountmanager.AptoideAccountManager
import cm.aptoide.pt.presenter.Presenter
import cm.aptoide.pt.presenter.View
import rx.Observable
import rx.Scheduler

class SendMagicLinkPresenter(
    private val view: MagicLinkView,
    private val accountManager: AptoideAccountManager,
    private val navigator: SendMagicLinkNavigator,
    private val viewScheduler: Scheduler) : Presenter {

  override fun present() {
    handleSendMagicLinkClick()
    handleEmailChangeEvents()
  }

  private fun handleEmailChangeEvents() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> View.LifecycleEvent.CREATE == lifecycleEvent }
        .flatMap {
          view.getEmailTextChangeEvent()
              .doOnNext { view.setInitialState() }
              .retry()
        }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, { e -> e.printStackTrace() })
  }

  private fun handleSendMagicLinkClick() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> View.LifecycleEvent.CREATE == lifecycleEvent }
        .flatMap {
          view.getMagicLinkClick()
              .flatMap { email ->
                validateEmail(email)
                    .filter { valid -> valid }
                    .observeOn(viewScheduler)
                    .doOnNext { view.setLoadingScreen() }
                    .flatMapCompletable {
                      accountManager.sendMagicLink(email)
                          .observeOn(viewScheduler)
                          .doOnCompleted {
                            view.removeLoadingScreen()
                            navigator.navigateToCheckYourEmail(email)
                          }
                    }
              }
              .retry()
        }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, { e -> e.printStackTrace() })
  }

  private fun validateEmail(email: String): Observable<Boolean> {
    return accountManager.isEmailValid(email)
        .toObservable()
        .observeOn(viewScheduler)
        .doOnNext { isValid ->
          if (!isValid) {
            view.setEmailInvalidError()
          }
        }
  }

}