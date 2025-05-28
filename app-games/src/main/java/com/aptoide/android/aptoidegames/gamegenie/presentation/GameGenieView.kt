package com.aptoide.android.aptoidegames.gamegenie.presentation

import ConversationsDrawer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.extensions.ScreenData
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.error_views.GenericErrorView
import com.aptoide.android.aptoidegames.error_views.NoConnectionView
import com.aptoide.android.aptoidegames.gamegenie.analytics.rememberGameGenieAnalytics
import com.aptoide.android.aptoidegames.gamegenie.presentation.composables.ChatParticipantName
import com.aptoide.android.aptoidegames.gamegenie.presentation.composables.MessageList
import com.aptoide.android.aptoidegames.gamegenie.presentation.composables.TextInputBar
import com.aptoide.android.aptoidegames.home.LoadingView

const val genieRoute = "chatbot"

fun gameGenieScreen() = ScreenData.withAnalytics(
  route = genieRoute,
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

@Composable
fun ChatbotView(
  uiState: GameGenieUIState,
  navigateTo: (String) -> Unit,
  onError: () -> Unit,
  onMessageSend: (String) -> Unit,
  onSuggestionSend: (String, Int) -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .wrapContentSize(Alignment.TopCenter)
      .imePadding(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    when (uiState.type) {
      GameGenieUIStateType.LOADING -> ChatScreen(
        uiState = uiState,
        navigateTo = navigateTo,
        onMessageSend = {},
        onSuggestionSend = { _, _ -> },
        isLoading = true
      )

      GameGenieUIStateType.NO_CONNECTION -> NoConnectionView(onRetryClick = onError)
      GameGenieUIStateType.ERROR -> GenericErrorView(onError)
      GameGenieUIStateType.IDLE -> ChatScreen(
        uiState = uiState,
        navigateTo = navigateTo,
        onMessageSend = onMessageSend,
        onSuggestionSend = onSuggestionSend
      )
      GameGenieUIStateType.LOADING_CHAT -> LoadingView()
    }
  }
}

@Composable
fun ChatScreen(
  uiState: GameGenieUIState,
  navigateTo: (String) -> Unit,
  onMessageSend: (String) -> Unit,
  onSuggestionSend: (String, Int) -> Unit,
  isLoading: Boolean = false,
) {
  val suggestions = listOf(
    stringResource(R.string.genai_example_1_body),
    stringResource(R.string.genai_example_2_body),
    stringResource(R.string.genai_example_3_body)
  )

  Column(
    modifier = Modifier
      .padding(vertical = 4.dp, horizontal = 18.dp)
      .fillMaxSize()
  ) {
    MessageList(
      messages = uiState.chat.conversation.asReversed(),
      navigateTo = navigateTo,
      modifier = Modifier
        .weight(1f),
      suggestions = suggestions,
      onSuggestionClick = onSuggestionSend
    )
    if (isLoading) {
      Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 8.dp)
      ) {
        ChatParticipantName(
          stringResource(R.string.genai_bottom_navigation_gamegenie_button)
        )
        TypingAnimation()
      }
    }

    TextInputBar(
      onMessageSent = onMessageSend,
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp)
    )
  }
}
