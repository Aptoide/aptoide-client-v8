package cm.aptoide.pt.socialMedia

import android.content.Context
import cm.aptoide.aptoideviews.socialmedia.SocialMediaView
import cm.aptoide.pt.R
import cm.aptoide.pt.link.CustomTabsHelper
import cm.aptoide.pt.themes.ThemeManager

class SocialMediaNavigator(val context: Context, val themeManager: ThemeManager) {

  companion object {
    var FACEBOOK_URL = "https://facebook.com/aptoide/"
    var TWITTER_URL = "https://twitter.com/aptoide"
    var INSTAGRAM_URL = "https://www.instagram.com/aptoideteam/"
  }

  fun navigateToSocialMediaWebsite(socialMediaType: SocialMediaView.SocialMediaType) {
    val socialMediaUrl: String = mapSocialMediaTypeToUrl(socialMediaType)
    CustomTabsHelper.getInstance()
        .openInChromeCustomTab(socialMediaUrl, context,
            themeManager.getAttributeForTheme(
                R.attr.colorPrimary).resourceId)
  }

  private fun mapSocialMediaTypeToUrl(socialMediaType: SocialMediaView.SocialMediaType): String {
    return when (socialMediaType) {
      SocialMediaView.SocialMediaType.FACEBOOK_CLICK -> FACEBOOK_URL
      SocialMediaView.SocialMediaType.TWITTER_CLICK -> TWITTER_URL
      SocialMediaView.SocialMediaType.INSTAGRAM_CLICK -> INSTAGRAM_URL
    }
  }
}