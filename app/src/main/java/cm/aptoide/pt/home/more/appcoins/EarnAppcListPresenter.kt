package cm.aptoide.pt.home.more.appcoins

import cm.aptoide.analytics.AnalyticsManager
import cm.aptoide.pt.actions.PermissionManager
import cm.aptoide.pt.actions.PermissionService
import cm.aptoide.pt.app.AppNavigator
import cm.aptoide.pt.crashreports.CrashReport
import cm.aptoide.pt.home.bundles.apps.RewardApp
import cm.aptoide.pt.home.more.base.ListAppsClickEvent
import cm.aptoide.pt.home.more.base.ListAppsPresenter
import cm.aptoide.pt.presenter.View
import cm.aptoide.pt.promotions.WalletApp
import cm.aptoide.pt.repository.request.RewardAppCoinsAppsRepository
import rx.Completable
import rx.Observable
import rx.Scheduler
import rx.schedulers.Schedulers

class EarnAppcListPresenter(private val view: EarnAppcListView,
                            private val viewScheduler: Scheduler,
                            private val crashReporter: CrashReport,
                            private val rewardAppsRepository: RewardAppCoinsAppsRepository,
                            private val analyticsManager: AnalyticsManager,
                            private val appNavigator: AppNavigator,
                            private val earnAppcListConfiguration: EarnAppcListConfiguration,
                            private val earnAppcListManager: EarnAppcListManager,
                            private val permissionManager: PermissionManager,
                            private val permissionService: PermissionService) :
    ListAppsPresenter<RewardApp>(view, viewScheduler, crashReporter) {

  override fun present() {
    super.present()
    loadWalletApp()
    handleWalletInstallClick()
    handleResumeDownload()
    handlePauseDownload()
    handleCancelDownload()
  }

  private fun handleOnWalletInstalled(): Observable<Boolean> {
    return earnAppcListManager.onWalletInstalled()
        .first()
        .observeOn(viewScheduler)
        .doOnNext { view.hideWalletArea() }
  }

  private fun loadWalletApp() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .flatMap { earnAppcListManager.getWalletApp() }
        .first()
        .observeOn(viewScheduler)
        .doOnNext { walletApp -> view.setupWallet(walletApp) }
        .flatMap {
          Observable.mergeDelayError(observeWalletState(), handleOnWalletInstalled())
        }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, { e -> crashReporter.log(e) })
  }

  private fun handleWalletInstallClick() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .observeOn(viewScheduler)
        .flatMap {
          view.onWalletInstallClick()
              .flatMapCompletable { startWalletDownload(earnAppcListManager.downloadApp()) }
              .retry()
        }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, { e -> crashReporter.log(e) })
  }

  private fun startWalletDownload(actionCompletable: Completable): Completable {
    return Observable.defer {
      if (earnAppcListManager.shouldShowRootInstallWarningPopup()) {
        view.showRootInstallWarningPopup()
            .doOnNext { answer -> earnAppcListManager.allowRootInstall(answer) }
      }
      Observable.just(null)
    }.observeOn(viewScheduler)
        .flatMap {
          permissionManager.requestDownloadAllowingMobileData(permissionService)
              .flatMap {
                permissionManager.requestExternalStoragePermission(permissionService)
              }
              .observeOn(Schedulers.io())
              .flatMapCompletable { actionCompletable }
        }.toCompletable()
  }

  private fun handleCancelDownload() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .flatMap {
          view.cancelDownload()
              .flatMapCompletable { earnAppcListManager.cancelWalletDownload() }
              .retry()
        }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, { e -> crashReporter.log(e) })
  }

  private fun handleResumeDownload() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .flatMap {
          view.resumeDownload()
              .flatMapCompletable {
                startWalletDownload(earnAppcListManager.resumeWalletDownload())
              }
              .retry()
        }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, { e -> crashReporter.log(e) })
  }

  private fun handlePauseDownload() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .flatMap {
          view.pauseDownload()
              .flatMapCompletable { earnAppcListManager.pauseWalletDownload() }
              .retry()
        }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, { e -> crashReporter.log(e) })
  }

  private fun observeWalletState(): Observable<WalletApp> {
    return earnAppcListManager.observeWalletApp()
        .observeOn(viewScheduler)
        .doOnNext { walletApp -> view.updateState(walletApp) }
        .doOnError { e -> e.printStackTrace() }
  }

  override fun getApps(refresh: Boolean): Observable<List<RewardApp>> {
    return rewardAppsRepository.getAppCoinsRewardAppsFromHomeMore(refresh, "appcoins-ads")
  }

  override fun getTitle(): String {
    return earnAppcListConfiguration.title ?: ""
  }

  override fun handleAppClick(appClickEvent: ListAppsClickEvent<RewardApp>) {
    val app = appClickEvent.application
    analyticsManager.logEvent(appClickEvent.application.clickUrl)
    appNavigator.navigateWithDownloadUrlAndReward(app.appId, app.packageName,
        earnAppcListConfiguration.tag, app.downloadUrl, app.reward?.appc?.toFloat() ?: -1f)
  }

}