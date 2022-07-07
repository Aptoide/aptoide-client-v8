package cm.aptoide.pt.editorial.epoxy

import cm.aptoide.analytics.AnalyticsManager
import cm.aptoide.analytics.implementation.navigation.NavigationTracker
import cm.aptoide.pt.editorial.EditorialAnalytics
import java.util.*

class ReactionAnalytics(private val analyticsManager: AnalyticsManager,
                        private val navigationTracker: NavigationTracker) {

  private val WHERE = "where"
  private val ACTION = "action"


  fun sendReactionButtonClickEvent(source: ReactionConfiguration.ReactionSource) {
    val data: MutableMap<String, Any> = HashMap()
    data[ACTION] = "view_reactions"
    data[WHERE] = source.stringName
    analyticsManager.logEvent(data, EditorialAnalytics.REACTION_INTERACT,
        AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true))
  }

  fun sendReactedEvent(source: ReactionConfiguration.ReactionSource) {
    val data: MutableMap<String, Any> = HashMap()
    data[ACTION] = "click_to_react"
    data[WHERE] = source.stringName
    analyticsManager.logEvent(data, EditorialAnalytics.REACTION_INTERACT,
        AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true))
  }

  fun sendDeletedEvent(source: ReactionConfiguration.ReactionSource) {
    val data: MutableMap<String, Any> = HashMap()
    data[ACTION] = "delete_reaction"
    data[WHERE] = source.stringName
    analyticsManager.logEvent(data, EditorialAnalytics.REACTION_INTERACT,
        AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true))
  }
}