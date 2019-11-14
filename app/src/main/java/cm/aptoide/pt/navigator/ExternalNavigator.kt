package cm.aptoide.pt.navigator

import android.content.Context
import cm.aptoide.pt.link.CustomTabsHelper

class ExternalNavigator(val context: Context, val theme: String) {

  fun navigateToCatappultWebsite() {
    CustomTabsHelper.getInstance()
        .openInChromeCustomTab("https://catappult.io/", context, theme)
  }
}