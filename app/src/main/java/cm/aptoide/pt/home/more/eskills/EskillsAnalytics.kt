package cm.aptoide.pt.home.more.eskills

import cm.aptoide.analytics.AnalyticsManager
import cm.aptoide.analytics.implementation.navigation.NavigationTracker
import java.util.HashMap

class EskillsAnalytics(val analyticsManager: AnalyticsManager,
                       val navigationTracker: NavigationTracker
) {

  companion object {
    // event names
    const val ESKILLS_PROMOTIONAL_CARD: String = "eskills_promotional_card"
    const val ESKILLS_APP_CLICK: String = "eskills_app_click"
    const val ESKILLS_PROMOTIONAL_PAGE: String = "eskills_promotional_page"

    // actions
    const val LEARN_MORE_ACTION: String = "learn_more_card_click"
    const val WALLET_DISCLAIMER_ACTION: String = "wallet_disclaimer_click"
    const val SEE_MORE_APPS_ACTION: String = "see_more_btn_click"
    const val HEADER_ACTION: String = "header_click"
    const val MORE_BTN_ACTION: String = "more_btn_click"

    // attributes
    const val ACTION = "action"
    const val CONTEXT = "context"
    const val PACKAGE_NAME = "package_name"
    const val POSITION = "position"
  }

  // info page
  fun sendLearnMoreClickEvent() {
    val data = HashMap<String, Any>()
    data[ACTION] = LEARN_MORE_ACTION
    analyticsManager.logEvent(data, ESKILLS_PROMOTIONAL_PAGE,
      AnalyticsManager.Action.CLICK,
      navigationTracker.getViewName(true))
  }

  fun sendWalletDisclaimerClickEvent() {
    val data = HashMap<String, Any>()
    data[ACTION] = WALLET_DISCLAIMER_ACTION
    analyticsManager.logEvent(data, ESKILLS_PROMOTIONAL_PAGE,
      AnalyticsManager.Action.CLICK,
      navigationTracker.getViewName(true))
  }

  fun sendSeeMoreAppsClickEvent() {
    val data = HashMap<String, Any>()
    data[ACTION] = SEE_MORE_APPS_ACTION
    analyticsManager.logEvent(data, ESKILLS_PROMOTIONAL_PAGE,
      AnalyticsManager.Action.CLICK,
      navigationTracker.getViewName(true))
  }

  // ListApps
  fun sendAppClickEvent(packageName: String, position: Int) {
    val data = HashMap<String, Any>()
    data[CONTEXT] = navigationTracker.getViewName(true)
    data[PACKAGE_NAME] = packageName
    data[POSITION] = position
    analyticsManager.logEvent(data, ESKILLS_APP_CLICK,
      AnalyticsManager.Action.CLICK,
      navigationTracker.getViewName(true))
  }

  // home page bundle
  fun sendHomeBundleHeaderClickEvent() {
    val data = HashMap<String, Any>()
    data[ACTION] = HEADER_ACTION
    analyticsManager.logEvent(data, ESKILLS_PROMOTIONAL_CARD,
      AnalyticsManager.Action.CLICK,
      navigationTracker.getViewName(true))
  }

  fun sendHomeBundleMoreClickEvent() {
    val data = HashMap<String, Any>()
    data[ACTION] = MORE_BTN_ACTION
    analyticsManager.logEvent(data, ESKILLS_PROMOTIONAL_CARD,
      AnalyticsManager.Action.CLICK,
      navigationTracker.getViewName(true))
  }
}