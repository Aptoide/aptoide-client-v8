package com.aptoide.android.aptoidegames.gamegenie.analytics

import com.aptoide.android.aptoidegames.analytics.BIAnalytics

class GameGenieAnalytics(
  private val biAnalytics: BIAnalytics,
) {

  fun sendGameGenieSuggestionClick(index: Int) = biAnalytics.logEvent(
    name = "gamegenie_suggests_click",
    params = mapOf("position" to index)
  )
}
