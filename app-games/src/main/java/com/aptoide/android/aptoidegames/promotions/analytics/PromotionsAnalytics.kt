package com.aptoide.android.aptoidegames.promotions.analytics

import com.aptoide.android.aptoidegames.analytics.BIAnalytics

class PromotionsAnalytics(
  private val biAnalytics: BIAnalytics
) {

  fun sendAhabV2DialogImpression(packageName: String) =
    sendBIAhabV2DialogEvent("impression", packageName)

  fun sendAhabV2DialogUpdate(packageName: String) =
    sendBIAhabV2DialogEvent("update", packageName)

  fun sendAhabV2DialogLater(packageName: String) =
    sendBIAhabV2DialogEvent("later", packageName)

  private fun sendBIAhabV2DialogEvent(
    action: String,
    packageName: String,
  ) {
    biAnalytics.logEvent(
      name = "ag_ahab_v2_dialog",
      mapOf(
        P_ACTION to action,
        P_PACKAGE_NAME to packageName
      )
    )
  }

  companion object {
    private const val P_ACTION = "action"
    private const val P_PACKAGE_NAME = "package_name"
  }
}
