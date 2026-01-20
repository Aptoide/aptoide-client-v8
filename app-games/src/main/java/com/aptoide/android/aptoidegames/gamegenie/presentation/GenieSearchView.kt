package com.aptoide.android.aptoidegames.gamegenie.presentation

import ConversationsDrawer
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.extensions.ScreenData
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.gamegenie.analytics.rememberGameGenieAnalytics
import com.aptoide.android.aptoidegames.mmp.WithUTM

const val genieSearchRoute = "genieSearch"

fun gameGenieSearchScreen() = ScreenData.withAnalytics(
  route = genieSearchRoute,
  screenAnalyticsName = "gamegenie",
  arguments = emptyList()
) { _, navigate, _ ->

  val viewModel = hiltViewModel<GameGenieViewModel>()
  val uiState by viewModel.uiState.collectAsState()
  val analytics = rememberGameGenieAnalytics()
  val firstLoad by viewModel.firstLoad.collectAsState(true)

  WithUTM(
    medium = "gamegenie",
    campaign = "gamegenie",
    content = "gamegenie-results",
    navigate = navigate
  ) { navigate ->
    ConversationsDrawer(
      mainScreen = {
        ChatbotView(
          firstLoad = firstLoad,
          uiState = uiState,
          navigateTo = navigate,
          onError = viewModel::reload,
          onMessageSend = { message, _ ->
            viewModel.sendMessage(message)
            analytics.sendGameGenieMessageSent()
          },
          setFirstLoadDone = viewModel::setFirstLoadDone,
          onSuggestionSend = { message, index ->
            viewModel.sendMessage(message)
            analytics.sendGameGenieSuggestionClick(index)
          }
        )
      },
      loadConversationFn = viewModel::loadConversation,
      currentChatId = uiState.chat.id,
      newChatFn = viewModel::emptyChat,
    )
  }
}
