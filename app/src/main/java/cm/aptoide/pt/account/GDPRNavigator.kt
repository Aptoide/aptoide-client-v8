package cm.aptoide.pt.account

import cm.aptoide.pt.R
import cm.aptoide.pt.link.CustomTabsHelper
import cm.aptoide.pt.navigator.ActivityNavigator
import cm.aptoide.pt.themes.ThemeManager

open class GDPRNavigator(
  private val activityNavigator: ActivityNavigator,
  private val themeManager: ThemeManager
) {

  fun navigateToTermsAndConditions() {
    CustomTabsHelper.getInstance()
      .openInChromeCustomTab(
        activityNavigator.getActivity()
          .getString(R.string.all_url_terms_conditions), activityNavigator.getActivity(),
        themeManager.getAttributeForTheme(R.attr.colorPrimary).resourceId
      )
  }

  fun navigateToPrivacyPolicy() {
    CustomTabsHelper.getInstance()
      .openInChromeCustomTab(
        activityNavigator.getActivity()
          .getString(R.string.all_url_privacy_policy), activityNavigator.getActivity(),
        themeManager.getAttributeForTheme(R.attr.colorPrimary).resourceId
      )
  }
}