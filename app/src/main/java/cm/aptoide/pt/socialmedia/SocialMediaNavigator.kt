package cm.aptoide.pt.socialmedia

import cm.aptoide.aptoideviews.socialmedia.SocialMediaView
import cm.aptoide.pt.navigator.ExternalNavigator

class SocialMediaNavigator(val externalNavigator: ExternalNavigator) {

  companion object {
    var FACEBOOK_URL = "https://facebook.com/aptoide/"
    var TWITTER_URL = "https://twitter.com/aptoide"
    var INSTAGRAM_URL = "https://www.instagram.com/aptoidestore"
    var TIKTOK_URL = "https://www.tiktok.com/@aptoidestore"
  }

  fun navigateToSocialMediaWebsite(socialMediaType: SocialMediaView.SocialMediaType) {
    val socialMediaUrl: String = mapSocialMediaTypeToUrl(socialMediaType)
    externalNavigator.navigateToExternalWebsite(socialMediaUrl)
  }

  private fun mapSocialMediaTypeToUrl(socialMediaType: SocialMediaView.SocialMediaType): String {
    return when (socialMediaType) {
      SocialMediaView.SocialMediaType.FACEBOOK_CLICK -> FACEBOOK_URL
      SocialMediaView.SocialMediaType.TWITTER_CLICK -> TWITTER_URL
      SocialMediaView.SocialMediaType.INSTAGRAM_CLICK -> INSTAGRAM_URL
      SocialMediaView.SocialMediaType.TIKTOK_CLICK -> TIKTOK_URL
    }
  }
}