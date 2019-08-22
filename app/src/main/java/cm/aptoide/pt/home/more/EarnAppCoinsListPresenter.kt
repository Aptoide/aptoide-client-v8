package cm.aptoide.pt.home.more

import cm.aptoide.pt.crashreports.CrashReport
import cm.aptoide.pt.home.bundles.apps.RewardApp
import cm.aptoide.pt.presenter.Presenter
import cm.aptoide.pt.presenter.View
import cm.aptoide.pt.repository.request.RewardAppCoinsAppsRepository
import rx.Observable
import rx.Scheduler

class EarnAppCoinsListPresenter(private val view: EarnAppCoinsListAppsView,
                                private val viewScheduler: Scheduler,
                                private val crashReporter: CrashReport,
                                private val rewardAppsRepository: RewardAppCoinsAppsRepository) : Presenter {


  override fun present() {
    loadAppList()
  }

  private fun loadAppList() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .observeOn(viewScheduler)
        .doOnNext { view.showLoading() }
        .flatMap { loadApps() }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({},{e -> crashReporter.log(e)})
  }

  private fun loadApps(): Observable<List<RewardApp>> {
    return rewardAppsRepository.getAppCoinsRewardAppsFromHomeMore(false, "appcoins-ads")
        .observeOn(viewScheduler)
        .doOnNext { apps -> view.showApps(apps) }
  }
}