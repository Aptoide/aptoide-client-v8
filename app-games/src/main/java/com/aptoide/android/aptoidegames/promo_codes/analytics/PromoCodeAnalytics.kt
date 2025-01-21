package com.aptoide.android.aptoidegames.promo_codes.analytics

import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import com.aptoide.android.aptoidegames.analytics.mapOfNonNull

class PromoCodeAnalytics(
  private val biAnalytics: BIAnalytics,
) {

  fun sendPromoCodeImpressionEvent(
    status: String,
    withWallet: Boolean? = null
  ) = sendPromoCodeEvent(
    action = "impression",
    status = status,
    withWallet = withWallet
  )

  fun sendPromoCodeClickEvent(
    withWallet: Boolean? = null
  ) = sendPromoCodeEvent(
    action = "click",
    withWallet = withWallet
  )

  private fun sendPromoCodeEvent(
    action: String,
    status: String? = null,
    withWallet: Boolean? = null
  ) {
    biAnalytics.logEvent(
      name = "ag_promo_codes",
      mapOfNonNull(
        P_ACTION to action,
        P_TYPE to when (withWallet) {
          true -> "with_wallet_app"
          false -> "without_wallet_app"
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
