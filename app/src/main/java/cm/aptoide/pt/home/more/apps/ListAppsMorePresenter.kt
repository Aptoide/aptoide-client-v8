package cm.aptoide.pt.home.more.apps

import android.content.SharedPreferences
import cm.aptoide.pt.app.AppNavigator
import cm.aptoide.pt.app.view.AppViewFragment
import cm.aptoide.pt.crashreports.CrashReport
import cm.aptoide.pt.dataprovider.ws.v7.V7
import cm.aptoide.pt.home.more.base.ListAppsClickEvent
import cm.aptoide.pt.home.more.base.ListAppsPresenter
import cm.aptoide.pt.home.more.base.ListAppsView
import cm.aptoide.pt.view.app.Application
import rx.Observable
import rx.Scheduler

class ListAppsMorePresenter(view: ListAppsView<Application>,
                            viewScheduler: Scheduler,
                            crashReporter: CrashReport,
                            private val appNavigator: AppNavigator,
                            private val sharedPreferences: SharedPreferences,
                            private val listAppsConfiguration: ListAppsConfiguration,
                            private val listAppsMoreRepository: ListAppsMoreRepository) :
    ListAppsPresenter<Application>(view, viewScheduler, crashReporter) {


  override fun getApps(refresh: Boolean): Observable<List<Application>> {
    val url = convertUrl()
    return listAppsMoreRepository.getApps(url, refresh)
  }

  override fun loadMoreApps(): Observable<List<Application>> {
    val url = convertUrl()
    return listAppsMoreRepository.loadMoreApps(url, true)
  }

  override fun getTitle(): String {
    return listAppsConfiguration.title ?: ""
  }

  override fun handleAppClick(appClickEvent: ListAppsClickEvent<Application>) {
    appNavigator.navigateWithAppId(appClickEvent.application.appId,
        appClickEvent.application.packageName, AppViewFragment.OpenType.OPEN_ONLY,
        listAppsConfiguration.tag)
  }

  private fun convertUrl(): String? {
    return if (listAppsConfiguration.action != null)
      listAppsConfiguration.action.replace(V7.getHost(sharedPreferences),
          "")
    else
      null
  }

}