package com.aptoide.android.aptoidegames.promo_codes.analytics

import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import com.aptoide.android.aptoidegames.analytics.mapOfNonNull

class PromoCodeAnalytics(
  private val biAnalytics: BIAnalytics,
) {

  fun sendPromoCodeImpressionEvent(
    status: String,
    isWalletInstalled: Boolean,
    isPromoCodeAppInstalled: Boolean
  ) = sendPromoCodeEvent(
    action = "impression",
    status = status,
    isWalletInstalled = isWalletInstalled,
    isPromoCodeAppInstalled = isPromoCodeAppInstalled
  )

  fun sendPromoCodeClickEvent(
    isWalletInstalled: Boolean,
    isPromoCodeAppInstalled: Boolean
  ) = sendPromoCodeEvent(
    action = "click",
    isWalletInstalled = isWalletInstalled,
    isPromoCodeAppInstalled = isPromoCodeAppInstalled
  )

  private fun sendPromoCodeEvent(
    action: String,
    status: String? = null,
    isWalletInstalled: Boolean,
    isPromoCodeAppInstalled: Boolean
  ) {
    biAnalytics.logEvent(
      name = "ag_promo_codes",
      mapOfNonNull(
        P_ACTION to action,
        P_WALLET_IS_INSTALLED to isWalletInstalled,
        P_APP_IS_INSTALLED to isPromoCodeAppInstalled,
        P_STATUS to status
      )
    )
  }

  companion object {
    private const val P_ACTION = "action"
    private const val P_WALLET_IS_INSTALLED = "wallet_is_installed"
    private const val P_APP_IS_INSTALLED = "app_is_installed"
    private const val P_STATUS = "status"
  }
}
