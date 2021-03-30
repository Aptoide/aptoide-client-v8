package cm.aptoide.pt.download.view.outofspace

import cm.aptoide.pt.crashreports.CrashReport
import cm.aptoide.pt.presenter.Presenter
import cm.aptoide.pt.presenter.View
import rx.Scheduler

class OutOfSpaceDialogPresenter(private val view: OutOfSpaceDialogView,
                                private val crashReporter: CrashReport,
                                private val viewScheduler: Scheduler,
                                private val ioScheduler: Scheduler,
                                private val outOfSpaceManager: OutOfSpaceManager) : Presenter {
  override fun present() {
    loadAppsToUninstall()
    uninstallApp()
    handleCancelButtonClick()
  }

  private fun handleCancelButtonClick() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .flatMap { view.cancelButtonClick() }
        .doOnNext { view.dismiss() }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, { e -> crashReporter.log(e) })
  }

  private fun uninstallApp() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .flatMap { view.uninstallClick() }
        .flatMapCompletable { outOfSpaceManager.uninstallApp(it) }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, { e -> crashReporter.log(e) })
  }

  private fun loadAppsToUninstall() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .observeOn(ioScheduler)
        .flatMap { outOfSpaceManager.getInstalledApps() }
        .observeOn(viewScheduler)
        .doOnNext { view.showInstalledApps(it) }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, { e -> crashReporter.log(e) })
  }

}