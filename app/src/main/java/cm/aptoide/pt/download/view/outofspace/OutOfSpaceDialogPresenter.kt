package cm.aptoide.pt.download.view.outofspace

import cm.aptoide.pt.crashreports.CrashReport
import cm.aptoide.pt.presenter.Presenter
import cm.aptoide.pt.presenter.View
import rx.Scheduler

class OutOfSpaceDialogPresenter(private val view: OutOfSpaceDialogView,
                                private val crashReporter: CrashReport,
                                private val viewScheduler: Scheduler,
                                private val ioScheduler: Scheduler,
                                private val outOfSpaceManager: OutOfSpaceManager,
                                private val outOfSpaceNavigator: OutOfSpaceNavigator) : Presenter {

  override fun present() {
    loadAppsToUninstall()
    loadRequiredStorageSize()
    uninstallApp()
    handleDismissDialogButtonClick()
    handleUninstalledEnoughApps()
  }

  private fun loadRequiredStorageSize() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .flatMapSingle { outOfSpaceManager.getRequiredStorageSize() }
        .doOnNext {
          view.requiredSpaceToInstall(it)
        }.compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, { e -> crashReporter.log(e) })
  }

  private fun handleUninstalledEnoughApps() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .flatMap { outOfSpaceManager.uninstalledEnoughApps() }
        .doOnNext {
          outOfSpaceNavigator.clearedEnoughSpace()
          view.dismiss()
        }.compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, { e -> crashReporter.log(e) })
  }

  private fun handleDismissDialogButtonClick() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .flatMap { view.dismissDialogClick() }
        .doOnNext { view.dismiss() }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, { e -> crashReporter.log(e) })
  }

  private fun uninstallApp() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .flatMap { view.uninstallClick() }
        .flatMapSingle { outOfSpaceManager.uninstallApp(it) }
        .doOnNext { removedAppSize -> view.requiredSpaceToInstall(removedAppSize) }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, { e -> crashReporter.log(e) })
  }

  private fun loadAppsToUninstall() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .observeOn(ioScheduler)
        .flatMap { outOfSpaceManager.getInstalledApps() }
        .observeOn(viewScheduler)
        .doOnNext { appsList ->
          if (appsList.isNotEmpty()) {
            view.showInstalledApps(appsList)
          } else {
            view.showGeneralOutOfSpaceError()
          }
        }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, { e -> crashReporter.log(e) })
  }

}