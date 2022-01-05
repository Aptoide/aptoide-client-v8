package cm.aptoide.pt.navigator

import android.content.Context
import cm.aptoide.pt.R
import cm.aptoide.pt.link.CustomTabsHelper
import cm.aptoide.pt.themes.ThemeManager

open class ExternalNavigator(val context: Context, val themeManager: ThemeManager) {

  fun navigateToExternalWebsite(url: String) {
    CustomTabsHelper.getInstance()
        .openInChromeCustomTab(url, context,
            themeManager.getAttributeForTheme(
                R.attr.colorPrimary).resourceId)
  }
}