package com.aptoide.android.aptoidegames.home.analytics

import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics.Companion.P_NAME
import com.aptoide.android.aptoidegames.analytics.UserProperty

class HomeAnalytics(
  private val genericAnalytics: GenericAnalytics,
  private val biAnalytics: BIAnalytics,
) {

  fun setECVideoFlagProperty(variant: String) =
    biAnalytics.setUserProperties(UserProperty("ab_test_ec_videos_jan_29", variant))

  fun sendECVideosFlagReady() =
    genericAnalytics.logEvent("ec_videos_flag_ready", params = emptyMap())

  fun sendHomeTabClick(tab: String) =
    genericAnalytics.logEvent("home_tab_clicked", params = mapOf(P_NAME to tab))
}
