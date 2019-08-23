package cm.aptoide.pt.home.more.appcoins

import cm.aptoide.analytics.AnalyticsManager
import cm.aptoide.pt.app.AppNavigator
import cm.aptoide.pt.crashreports.CrashReport
import cm.aptoide.pt.home.bundles.apps.RewardApp
import cm.aptoide.pt.home.more.ListAppsConfiguration
import cm.aptoide.pt.presenter.Presenter
import cm.aptoide.pt.presenter.View
import cm.aptoide.pt.repository.request.RewardAppCoinsAppsRepository
import rx.Observable
import rx.Scheduler

class EarnAppCoinsListPresenter(private val view: EarnAppCoinsListAppsView,
                                private val viewScheduler: Scheduler,
                                private val crashReporter: CrashReport,
                                private val rewardAppsRepository: RewardAppCoinsAppsRepository,
                                private val analyticsManager: AnalyticsManager,
                                private val appNavigator: AppNavigator,
                                private val listAppsConfiguration: ListAppsConfiguration) :
    Presenter {


  override fun present() {
    loadAppList()
    setupToolbar()
    handleAppClick()
  }

  private fun setupToolbar() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .observeOn(viewScheduler)
        .doOnNext { listAppsConfiguration.title?.apply { view.setToolbarInfo(this) } }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, { e -> crashReporter.log(e) })
  }

  private fun loadAppList() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .observeOn(viewScheduler)
        .doOnNext { view.showLoading() }
        .flatMap { loadApps() }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, { e -> crashReporter.log(e) })
  }

  private fun loadApps(): Observable<List<RewardApp>> {
    return rewardAppsRepository.getAppCoinsRewardAppsFromHomeMore(false, "appcoins-ads")
        .observeOn(viewScheduler)
        .doOnNext { apps -> view.showApps(apps) }
  }

  private fun handleAppClick() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .flatMap { view.appClicked() }
        .doOnNext { event ->
          val app = event.application
          analyticsManager.logEvent(event.application.clickUrl)
          appNavigator.navigateWithDownloadUrlAndReward(app.appId, app.packageName,
              listAppsConfiguration.tag, app.downloadUrl, app.reward?.appc?.toFloat() ?: -1f)
        }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, { e -> crashReporter.log(e) })
  }
}