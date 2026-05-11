package cm.aptoide.pt.feature_gamegenie.analytics

interface GameGenieAnalytics {

  fun sendGameGenieSuggestionClick(index: Int)

  fun sendGameGenieMessageSent(source: String)

  fun sendGameGenieAppClick(packageName: String, appPosition: Int)

  fun sendGameGenieHistoryOpen()

  fun sendGameGenieHistoryClick()

  fun sendGameGenieHistoryDelete()

  fun sendGameGenieNewChat()

  fun sendGameGenieEntryScreenSearch()

  fun sendGameGenieCompanionClick(packageName: String)

  fun sendGameGenieTryLaunchOverlay(packageName: String)

  fun sendGameGenieOverlayLaunched(packageName: String)

  fun sendGameGenieOverlayDialogLetsDoIt()

  fun sendGameGenieOverlayClick()

  fun sendGameGenieOverlayRemove()

  fun sendGameGenieOverlayAskAnything()

  fun sendGameGenieOverlayScreenshot()

  companion object {
    const val SOURCE_SEARCH = "search"
    const val SOURCE_CHAT = "chat"
  }
}
