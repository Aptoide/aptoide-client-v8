package cm.aptoide.pt.themes

import cm.aptoide.pt.presenter.Presenter
import cm.aptoide.pt.presenter.View

class DarkThemeDialogPresenter(val view: DarkThemeDialogView,
                               val newFeatureManager: NewFeatureManager,
                               val themeManager: ThemeManager,
                               val themeAnalytics: ThemeAnalytics) : Presenter {

  override fun present() {
    handleDialogShown()
    handleDismissClick()
    handleTurnItOnClick()
  }

  private fun handleDialogShown() {
    view.lifecycleEvent
        .filter { e -> e == View.LifecycleEvent.RESUME }
        .doOnNext {
          newFeatureManager.setFeatureAsShown()
          newFeatureManager.unscheduleNotification()
        }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, {})
  }

  private fun handleTurnItOnClick() {
    view.lifecycleEvent
        .filter { e -> e == View.LifecycleEvent.CREATE }
        .flatMap { view.clickTurnItOn() }
        .doOnNext {
          view.dismissView()
          themeManager.setThemeOption(ThemeManager.ThemeOption.DARK)
          themeManager.resetToBaseTheme()
          themeAnalytics.setDarkThemeUserProperty(themeManager.getDarkThemeMode())
          themeAnalytics.sendDarkThemeDialogTurnItOnClickEvent("HomeFragment")
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
          themeAnalytics.sendDarkThemeDismissClickEvent("HomeFragment")
        }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, {})
  }

}