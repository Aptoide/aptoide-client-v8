package com.aptoide.android.aptoidegames.search.analytics

import cm.aptoide.pt.feature_apps.data.App
import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.analytics.dto.AnalyticsUIContext
import com.aptoide.android.aptoidegames.analytics.dto.SearchMeta
import com.aptoide.android.aptoidegames.analytics.toBIParameters

class SearchAnalytics(
  private val analyticsUIContext: AnalyticsUIContext,
  private val biAnalytics: BIAnalytics,
  private val genericAnalytics: GenericAnalytics,
) {

  fun sendSearchEvent(
    searchMeta: SearchMeta,
    searchTermPosition: Int? = null,
  ) {
    genericAnalytics.sendSearchMadeEvent(searchMeta)
    biAnalytics.logEvent(
      name = "Search",
      searchMeta.toBIParameters(searchTermPosition)
    )
  }

  fun sendSearchResultClickEvent(
    app: App,
    position: Int,
    searchMeta: SearchMeta,
  ) {
    genericAnalytics.sendAppPromoClick(
      app = app,
      analyticsContext = analyticsUIContext
    )
    biAnalytics.logEvent(
      name = "Search_Result_Click",
      params = app.toBIParameters(
        aabTypes = "no_info",
        P_POSITION to position,
      ) + searchMeta.toBIParameters(null),
    )
  }

  companion object {
    private const val P_POSITION = "position"
  }
}
