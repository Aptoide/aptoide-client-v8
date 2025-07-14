package com.aptoide.android.aptoidegames.gamegenie.presentation

import ConversationsDrawer
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.extensions.ScreenData
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.gamegenie.analytics.rememberGameGenieAnalytics

const val genieSearchRoute = "genieSearch"

fun gameGenieSearchScreen() = ScreenData.withAnalytics(
  route = genieSearchRoute,
  screenAnalyticsName = "gamegenie",
  arguments = emptyList()
) { _, navigate, _ ->

  val viewModel = hiltViewModel<GameGenieViewModel>()
  val uiState by viewModel.uiState.collectAsState()
  val analytics = rememberGameGenieAnalytics()

  ConversationsDrawer(
    mainScreen = {
      ChatbotView(
        uiState = uiState,
        navigateTo = navigate,
        onError = viewModel::reload,
        onMessageSend = { message ->
          viewModel.sendMessage(message)
          analytics.sendGameGenieMessageSent()
        },
        onSuggestionSend = { message, index ->
          viewModel.sendMessage(message)
          analytics.sendGameGenieSuggestionClick(index)
        },
      )
    },
    loadConversationFn = viewModel::loadConversation,
    currentChatId = uiState.chat.id,
    newChatFn = viewModel::createNewChat,
  )
}
