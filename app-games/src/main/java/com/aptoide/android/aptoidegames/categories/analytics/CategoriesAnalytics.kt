package com.aptoide.android.aptoidegames.categories.analytics

import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics.Companion.P_CATEGORY
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics.Companion.P_ITEM_POSITION
import com.aptoide.android.aptoidegames.analytics.dto.AnalyticsUIContext
import com.aptoide.android.aptoidegames.analytics.mapOfNonNull

class CategoriesAnalytics(
  private val genericAnalytics: GenericAnalytics,
) {

  fun sendCategoryClick(
    categoryName: String,
    analyticsContext: AnalyticsUIContext,
  ) = genericAnalytics.logEvent(
    name = "category_clicked",
    params = mapOfNonNull(
      P_CATEGORY to categoryName,
      P_ITEM_POSITION to analyticsContext.itemPosition
    )
  )
}
