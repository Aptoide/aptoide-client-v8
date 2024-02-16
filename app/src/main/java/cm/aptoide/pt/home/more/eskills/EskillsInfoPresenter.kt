package cm.aptoide.pt.home.more.eskills

import android.content.SharedPreferences
import cm.aptoide.pt.app.AppNavigator
import cm.aptoide.pt.app.view.AppViewFragment
import cm.aptoide.pt.crashreports.CrashReport
import cm.aptoide.pt.dataprovider.ws.v7.V7
import cm.aptoide.pt.home.more.apps.ListAppsConfiguration
import cm.aptoide.pt.home.more.apps.ListAppsMoreManager
import cm.aptoide.pt.home.more.base.ListAppsClickEvent
import cm.aptoide.pt.home.more.base.ListAppsPresenter
import cm.aptoide.pt.presenter.View
import cm.aptoide.pt.view.EskillsInfoNavigator
import cm.aptoide.pt.view.app.Application
import rx.Observable
import rx.Scheduler

class EskillsInfoPresenter(
  private val view: EskillsInfoView,
  private val viewScheduler: Scheduler,
  private val crashReporter: CrashReport,
  private val appNavigator: AppNavigator,
  private val eskillsInfoNavigator: EskillsInfoNavigator,
  private val eskillsAnalytics: EskillsAnalytics,
  private val sharedPreferences: SharedPreferences,
  private val listAppsConfiguration: ListAppsConfiguration,
  private val listAppsMoreManager: ListAppsMoreManager
) :
  ListAppsPresenter<Application>(view, viewScheduler, crashReporter) {

  private val url by lazy(LazyThreadSafetyMode.NONE) {
    listAppsConfiguration.action?.replace(V7.getHost(sharedPreferences), "")
  }

  override fun present() {
    super.present()
    handleLearnMoreClick()
    handleWalletDisclaimerClick()
    handleMoreAppsClick()
  }

  private fun handleLearnMoreClick() {
    view.lifecycleEvent
      .filter { it == View.LifecycleEvent.CREATE }
      .flatMap { view.handleLearnMoreClick() }
      .observeOn(viewScheduler)
      .doOnNext {
        view.scrollToInfo()
        eskillsAnalytics.sendLearnMoreClickEvent()
      }
      .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
      .subscribe({}, { crashReporter.log(it) })
  }


  private fun handleWalletDisclaimerClick() {
    view.lifecycleEvent
      .filter { it == View.LifecycleEvent.CREATE }
      .flatMap { view.handleWalletDisclaimerClick() }
      .observeOn(viewScheduler)
      .doOnNext {
        eskillsInfoNavigator.navigateToAppCoinsWallet()
        eskillsAnalytics.sendWalletDisclaimerClickEvent()
      }
      .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
      .subscribe({}, { crashReporter.log(it) })
  }

  private fun handleMoreAppsClick() {
    view.lifecycleEvent
      .filter { it == View.LifecycleEvent.CREATE }
      .flatMap { view.handleMoreAppsClick() }
      .observeOn(viewScheduler)
      .doOnNext {
        eskillsInfoNavigator.navigateToESkillsMoreGames(
          listAppsConfiguration.title!!, listAppsConfiguration.tag, listAppsConfiguration.action,
          listAppsConfiguration.eventName!!
        )
        eskillsAnalytics.sendSeeMoreAppsClickEvent()
      }
      .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
      .subscribe({}, { crashReporter.log(it) })
  }


  override fun getApps(refresh: Boolean): Observable<List<Application>> {
    return listAppsMoreManager.loadFreshApps(
      url,
      refresh,
      listAppsConfiguration.eventName,
      listAppsConfiguration.groupId,
      9
    )
  }

  override fun getTitle(): String {
    return listAppsConfiguration.title ?: ""
  }

  override fun handleAppClick(appClickEvent: ListAppsClickEvent<Application>) {
    appNavigator.navigateWithAppIdFromEskills(
      appClickEvent.application.appId,
      appClickEvent.application.packageName, AppViewFragment.OpenType.OPEN_ONLY,
      listAppsConfiguration.tag
    )
    eskillsAnalytics.sendAppClickEvent(
      appClickEvent.application.packageName,
      appClickEvent.appPosition
    )
  }

  override fun handleHeaderClick() {}
  override fun handleBundleHeaderClick() {}

}
