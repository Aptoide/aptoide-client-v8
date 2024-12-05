package com.aptoide.android.aptoidegames.feature_oos.analytics

import cm.aptoide.pt.feature_apps.data.App
import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.analytics.toGenericParameters

class OutOfSpaceAnalytics(
  private val genericAnalytics: GenericAnalytics,
  @Suppress("unused") private val biAnalytics: BIAnalytics,
  @Suppress("unused") private val storeName: String,
) {

  fun sendNotEnoughSpaceDialogShow(app: App) = genericAnalytics.logEvent(
    name = "oos_not_enough_space",
    params = mapOf(
      *app.toGenericParameters(),
    )
  )

  fun sendOOsGoBackButtonClick() = genericAnalytics.logEvent(
    name = "oos_go_back_clicked",
    params = emptyMap()
  )
}
