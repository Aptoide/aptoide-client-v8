package cm.aptoide.pt.socialMedia

import cm.aptoide.analytics.AnalyticsManager
import cm.aptoide.analytics.implementation.navigation.NavigationTracker
import cm.aptoide.aptoideviews.socialmedia.SocialMediaView
import java.util.*

open class SocialMediaAnalytics(val analyticsManager: AnalyticsManager,
                                val navigationTracker: NavigationTracker) {

  companion object {
    @JvmField
    var PROMOTE_SOCIAL_MEDIA_EVENT_NAME: String = "promote_social_media_click"
    var FACEBOOK_ACTION: String = "facebook"
    var TWITTER_ACTION: String = "twitter"
    var INSTAGRAM_ACTION: String = "instagram"
    var ACTION = "action"
  }

  fun sendPromoteSocialMediaClickEvent(socialMediaType: SocialMediaView.SocialMediaType) {
    val map = HashMap<String, Any>()
    map[ACTION] = mapSocialMediaTypeToAction(socialMediaType)
    analyticsManager.logEvent(map, PROMOTE_SOCIAL_MEDIA_EVENT_NAME,
        AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true))
  }

  private fun mapSocialMediaTypeToAction(socialMediaType: SocialMediaView.SocialMediaType): String {
    return when (socialMediaType) {
      SocialMediaView.SocialMediaType.FACEBOOK_CLICK -> FACEBOOK_ACTION
      SocialMediaView.SocialMediaType.TWITTER_CLICK -> TWITTER_ACTION
      SocialMediaView.SocialMediaType.INSTAGRAM_CLICK -> INSTAGRAM_ACTION
    }
  }

}