package cm.aptoide.pt.home.more.eskills

import cm.aptoide.analytics.AnalyticsManager
import cm.aptoide.analytics.implementation.navigation.NavigationTracker
import java.util.HashMap

class EskillsAnalytics(val analyticsManager: AnalyticsManager,
                       val navigationTracker: NavigationTracker
) {

  companion object {
    const val ESKILLS_EVENT_NAME: String = "eskills_event"
    const val LEARN_MORE_ACTION: String = "eskills_info_learn_more"
    const val WALLET_DISCLAIMER_ACTION: String = "eskills_info_wallet_disclaimer"
    const val SEE_MORE_APPS_ACTION: String = "eskills_info_see_more_apps"
    const val APPS_ACTION: String = "eskills_info_apps"
    const val ACTION = "action"
    const val CONTEXT = "context"
    const val PACKAGE_NAME = "package_name"
    const val POSITION = "position"
    const val STATUS = "status"
    const val STARTED = "started"
    const val SUCCESS = "success"
    const val CANCELLED = "cancelled"
    const val INSTALL_WALLET_ACTION= "eskills_companion_wallet_install"
  }

  // info page
  fun sendLearnMoreClickEvent() {
    val map = HashMap<String, Any>()
    map[ACTION] = LEARN_MORE_ACTION
    analyticsManager.logEvent(map, ESKILLS_EVENT_NAME,
      AnalyticsManager.Action.CLICK,
      navigationTracker.getViewName(true))
  }

  fun sendWalletDisclaimerClickEvent() {
    val map = HashMap<String, Any>()
    map[ACTION] = WALLET_DISCLAIMER_ACTION
    analyticsManager.logEvent(map, ESKILLS_EVENT_NAME,
      AnalyticsManager.Action.CLICK,
      navigationTracker.getViewName(true))
  }

  fun sendSeeMoreAppsClickEvent() {
    val map = HashMap<String, Any>()
    map[ACTION] = SEE_MORE_APPS_ACTION
    analyticsManager.logEvent(map, ESKILLS_EVENT_NAME,
      AnalyticsManager.Action.CLICK,
      navigationTracker.getViewName(true))
  }

  fun sendAppsClickEvent(packageName: String, position: Int) {
    val app = HashMap<String, Any>()
    app[PACKAGE_NAME] = packageName
    app[POSITION] = position
    val map = HashMap<String, Any>()
    map[ACTION] = APPS_ACTION
    map[CONTEXT] = app
    analyticsManager.logEvent(map, ESKILLS_EVENT_NAME,
      AnalyticsManager.Action.CLICK,
      navigationTracker.getViewName(true))
  }


  // AppView
  fun sendWalletSuccessfullyInstalledEvent() {
    val map = HashMap<String, Any>()
    map[ACTION] = INSTALL_WALLET_ACTION
    map[STATUS] = SUCCESS
    analyticsManager.logEvent(map, ESKILLS_EVENT_NAME,
      AnalyticsManager.Action.INSTALL,
      navigationTracker.getViewName(true))
  }

  fun sendWalletDownloadStartedEvent() {
    val map = HashMap<String, Any>()
    map[ACTION] = INSTALL_WALLET_ACTION
    map[STATUS] = STARTED
    analyticsManager.logEvent(map, ESKILLS_EVENT_NAME,
      AnalyticsManager.Action.INSTALL,
      navigationTracker.getViewName(true))
  }

  fun sendWalletDownloadCanceledEvent() {
    val map = HashMap<String, Any>()
    map[ACTION] = INSTALL_WALLET_ACTION
    map[STATUS] = CANCELLED
    analyticsManager.logEvent(map, ESKILLS_EVENT_NAME,
      AnalyticsManager.Action.INSTALL,
      navigationTracker.getViewName(true))
  }
}