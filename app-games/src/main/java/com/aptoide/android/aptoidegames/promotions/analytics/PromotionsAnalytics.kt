package com.aptoide.android.aptoidegames.promotions.analytics

import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.analytics.dto.AnalyticsUIContext

class PromotionsAnalytics(
  private val analyticsUIContext: AnalyticsUIContext,
  private val biAnalytics: BIAnalytics,
  private val genericAnalytics: GenericAnalytics,
) {
  fun sendVanillaPromotionalCardsEvent(
    action: String,
    type: String,
    packageName: String,
  ) {
    biAnalytics.logEvent(
      name = "vanilla_promotional_cards",
      mapOf(
        P_ACTION to action,
        P_TYPE to type,
        P_PACKAGE_NAME to packageName
      )
    )
  }

  companion object {
    private const val P_ACTION = "action"
    private const val P_PACKAGE_NAME = "package_name"
    private const val P_TYPE = "type"
  }
}
