package cm.aptoide.pt.abtesting.analytics

import cm.aptoide.analytics.AnalyticsManager
import cm.aptoide.analytics.implementation.navigation.NavigationTracker
import java.util.*

class AppsNameAnalytics(val analyticsManager: AnalyticsManager,
                        val navigationTracker: NavigationTracker) {
  companion object {
    const val MOB_512_APPS_NAME_PARTICIPATING_EVENT = "mob_512_apps_name_participating"
    const val MOB_512_APPS_NAME_CONVERSION_EVENT = "mob_512_apps_name_converting"
  }

  fun sendAbTestParticipatingEvent(group: String) {
    val data = HashMap<String, Any>()
    data["group"] = group
    analyticsManager.logEvent(data, MOB_512_APPS_NAME_PARTICIPATING_EVENT,
        AnalyticsManager.Action.IMPRESSION, navigationTracker.getViewName(true))
  }

  fun sendAbTestConvertingEvent(group: String) {
    val data = HashMap<String, Any>()
    data["group"] = group
    analyticsManager.logEvent(data, MOB_512_APPS_NAME_CONVERSION_EVENT,
        AnalyticsManager.Action.CLICK, navigationTracker.getViewName(true))
  }
}