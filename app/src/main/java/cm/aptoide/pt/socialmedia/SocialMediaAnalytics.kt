package cm.aptoide.pt.socialmedia

import cm.aptoide.analytics.AnalyticsManager
import cm.aptoide.analytics.implementation.navigation.NavigationTracker
import cm.aptoide.aptoideviews.socialmedia.SocialMediaView
import java.util.*

open class SocialMediaAnalytics(val analyticsManager: AnalyticsManager,
                                val navigationTracker: NavigationTracker) {

  companion object {
    const val PROMOTE_SOCIAL_MEDIA_EVENT_NAME: String = "promote_social_media_click"
    val FACEBOOK_ACTION: String = "facebook"
    val TWITTER_ACTION: String = "twitter"
    val INSTAGRAM_ACTION: String = "instagram"
    val TIKTOK_ACTION: String = "tiktok"
    val ACTION = "action"
    val CONTEXT = "context"
  }

  fun sendPromoteSocialMediaClickEvent(socialMediaType: SocialMediaView.SocialMediaType) {
    val map = HashMap<String, Any>()
    map[ACTION] = mapSocialMediaTypeToAction(socialMediaType)
    map[CONTEXT] = navigationTracker.getViewName(true)
    analyticsManager.logEvent(map, PROMOTE_SOCIAL_MEDIA_EVENT_NAME,
        AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true))
  }

  private fun mapSocialMediaTypeToAction(socialMediaType: SocialMediaView.SocialMediaType): String {
    return when (socialMediaType) {
      SocialMediaView.SocialMediaType.FACEBOOK_CLICK -> FACEBOOK_ACTION
      SocialMediaView.SocialMediaType.TWITTER_CLICK -> TWITTER_ACTION
      SocialMediaView.SocialMediaType.INSTAGRAM_CLICK -> INSTAGRAM_ACTION
      SocialMediaView.SocialMediaType.TIKTOK_CLICK -> TIKTOK_ACTION
    }
  }

}