package com.aptoide.android.aptoidegames.gamesfeed.analytics

import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import javax.inject.Inject

class GamesFeedAnalytics @Inject constructor(
  private val genericAnalytics: GenericAnalytics,
) {
  fun sendGamesFeedItemClick(title: String, url: String, context: String) =
    genericAnalytics.logEvent(
      name = "foryou_click_feed_item",
      params = mapOf(
        "title" to title,
        "url" to url,
        "context" to context
      )
    )

  fun sendGamesFeedSeeAllClick() = genericAnalytics.logEvent(
    name = "foryou_home_see_all",
    params = emptyMap()
  )

  fun sendGamesFeedImpression(installedPackages: List<String>) = genericAnalytics.logEvent(
    name = "foryou_game_impression",
    params = mapOf("installed_packages" to installedPackages.joinToString(","))
  )
}
