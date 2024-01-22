package cm.aptoide.pt.home.more.eskills

import android.content.SharedPreferences
import cm.aptoide.pt.app.AppNavigator
import cm.aptoide.pt.app.view.AppViewFragment
import cm.aptoide.pt.crashreports.CrashReport
import cm.aptoide.pt.home.more.apps.ListAppsConfiguration
import cm.aptoide.pt.home.more.apps.ListAppsMoreManager
import cm.aptoide.pt.home.more.apps.ListAppsMorePresenter
import cm.aptoide.pt.home.more.base.ListAppsClickEvent
import cm.aptoide.pt.home.more.base.ListAppsView
import cm.aptoide.pt.view.app.Application
import rx.Scheduler

class ListAppsEskillsPresenter(view: ListAppsView<Application>,
                               viewScheduler: Scheduler,
                               crashReporter: CrashReport,
                               private val appNavigator: AppNavigator,
                               sharedPreferences: SharedPreferences,
                               private val listAppsConfiguration: ListAppsConfiguration,
                               listAppsMoreManager: ListAppsMoreManager,
                               private val eskillsAnalytics: EskillsAnalytics
) :
    ListAppsMorePresenter(
      view, viewScheduler, crashReporter,
      appNavigator, sharedPreferences, listAppsConfiguration,
      listAppsMoreManager) {

  override fun handleAppClick(appClickEvent: ListAppsClickEvent<Application>) {
    appNavigator.navigateWithAppIdFromEskills(appClickEvent.application.appId,
          appClickEvent.application.packageName, AppViewFragment.OpenType.OPEN_ONLY,
          listAppsConfiguration.tag)
    eskillsAnalytics.sendAppsClickEvent(appClickEvent.application.packageName,
           appClickEvent.appPosition)
  }

  override fun handleHeaderClick() {}
}