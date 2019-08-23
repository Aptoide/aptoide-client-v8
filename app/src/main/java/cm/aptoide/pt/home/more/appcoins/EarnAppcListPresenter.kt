package cm.aptoide.pt.home.more.appcoins

import cm.aptoide.analytics.AnalyticsManager
import cm.aptoide.pt.app.AppNavigator
import cm.aptoide.pt.crashreports.CrashReport
import cm.aptoide.pt.home.bundles.apps.RewardApp
import cm.aptoide.pt.home.more.base.ListAppsClickEvent
import cm.aptoide.pt.home.more.base.ListAppsPresenter
import cm.aptoide.pt.repository.request.RewardAppCoinsAppsRepository
import rx.Observable
import rx.Scheduler

class EarnAppcListPresenter(private val view: EarnAppcListView,
                            private val viewScheduler: Scheduler,
                            private val crashReporter: CrashReport,
                            private val rewardAppsRepository: RewardAppCoinsAppsRepository,
                            private val analyticsManager: AnalyticsManager,
                            private val appNavigator: AppNavigator,
                            private val earnAppcListConfiguration: EarnAppcListConfiguration) :
    ListAppsPresenter<RewardApp>(view, viewScheduler, crashReporter) {

  override fun present() {
    super.present()
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