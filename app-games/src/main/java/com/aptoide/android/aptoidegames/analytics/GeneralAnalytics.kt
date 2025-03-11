package com.aptoide.android.aptoidegames.analytics

import com.aptoide.android.aptoidegames.analytics.GenericAnalytics.Companion.P_FIRST_LAUNCH
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics.Companion.P_OPEN_TYPE
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics.Companion.P_SERVICE
import com.aptoide.android.aptoidegames.analytics.dto.AnalyticsUIContext
import javax.inject.Inject

class GeneralAnalytics @Inject constructor(
  private val genericAnalytics: GenericAnalytics,
) {

  fun sendNoNetworkRetry() = genericAnalytics.logEvent(
    name = "retry_no_connection_clicked",
    params = emptyMap()
  )

  fun sendOpenAppEvent(
    appOpenSource: String,
    isFirstLaunch: Boolean,
    networkType: String,
  ) = genericAnalytics.logEvent(
    name = "app_open",
    params = mapOf(
      P_OPEN_TYPE to appOpenSource,
      P_FIRST_LAUNCH to if (isFirstLaunch) "true" else "false",
      P_SERVICE to networkType
    )
  )

  fun sendEngagedUserEvent() = genericAnalytics.logEvent(
    name = "engaged_user",
    params = emptyMap()
  )

  fun sendBackButtonClick(analyticsContext: AnalyticsUIContext) = genericAnalytics.logEvent(
    name = "back_button_clicked",
    params = analyticsContext.toGenericParameters()
  )

  fun sendMenuClick(link: String) = genericAnalytics.logEvent(
    name = "menu_clicked",
    params = mapOf("link" to link)
  )

  fun sendBottomBarHomeClick() = genericAnalytics.logEvent(
    name = "bn_home_clicked",
    params = emptyMap()
  )

  fun sendBottomBarSearchClick() = genericAnalytics.logEvent(
    name = "bn_search_clicked",
    params = emptyMap()
  )

  fun sendBottomBarGameGenieClick() = genericAnalytics.logEvent(
    name = "bn_gamegenie_clicked",
    params = emptyMap()
  )

  fun sendBottomBarCategoriesClick() = genericAnalytics.logEvent(
    name = "bn_categories_clicked",
    params = emptyMap()
  )

  fun sendBottomBarUpdatesClick() = genericAnalytics.logEvent(
    name = "bn_updates_clicked",
    params = emptyMap()
  )
}
