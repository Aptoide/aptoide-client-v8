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
      name = "playandearn_home_click_app",
      params = mapOfNonNull(
        P_PACKAGE_NAME to packageName,
      )
    )
  }

  fun sendPaEHomeEarnNowClick() {
    genericAnalytics.logEvent(
      name = "playandearn_home_click_earnnow",
      params = null
    )
  }

  fun sendPaEActionBarBadgeClick() {
    genericAnalytics.logEvent(
      name = "playandearn_actionbar_click_badge",
      params = null
    )
  }

  fun sendPaERewardsHomeTabClick() {
    genericAnalytics.logEvent(
      name = "playandearn_rewards_click_tab",
      params = null
    )
  }

  fun sendPaERewardsHubKnowMoreClick() {
    genericAnalytics.logEvent(
      name = "playandearn_hub_click_knowmore",
      params = null
    )
  }

  fun sendPaERewardsHubLetsGoClick() {
    genericAnalytics.logEvent(
      name = "playandearn_hub_click_letsgo",
      params = null
    )
  }

  fun sendPaEPlayClick(
    packageName: String,
    analyticsContext: AnalyticsUIContext,
  ) {
    genericAnalytics.logEvent(
      name = "playandearn_play",
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
      name = "playandearn_home_applaunched",
      params = analyticsContext.toGenericParameters(
        P_PACKAGE_NAME to packageName,
      )
    )
  }

  fun sendPaEGoogleLoginClick() {
    genericAnalytics.logEvent(
      name = "playandearn_steps_glogin",
      params = null
    )
  }

  fun sendPaEPermissionRestrictedSettingsClick() {
    genericAnalytics.logEvent(
      name = "playandearn_steps_permission_restricted_settings_click",
      params = null
    )
  }

  fun sendPaEFinalPermissionsClick() {
    genericAnalytics.logEvent(
      name = "playandearn_steps_final_permissions_click",
      params = null
    )
  }

  fun sendPaEExchangeNowClick() {
    genericAnalytics.logEvent(
      name = "playandearn_exchangenow_click",
      params = null
    )
  }
}
