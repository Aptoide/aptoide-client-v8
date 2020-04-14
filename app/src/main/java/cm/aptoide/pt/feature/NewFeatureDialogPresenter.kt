package cm.aptoide.pt.feature

import cm.aptoide.pt.presenter.Presenter
import cm.aptoide.pt.presenter.View
import cm.aptoide.pt.themes.NewFeatureDialogView
import cm.aptoide.pt.themes.NewFeatureManager

class NewFeatureDialogPresenter(val view: NewFeatureDialogView,
                                private val newFeatureManager: NewFeatureManager,
                                private val newFeatureListener: NewFeatureListener) : Presenter {

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
          newFeatureListener.onActivateFeature()
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
          newFeatureListener.onDismiss()
        }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, {})
  }

}