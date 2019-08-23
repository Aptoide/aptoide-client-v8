package cm.aptoide.pt.home.more.appcoins

import cm.aptoide.analytics.AnalyticsManager
import cm.aptoide.aptoideviews.errors.ErrorView
import cm.aptoide.pt.app.AppNavigator
import cm.aptoide.pt.crashreports.CrashReport
import cm.aptoide.pt.dataprovider.util.ErrorUtils
import cm.aptoide.pt.home.bundles.apps.RewardApp
import cm.aptoide.pt.presenter.Presenter
import cm.aptoide.pt.presenter.View
import cm.aptoide.pt.repository.request.RewardAppCoinsAppsRepository
import rx.Observable
import rx.Scheduler

class EarnAppcListPresenter(private val view: EarnAppcListView,
                            private val viewScheduler: Scheduler,
                            private val crashReporter: CrashReport,
                            private val rewardAppsRepository: RewardAppCoinsAppsRepository,
                            private val analyticsManager: AnalyticsManager,
                            private val appNavigator: AppNavigator,
                            private val earnAppcListConfiguration: EarnAppcListConfiguration) : Presenter {

  override fun present() {
    loadAppList()
    setupToolbar()
    handleAppClick()
    handleRetryClick()
    handleRefreshSwipe()
  }

  private fun handleRetryClick() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .observeOn(viewScheduler)
        .flatMap {
          view.errorRetryClick()
              .doOnNext{ view.showLoading() }
              .flatMap { loadApps(true) }
              .retry()
        }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, { e -> crashReporter.log(e) })
  }

  private fun handleRefreshSwipe() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .observeOn(viewScheduler)
        .flatMap {
          view.refreshEvents()
              .flatMap { loadApps(true) }
              .retry()
        }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, { e -> crashReporter.log(e) })
  }

  private fun setupToolbar() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .observeOn(viewScheduler)
        .doOnNext { earnAppcListConfiguration.title?.apply { view.setToolbarInfo(this) } }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, { e -> crashReporter.log(e) })
  }

  private fun loadAppList() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .observeOn(viewScheduler)
        .doOnNext { view.showLoading() }
        .flatMap { loadApps(false) }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, { e -> crashReporter.log(e) })
  }

  private fun loadApps(refresh: Boolean): Observable<List<RewardApp>> {
    return rewardAppsRepository.getAppCoinsRewardAppsFromHomeMore(refresh, "appcoins-ads")
        .observeOn(viewScheduler)
        .doOnNext { apps -> view.showApps(apps) }
        .doOnError { e ->
          if (ErrorUtils.isNoNetworkConnection(e)) {
            view.showNoNetworkError()
          } else {
            view.showGenericError()
          }
        }
  }

  private fun handleAppClick() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .flatMap {
          view.getItemClickEvents().doOnNext { event ->
            val app = event.application
            analyticsManager.logEvent(event.application.clickUrl)
            appNavigator.navigateWithDownloadUrlAndReward(app.appId, app.packageName,
                earnAppcListConfiguration.tag, app.downloadUrl, app.reward?.appc?.toFloat() ?: -1f)
          }.retry()
        }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, { e -> crashReporter.log(e) })
  }
}