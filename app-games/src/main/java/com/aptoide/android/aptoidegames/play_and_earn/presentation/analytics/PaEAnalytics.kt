package com.aptoide.android.aptoidegames.play_and_earn.presentation.analytics

import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics.Companion.P_PACKAGE_NAME
import com.aptoide.android.aptoidegames.analytics.dto.AnalyticsUIContext
import com.aptoide.android.aptoidegames.analytics.mapOfNonNull
import com.aptoide.android.aptoidegames.analytics.toGenericParameters
import javax.inject.Inject

class PaEAnalytics @Inject constructor(
  private val genericAnalytics: GenericAnalytics,
) {

  fun sendPaEHomeAppClick(packageName: String) {
    genericAnalytics.logEvent(
      name = "pae_home_click_app",
      params = mapOfNonNull(
        P_PACKAGE_NAME to packageName,
      )
    )
  }

  fun sendPaEHomeEarnNowClick() {
    genericAnalytics.logEvent(
      name = "pae_home_click_earnnow",
      params = null
    )
  }

  fun sendPaEActionBarBadgeClick() {
    genericAnalytics.logEvent(
      name = "pae_actionbar_click_badge",
      params = null
    )
  }

  fun sendPaERewardsHomeTabClick() {
    genericAnalytics.logEvent(
      name = "pae_rewards_click_tab",
      params = null
    )
  }

  fun sendPaERewardsHubKnowMoreClick() {
    genericAnalytics.logEvent(
      name = "pae_hub_click_knowmore",
      params = null
    )
  }

  fun sendPaERewardsHubLetsGoClick() {
    genericAnalytics.logEvent(
      name = "pae_hub_click_letsgo",
      params = null
    )
  }

  fun sendPaEPlayClick(
    packageName: String,
    analyticsContext: AnalyticsUIContext,
  ) {
    genericAnalytics.logEvent(
      name = "pae_play",
      params = analyticsContext.toGenericParameters(
        P_PACKAGE_NAME to packageName,
      )
    )
  }

  fun sendPaEAppLaunched(
    packageName: String,
    analyticsContext: AnalyticsUIContext,
  ) {
    genericAnalytics.logEvent(
      name = "pae_home_applaunched",
      params = analyticsContext.toGenericParameters(
        P_PACKAGE_NAME to packageName,
      )
    )
  }

  fun sendPaEGoogleLoginClick() {
    genericAnalytics.logEvent(
      name = "pae_steps_glogin",
      params = null
    )
  }

  fun sendPaEPermissionRestrictedSettingsClick() {
    genericAnalytics.logEvent(
      name = "pae_perms_restricted_click",
      params = null
    )
  }

  fun sendPaEFinalPermissionsClick() {
    genericAnalytics.logEvent(
      name = "pae_perms_final_click",
      params = null
    )
  }

  fun sendPaEExchangeNowClick() {
    genericAnalytics.logEvent(
      name = "pae_exchangenow_click",
      params = null
    )
  }
}
