package com.aptoide.android.aptoidegames.search.analytics

import cm.aptoide.pt.feature_apps.data.App
import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.analytics.dto.SearchMeta
import com.aptoide.android.aptoidegames.analytics.toBIParameters
import com.aptoide.android.aptoidegames.analytics.toGenericParameters

class SearchAnalytics(
  private val biAnalytics: BIAnalytics,
  private val genericAnalytics: GenericAnalytics,
) {

  fun sendSearchEvent(
    searchMeta: SearchMeta,
    searchTermPosition: Int? = null,
  ) {
    genericAnalytics.logEvent(
      name = "search_made",
      params = searchMeta.toGenericParameters()
    )
    biAnalytics.logEvent(
      name = "Search",
      searchMeta.toBIParameters(searchTermPosition)
    )
  }

  fun sendSearchResultClickEvent(
    app: App,
    position: Int,
    searchMeta: SearchMeta?,
  ) {
    biAnalytics.logEvent(
      name = "Search_Result_Click",
      params = app.toBIParameters(
        aabTypes = "no_info",
        P_POSITION to position,
      ) + searchMeta.toBIParameters(null),
    )
  }

  fun sendEmptySearchResultClickEvent(
    searchMeta: SearchMeta,
  ) {
    biAnalytics.logEvent(
      name = "Search_Result_Click",
      params = mapOf(P_POSITION to "empty") + searchMeta.toBIParameters(null)
    )
  }

  companion object {
    private const val P_POSITION = "position"
  }
}
