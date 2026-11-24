package com.aptoide.android.aptoidegames.gamegenie.analytics

import com.aptoide.android.aptoidegames.analytics.GenericAnalytics

/** Where the user opened GameGenie from, now that it is no longer a bottom bar tab (APT-247). */
enum class GameGenieEntryPoint(val source: String) {
  SEARCH_SUGGESTIONS("search_suggestions"),
  SEARCH_EMPTY("search_empty"),
  SETTINGS("settings"),
}

class GameGenieAnalytics(
  private val genericAnalytics: GenericAnalytics,
) {

  companion object {
    const val SOURCE_SEARCH = "search"
    const val SOURCE_CHAT = "chat"
  }

  fun sendGameGenieEntryClick(entryPoint: GameGenieEntryPoint) = genericAnalytics.logEvent(
    name = "gamegenie_entry_click",
    params = mapOf("source" to entryPoint.source)
  )

  fun sendGameGenieSuggestionClick(index: Int) = genericAnalytics.logEvent(
    name = "gamegenie_suggests_click",
    params = mapOf("position" to index)
  )

  fun sendGameGenieMessageSent(source: String) = genericAnalytics.logEvent(
    name = "gamegenie_send_message",
    params = mapOf("source" to source)
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

  fun sendGameGenieEntryScreenSearch() = genericAnalytics.logEvent(
    name = "gamegenie_find_click",
    params = emptyMap()
  )

  fun sendGameGenieCompanionClick(
    packageName: String,
  ) = genericAnalytics.logEvent(
    name = "gamegenie_installedgame_click",
    params = mapOf(
      "package_name" to packageName,
    )
  )

  fun sendGameGenieTryLaunchOverlay(
    packageName: String,
  ) = genericAnalytics.logEvent(
    name = "gamegenie_try_launch_overlay",
    params = mapOf(
      "package_name" to packageName,
    )
  )

  fun sendGameGenieOverlayLaunched(
    packageName: String,
  ) = genericAnalytics.logEvent(
    name = "gamegenie_overlay_launched",
    params = mapOf(
      "package_name" to packageName,
    )
  )

  fun sendGameGenieOverlayDialogLetsDoIt() = genericAnalytics.logEvent(
    name = "gamegenie_overlay_dialog_letsdoit",
    params = emptyMap()
  )

  fun sendGameGenieOverlayClick() = genericAnalytics.logEvent(
    name = "gamegenie_overlay_click",
    params = emptyMap()
  )

  fun sendGameGenieOverlayRemove() = genericAnalytics.logEvent(
    name = "gamegenie_overlay_remove",
    params = emptyMap()
  )

  fun sendGameGenieOverlayAskAnything() = genericAnalytics.logEvent(
    name = "gamegenie_overlay_ask_anything",
    params = emptyMap()
  )

  fun sendGameGenieOverlayScreenshot() = genericAnalytics.logEvent(
    name = "gamegenie_overlay_screenshot",
    params = emptyMap()
  )
}
