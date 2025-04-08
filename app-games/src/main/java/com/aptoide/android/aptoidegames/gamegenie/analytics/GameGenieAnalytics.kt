package com.aptoide.android.aptoidegames.gamegenie.analytics

import com.aptoide.android.aptoidegames.analytics.BIAnalytics

class GameGenieAnalytics(
  private val biAnalytics: BIAnalytics,
) {

  fun sendGameGenieSuggestionClick(index: Int) = biAnalytics.logEvent(
    name = "gamegenie_suggests_click",
    params = mapOf("position" to index)
  )

  fun sendGameGenieMessageSent() = biAnalytics.logEvent(
    name = "gamegenie_send_message",
    params = emptyMap()
  )

  fun sendGameGenieAppClick(packageName: String, appPosition: Int) = biAnalytics.logEvent(
    name = "gamegenie_app_click",
    params = mapOf(
      "package_name" to packageName,
      "app_position" to appPosition
    )
  )

  fun sendGameGenieHistoryOpen() = biAnalytics.logEvent(
    name = "gamegenie_history_open",
    params = emptyMap()
  )

  fun sendGameGenieHistoryClick() = biAnalytics.logEvent(
    name = "gamegenie_history_click",
    params = emptyMap()
  )

  fun sendGameGenieHistoryDelete() = biAnalytics.logEvent(
    name = "gamegenie_history_delete",
    params = emptyMap()
  )

  fun sendGameGenieNewChat() = biAnalytics.logEvent(
    name = "gamegenie_new_chat",
    params = emptyMap()
  )
}
