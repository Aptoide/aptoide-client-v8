package cm.aptoide.pt.app.aptoideinstall

import cm.aptoide.analytics.AnalyticsManager
import cm.aptoide.analytics.implementation.navigation.NavigationTracker
import java.util.*

class AptoideInstallAnalytics(val analyticsManager: AnalyticsManager,
                              val navigationTracker: NavigationTracker) {

  companion object {
    const val PARTICIPATING_EVENT = "mob_238_apps_participating"
    const val CONVERSION_EVENT = "mob_238_apps_converting"
  }

  fun sendAbTestParticipatingEvent(group: String) {
    val data = HashMap<String, Any>()
    data.put("group", group)
    analyticsManager.logEvent(data, PARTICIPATING_EVENT,
        AnalyticsManager.Action.IMPRESSION, navigationTracker.getViewName(true))
  }

  fun sendAbTestConvertingEvent(group: String) {
    val data = HashMap<String, Any>()
    data.put("group", group)
    analyticsManager.logEvent(data, CONVERSION_EVENT,
        AnalyticsManager.Action.CLICK, navigationTracker.getViewName(true))
  }
}