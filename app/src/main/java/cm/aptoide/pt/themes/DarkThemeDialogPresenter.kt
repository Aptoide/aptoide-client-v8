package cm.aptoide.pt.themes

import cm.aptoide.pt.presenter.Presenter
import cm.aptoide.pt.presenter.View

class DarkThemeDialogPresenter(val view: DarkThemeDialogView,
                               val darkThemeDialogManager: DarkThemeDialogManager) : Presenter {

  override fun present() {
    handleDismissClick()
    handleTurnItOnClick()
  }

  private fun handleTurnItOnClick() {
    view.lifecycleEvent
        .filter { e -> e == View.LifecycleEvent.CREATE }
        .flatMap { view.clickTurnItOn() }
        .doOnNext {
          view.dismissView()
          darkThemeDialogManager.setDarkTheme()
        }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, {})
  }

  private fun handleDismissClick() {
    view.lifecycleEvent
        .filter { e -> e == View.LifecycleEvent.CREATE }
        .flatMap { view.clickDismiss() }
        .doOnNext {
          view.dismissView()
        }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, {})
  }

}