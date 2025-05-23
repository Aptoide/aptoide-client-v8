package com.aptoide.android.aptoidegames.gamegenie.analytics

import com.aptoide.android.aptoidegames.analytics.GenericAnalytics

class GameGenieAnalytics(
  private val genericAnalytics: GenericAnalytics,
) {

  fun sendGameGenieSuggestionClick(index: Int) = genericAnalytics.logEvent(
    name = "gamegenie_suggests_click",
    params = mapOf("position" to index)
  )

  fun sendGameGenieMessageSent() = genericAnalytics.logEvent(
    name = "gamegenie_send_message",
    params = emptyMap()
  )

  fun sendGameGenieAppClick(
    packageName: String,
    appPosition: Int,
  ) = genericAnalytics.logEvent(
    name = "gamegenie_app_click",
    params = mapOf(
      "package_name" to packageName,
      "app_position" to appPosition
    )
  )

  fun sendGameGenieHistoryOpen() = genericAnalytics.logEvent(
    name = "gamegenie_history_open",
    params = emptyMap()
  )

  fun sendGameGenieHistoryClick() = genericAnalytics.logEvent(
    name = "gamegenie_history_click",
    params = emptyMap()
  )

  fun sendGameGenieHistoryDelete() = genericAnalytics.logEvent(
    name = "gamegenie_history_delete",
    params = emptyMap()
  )

  fun sendGameGenieNewChat() = genericAnalytics.logEvent(
    name = "gamegenie_new_chat",
    params = emptyMap()
  )
}
