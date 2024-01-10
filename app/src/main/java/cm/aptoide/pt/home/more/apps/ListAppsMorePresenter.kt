package cm.aptoide.pt.home.more.apps

import android.content.SharedPreferences
import cm.aptoide.pt.ads.data.AptoideNativeAd
import cm.aptoide.pt.app.AppNavigator
import cm.aptoide.pt.app.view.AppViewFragment
import cm.aptoide.pt.crashreports.CrashReport
import cm.aptoide.pt.dataprovider.ws.v7.V7
import cm.aptoide.pt.home.bundles.apps.EskillsApp
import cm.aptoide.pt.home.more.base.ListAppsClickEvent
import cm.aptoide.pt.home.more.base.ListAppsPresenter
import cm.aptoide.pt.home.more.base.ListAppsView
import cm.aptoide.pt.search.model.SearchAdResult
import cm.aptoide.pt.view.app.Application
import rx.Observable
import rx.Scheduler

class ListAppsMorePresenter(view: ListAppsView<Application>,
                            viewScheduler: Scheduler,
                            crashReporter: CrashReport,
                            private val appNavigator: AppNavigator,
                            private val sharedPreferences: SharedPreferences,
                            private val listAppsConfiguration: ListAppsConfiguration,
                            private val listAppsMoreManager: ListAppsMoreManager) :
    ListAppsPresenter<Application>(view, viewScheduler, crashReporter) {

  private val url by lazy(LazyThreadSafetyMode.NONE) {
    listAppsConfiguration.action?.replace(V7.getHost(sharedPreferences), "")
  }

  fun isEskills(): Boolean {
    return listAppsConfiguration.tag == "skills-more"
  }

  override fun getApps(refresh: Boolean): Observable<List<Application>> {
    return listAppsMoreManager.loadFreshApps(url, refresh, listAppsConfiguration.eventName, listAppsConfiguration.groupId)
  }

  override fun loadMoreApps(): Observable<List<Application>> {
    return listAppsMoreManager.loadMoreApps(url, true, listAppsConfiguration.eventName)
  }

  override fun getTitle(): String {
    return listAppsConfiguration.title ?: ""
  }

  override fun handleAppClick(appClickEvent: ListAppsClickEvent<Application>) {
    if (appClickEvent.application is AptoideNativeAd) {
      appNavigator.navigateWithAd(SearchAdResult(appClickEvent.application),
          listAppsConfiguration.tag)
    } else if (appClickEvent.application is EskillsApp) {
      appNavigator.navigateWithAppIdFromEskills(appClickEvent.application.appId,
          appClickEvent.application.packageName, AppViewFragment.OpenType.OPEN_ONLY,
          listAppsConfiguration.tag)
    } else {
      appNavigator.navigateWithAppId(appClickEvent.application.appId,
          appClickEvent.application.packageName, AppViewFragment.OpenType.OPEN_ONLY,
          listAppsConfiguration.tag)
    }
  }

  override fun handleHeaderClick() {
    appNavigator.navigateToESkillsSectionOfAppCoinsInfoView()
  }
}