package com.aptoide.android.aptoidegames.promo_codes.analytics

import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import com.aptoide.android.aptoidegames.analytics.dto.AnalyticsUIContext
import com.aptoide.android.aptoidegames.analytics.toBiParameters

class PromoCodeAnalytics(
  private val biAnalytics: BIAnalytics,
) {

  fun sendPromoCodeImpressionEvent(
    analyticsContext: AnalyticsUIContext,
    status: String,
    withWallet: Boolean? = null
  ) = sendPromoCodeEvent(
    analyticsContext = analyticsContext,
    action = "impression",
    status = status,
    withWallet = withWallet
  )

  fun sendPromoCodeClickEvent(
    analyticsContext: AnalyticsUIContext,
    withWallet: Boolean? = null
  ) = sendPromoCodeEvent(
    analyticsContext = analyticsContext,
    action = "click",
    withWallet = withWallet
  )

  private fun sendPromoCodeEvent(
    analyticsContext: AnalyticsUIContext,
    action: String,
    status: String? = null,
    withWallet: Boolean? = null
  ) {
    biAnalytics.logEvent(
      name = "ag_promo_codes",
      analyticsContext.toBiParameters(
        P_ACTION to action,
        P_TYPE to when (withWallet) {
          true -> "with_wallet_app"
          false -> "with_wallet_app"
          null -> "n-a"
        },
        P_STATUS to status
      )
    )
  }

  companion object {
    private const val P_ACTION = "action"
    private const val P_TYPE = "type"
    private const val P_STATUS = "status"
  }
}
