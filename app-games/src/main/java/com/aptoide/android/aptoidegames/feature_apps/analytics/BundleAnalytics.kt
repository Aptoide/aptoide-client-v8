package com.aptoide.android.aptoidegames.feature_apps.analytics

import cm.aptoide.pt.feature_apps.data.App
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics.Companion.P_SCROLL_COUNT
import com.aptoide.android.aptoidegames.analytics.dto.AnalyticsUIContext
import com.aptoide.android.aptoidegames.analytics.toGenericParameters

class BundleAnalytics(
  private val genericAnalytics: GenericAnalytics,
) {

  fun sendAppPromoClick(
    app: App,
    analyticsContext: AnalyticsUIContext,
  ) = genericAnalytics.logEvent(
    name = "app_promo_clicked",
    params = analyticsContext.toGenericParameters(
      *app.toGenericParameters()
    )
  )

  fun sendSeeAllClick(analyticsContext: AnalyticsUIContext) = genericAnalytics.logEvent(
    name = "see_all_clicked",
    params = analyticsContext.bundleMeta.toGenericParameters()
  )

  fun sendCarouselSwipe(
    count: Int,
    analyticsContext: AnalyticsUIContext,
  ) = genericAnalytics.logEvent(
    name = "carousel_swipe",
    params = analyticsContext.bundleMeta.toGenericParameters(P_SCROLL_COUNT to count)
  )
}
