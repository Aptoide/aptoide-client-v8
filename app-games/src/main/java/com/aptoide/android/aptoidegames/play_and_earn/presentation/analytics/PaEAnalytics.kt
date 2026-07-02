package com.aptoide.android.aptoidegames.play_and_earn.presentation.analytics

import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics.Companion.P_PACKAGE_NAME
import com.aptoide.android.aptoidegames.analytics.dto.AnalyticsUIContext
import com.aptoide.android.aptoidegames.analytics.mapOfNonNull
import com.aptoide.android.aptoidegames.analytics.toGenericParameters
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.PaERewardType
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

  // --- First sign-in reward funnel (apkfy reward screen + games-feed reward card) ---

  fun sendPaERewardCardShown(source: String, rewardType: PaERewardType) {
    genericAnalytics.logEvent(
      name = "pae_rewardcard_shown",
      params = mapOfNonNull(
        P_SOURCE to source,
        P_REWARD_TYPE to rewardType.analyticsName,
      )
    )
  }

  fun sendPaERewardCardCollectClick(source: String, rewardType: PaERewardType) {
    genericAnalytics.logEvent(
      name = "pae_rewardcard_click_collect",
      params = mapOfNonNull(
        P_SOURCE to source,
        P_REWARD_TYPE to rewardType.analyticsName,
      )
    )
  }

  fun sendPaERewardCardDismissClick(source: String, rewardType: PaERewardType) {
    genericAnalytics.logEvent(
      name = "pae_rewardcard_click_dismiss",
      params = mapOfNonNull(
        P_SOURCE to source,
        P_REWARD_TYPE to rewardType.analyticsName,
      )
    )
  }

  fun sendPaERewardClaimed(source: String, rewardType: PaERewardType, units: Int) {
    genericAnalytics.logEvent(
      name = "pae_reward_claimed",
      params = mapOfNonNull(
        P_SOURCE to source,
        P_REWARD_TYPE to rewardType.analyticsName,
        P_UNITS to units,
      )
    )
  }

  // --- Reward-claimed success dialog ---

  fun sendPaERewardDialogEarnMoreClick() {
    genericAnalytics.logEvent(name = "pae_rewarddialog_click_earnmore", params = null)
  }

  fun sendPaERewardDialogMyBalanceClick() {
    genericAnalytics.logEvent(name = "pae_rewarddialog_click_mybalance", params = null)
  }

  // --- Redeem / exchange flow ---

  fun sendPaERedeemClick() {
    genericAnalytics.logEvent(name = "pae_redeem_click_redeem", params = null)
  }

  fun sendPaERedeemEarnMoreClick() {
    genericAnalytics.logEvent(name = "pae_redeem_click_earnmore", params = null)
  }

  fun sendPaEPickRewardClick(rewardType: PaERewardType) {
    genericAnalytics.logEvent(
      name = "pae_pickreward_click_select",
      params = mapOfNonNull(P_REWARD_TYPE to rewardType.analyticsName)
    )
  }

  fun sendPaEExchangeGetCodeClick(rewardType: PaERewardType) {
    genericAnalytics.logEvent(
      name = "pae_exchangeemail_click_getcode",
      params = mapOfNonNull(P_REWARD_TYPE to rewardType.analyticsName)
    )
  }

  fun sendPaEExchangeResult(success: Boolean, rewardType: PaERewardType) {
    genericAnalytics.logEvent(
      name = "pae_exchange_result",
      params = mapOfNonNull(
        P_STATUS to if (success) STATUS_SUCCESS else STATUS_FAIL,
        P_REWARD_TYPE to rewardType.analyticsName,
      )
    )
  }

  fun sendPaEExchangeSuccessEarnMoreClick() {
    genericAnalytics.logEvent(name = "pae_exchangesuccess_click_earnmore", params = null)
  }

  companion object {
    const val SOURCE_APKFY = "apkfy"
    const val SOURCE_GAMES_FEED = "games_feed"

    private const val STATUS_SUCCESS = "success"
    private const val STATUS_FAIL = "fail"

    private const val P_SOURCE = "source"
    private const val P_REWARD_TYPE = "reward_type"
    private const val P_UNITS = "units"
    private const val P_STATUS = "status"
  }
}

/** Stable lowercase token for the reward currency, used as the `reward_type` event parameter. */
private val PaERewardType.analyticsName: String
  get() = name.lowercase()
