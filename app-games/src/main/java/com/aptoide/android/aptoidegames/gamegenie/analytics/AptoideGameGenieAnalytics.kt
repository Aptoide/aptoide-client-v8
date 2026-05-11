package com.aptoide.android.aptoidegames.gamegenie.analytics

import cm.aptoide.pt.feature_gamegenie.analytics.GameGenieAnalytics
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics

class AptoideGameGenieAnalytics(
  private val genericAnalytics: GenericAnalytics,
) : GameGenieAnalytics {

  override fun sendGameGenieSuggestionClick(index: Int) = genericAnalytics.logEvent(
    name = "gamegenie_suggests_click",
    params = mapOf("position" to index)
  )

  override fun sendGameGenieMessageSent(source: String) = genericAnalytics.logEvent(
    name = "gamegenie_send_message",
    params = mapOf("source" to source)
  )

  override fun sendGameGenieAppClick(
    packageName: String,
    appPosition: Int,
  ) = genericAnalytics.logEvent(
    name = "gamegenie_app_click",
    params = mapOf(
      "package_name" to packageName,
      "app_position" to appPosition
    )
  )

  override fun sendGameGenieHistoryOpen() = genericAnalytics.logEvent(
    name = "gamegenie_history_open",
    params = emptyMap()
  )

  override fun sendGameGenieHistoryClick() = genericAnalytics.logEvent(
    name = "gamegenie_history_click",
    params = emptyMap()
  )

  override fun sendGameGenieHistoryDelete() = genericAnalytics.logEvent(
    name = "gamegenie_history_delete",
    params = emptyMap()
  )

  override fun sendGameGenieNewChat() = genericAnalytics.logEvent(
    name = "gamegenie_new_chat",
    params = emptyMap()
  )

  override fun sendGameGenieEntryScreenSearch() = genericAnalytics.logEvent(
    name = "gamegenie_find_click",
    params = emptyMap()
  )

  override fun sendGameGenieCompanionClick(
    packageName: String,
  ) = genericAnalytics.logEvent(
    name = "gamegenie_installedgame_click",
    params = mapOf(
      "package_name" to packageName,
    )
  )

  override fun sendGameGenieTryLaunchOverlay(
    packageName: String,
  ) = genericAnalytics.logEvent(
    name = "gamegenie_try_launch_overlay",
    params = mapOf(
      "package_name" to packageName,
    )
  )

  override fun sendGameGenieOverlayLaunched(
    packageName: String,
  ) = genericAnalytics.logEvent(
    name = "gamegenie_overlay_launched",
    params = mapOf(
      "package_name" to packageName,
    )
  )

  override fun sendGameGenieOverlayDialogLetsDoIt() = genericAnalytics.logEvent(
    name = "gamegenie_overlay_dialog_letsdoit",
    params = emptyMap()
  )

  override fun sendGameGenieOverlayClick() = genericAnalytics.logEvent(
    name = "gamegenie_overlay_click",
    params = emptyMap()
  )

  override fun sendGameGenieOverlayRemove() = genericAnalytics.logEvent(
    name = "gamegenie_overlay_remove",
    params = emptyMap()
  )

  override fun sendGameGenieOverlayAskAnything() = genericAnalytics.logEvent(
    name = "gamegenie_overlay_ask_anything",
    params = emptyMap()
  )

  override fun sendGameGenieOverlayScreenshot() = genericAnalytics.logEvent(
    name = "gamegenie_overlay_screenshot",
    params = emptyMap()
  )
}
