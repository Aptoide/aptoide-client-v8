package cm.aptoide.pt.abtesting.analytics

import cm.aptoide.analytics.AnalyticsManager
import cm.aptoide.analytics.implementation.navigation.NavigationTracker
import cm.aptoide.pt.notification.NotificationAnalytics
import java.util.*

class UpdateNotificationAnalytics(val analyticsManager: AnalyticsManager,
                                  val navigationTracker: NavigationTracker,
                                  val notificationAnalytics: NotificationAnalytics) {

  companion object {
    const val MOB_657_UPDATES_NOTIFICATION_PARTICIPATING_EVENT =
        "mob_657_updates_notification_participating"
    const val MOB_657_UPDATES_NOTIFICATION_CONVERSION_EVENT =
        "mob_657_updates_notification_converting"
  }

  fun sendUpdatesNotificationReceivedEvent(){
    notificationAnalytics.sendUpdatesNotificationReceivedEvent()
  }

  fun sendUpdatesNotificationImpressionEvent(group: String){
    notificationAnalytics.sendUpdatesNotificationImpressionEvent()
    sendAbTestParticipatingEvent(group)
  }

  fun sendUpdatesNotificationClickEvent(group: String){
    notificationAnalytics.sendUpdatesNotificationClickEvent()
    sendAbTestConvertingEvent(group)
  }

  private fun sendAbTestParticipatingEvent(group: String) {
    val data = HashMap<String, Any>()
    data["group"] = group
    analyticsManager.logEvent(data, MOB_657_UPDATES_NOTIFICATION_PARTICIPATING_EVENT,
        AnalyticsManager.Action.IMPRESSION, navigationTracker.getViewName(true))
  }

  private fun sendAbTestConvertingEvent(group: String) {
    val data = HashMap<String, Any>()
    data["group"] = group
    analyticsManager.logEvent(data, MOB_657_UPDATES_NOTIFICATION_CONVERSION_EVENT,
        AnalyticsManager.Action.CLICK, navigationTracker.getViewName(true))
  }

}