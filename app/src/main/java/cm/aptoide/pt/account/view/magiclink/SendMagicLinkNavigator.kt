package cm.aptoide.pt.account.view.magiclink

import android.content.Context
import cm.aptoide.pt.R
import cm.aptoide.pt.link.CustomTabsHelper
import cm.aptoide.pt.navigator.FragmentNavigator
import cm.aptoide.pt.themes.ThemeManager

class SendMagicLinkNavigator(private val fragmentNavigator: FragmentNavigator, val context: Context,
                             val themeManager: ThemeManager) {

  fun navigateToCheckYourEmail(email: String) {
    fragmentNavigator.navigateTo(CheckYourEmailFragment.newInstance(email), true)
  }

  fun navigateToBlog() {
    CustomTabsHelper.getInstance()
        .openInChromeCustomTab(
            "https://blog.aptoide.com/aptoide-new-authentication-system-no-user-data-storage/",
            context,
            themeManager.getAttributeForTheme(
                R.attr.colorPrimary).resourceId)
  }
}