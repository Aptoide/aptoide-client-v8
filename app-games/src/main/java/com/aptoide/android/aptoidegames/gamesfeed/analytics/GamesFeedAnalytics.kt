package com.aptoide.android.aptoidegames.gamesfeed.analytics

import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import javax.inject.Inject

class GamesFeedAnalytics @Inject constructor(
  private val genericAnalytics: GenericAnalytics,
) {
  fun sendGamesFeedItemClick(title: String, url: String, context: String) =
    genericAnalytics.logEvent(
      name = "exp9_gamesfeed_click_feed_item",
      params = mapOf(
        "title" to title,
        "url" to url,
        "context" to context
      )
    )

  fun sendGamesFeedSeeAllClick() = genericAnalytics.logEvent(
    name = "exp9_gamesfeed_home_see_all",
    params = emptyMap()
  )

  fun sendGamesFeedRobloxInstalled() = genericAnalytics.logEvent(
    name = "exp9_roblox_installed",
    params = emptyMap()
  )
}
