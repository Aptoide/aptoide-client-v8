package com.aptoide.android.aptoidegames.gamegenie.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.aptoide.android.aptoidegames.gamegenie.presentation.composables.ChatParticipantName
import com.aptoide.android.aptoidegames.gamegenie.presentation.composables.MessageList
import com.aptoide.android.aptoidegames.gamegenie.presentation.composables.TextInputBar

const val genieRoute = "chatbot"

fun gameGenieScreen() = ScreenData.withAnalytics(
  route = genieRoute,
  screenAnalyticsName = "gamegenie",
  arguments = emptyList()
) { _, navigate, _ ->

  val viewModel = hiltViewModel<ChatbotViewModel>()
  val uiState by viewModel.uiState.collectAsState()

  ChatbotView(
    uiState = uiState,
    navigateTo = navigate,
    onError = viewModel::reload,
    onMessageSend = viewModel::sendMessage,
    onAllAppsFail = viewModel::setGeneralError
  )
}

@Composable
fun ChatbotView(
  uiState: GameGenieUIState,
  navigateTo: (String) -> Unit,
  onError: () -> Unit,
  onMessageSend: (String) -> Unit,
  onAllAppsFail: () -> Unit,
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
        uiState = uiState, navigateTo = navigateTo, onMessageSend = {}, isLoading = true,
        onAllAppsFail = onAllAppsFail
      )

      GameGenieUIStateType.NO_CONNECTION -> NoConnectionView(onRetryClick = onError)
      GameGenieUIStateType.ERROR -> GenericErrorView(onError)
      GameGenieUIStateType.IDLE -> ChatScreen(
        uiState = uiState, navigateTo = navigateTo, onMessageSend = onMessageSend,
        onAllAppsFail = onAllAppsFail
      )
    }
  }
}

@Composable
fun ChatScreen(
  uiState: GameGenieUIState,
  navigateTo: (String) -> Unit,
  onMessageSend: (String) -> Unit,
  isLoading: Boolean = false,
  onAllAppsFail: () -> Unit,
) {
  val listState = rememberLazyListState()

  LaunchedEffect(key1 = uiState.conversation) {
    listState.animateScrollToItem(index = uiState.conversation.lastIndex)
  }

  Column(
    modifier = Modifier
      .padding(vertical = 4.dp, horizontal = 18.dp)
  ) {
    MessageList(
      messages = uiState.conversation,
      navigateTo = navigateTo,
      listState = listState,
      modifier = Modifier
        .weight(1f),
      onAllAppsFail = onAllAppsFail
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
    )
  }
}
