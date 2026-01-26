package com.aptoide.android.aptoidegames.gamegenie.presentation

import ConversationsDrawer
import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.extensions.ScreenData
import com.aptoide.android.aptoidegames.BottomSheetContent
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.error_views.GenericErrorView
import com.aptoide.android.aptoidegames.error_views.NoConnectionView
import com.aptoide.android.aptoidegames.gamegenie.analytics.rememberGameGenieAnalytics
import com.aptoide.android.aptoidegames.gamegenie.domain.GameCompanion
import com.aptoide.android.aptoidegames.gamegenie.domain.Suggestion
import com.aptoide.android.aptoidegames.gamegenie.presentation.composables.ChatBackButton
import com.aptoide.android.aptoidegames.gamegenie.presentation.composables.ChatParticipantName
import com.aptoide.android.aptoidegames.gamegenie.presentation.composables.CompanionGamesStrip
import com.aptoide.android.aptoidegames.gamegenie.presentation.composables.MessageList
import com.aptoide.android.aptoidegames.gamegenie.presentation.composables.TextInputBar
import com.aptoide.android.aptoidegames.gamegenie.presentation.composables.companion.ChatbotViewCompanion
import com.aptoide.android.aptoidegames.home.LoadingView
import com.aptoide.android.aptoidegames.mmp.WithUTM

const val genieRoute = "chatbot"

private enum class ChatMode {
  General,
  Companion
}

fun gameGenieScreen(
  showBottomSheet: ((BottomSheetContent?) -> Unit)? = null,
) = ScreenData.withAnalytics(
  route = genieRoute,
  screenAnalyticsName = "gamegenie",
  arguments = emptyList()
) { _, navigate, _ ->

  val viewModel = hiltViewModel<GameGenieViewModel>()
  val uiState by viewModel.uiState.collectAsState()
  val analytics = rememberGameGenieAnalytics()
  val firstLoad by viewModel.firstLoad.collectAsState(true)

  var chatMode by remember { mutableStateOf(ChatMode.General) }

  WithUTM(
    medium = "gamegenie",
    campaign = "gamegenie",
    content = "gamegenie-results",
    navigate = navigate
  ) { navigate ->
    ConversationsDrawer(
      mainScreen = {
        when (chatMode) {
        ChatMode.General -> {
          ChatbotView(
            firstLoad = firstLoad,
            uiState = uiState,
            installedGames = viewModel.installedGames.collectAsState().value,
              navigateTo = navigate,
              navigateBack = null,
              onError = viewModel::reload,
            onMessageSend = { message, image ->
              viewModel.sendMessage(message, image)
              analytics.sendGameGenieMessageSent()
            },
            setFirstLoadDone = viewModel::setFirstLoadDone,
            onSuggestionSend = { message, index ->
              viewModel.sendMessage(message, null)
              analytics.sendGameGenieSuggestionClick(index)
            },
            onGameClick = { selectedGame ->
              analytics.sendGameGenieCompanionClick(selectedGame.packageName)
              viewModel.updateLoadingState()
              viewModel.setSelectedGame(selectedGame)
              viewModel.loadCompanionChat(selectedGame.packageName)
              chatMode = ChatMode.Companion
              }
            )
          }

          ChatMode.Companion -> {
            viewModel.selectedGame.collectAsState().value?.let {
              val companionSuggestions by viewModel.companionSuggestions.collectAsState()
              val installedGames by viewModel.installedGames.collectAsState()
            ChatbotViewCompanion(
              selectedGame = it,
              firstLoad = firstLoad,
              navigateBack = {
                viewModel.resetSelectedGame()
                viewModel.emptyChat()
                chatMode = ChatMode.General
                },
                uiState = uiState,
                navigateTo = navigate,
                onError = viewModel::reload,
                setFirstLoadDone = viewModel::setFirstLoadDone,
                onMessageSend = { message, image ->
                  viewModel.sendMessage(message, image)
                  analytics.sendGameGenieMessageSent()
                },
                showBottomSheet = showBottomSheet,
                suggestions = companionSuggestions,
                onSuggestionClick = { message, index ->
                  viewModel.sendMessage(message)
                  analytics.sendGameGenieSuggestionClick(index)
                },
              installedGames = installedGames,
              onGameSwitch = { newGame ->
                viewModel.setSelectedGame(newGame)
                viewModel.loadCompanionChat(newGame.packageName)
              },
              onOverlayInteraction = viewModel::setClickedOverlayButton,
              onClearScreenshot = viewModel::clearScreenshot
            )
          }
        }
      }
    },
    loadConversationFn = { id ->
      chatMode = ChatMode.General
        viewModel.resetSelectedGame()
        viewModel.loadConversation(id)
      },
      currentChatId = uiState.chat.id,
      newChatFn = viewModel::emptyChat,
    )
  }
}

@Composable
fun ChatbotView(
  firstLoad: Boolean,
  uiState: GameGenieUIState,
  installedGames: List<GameCompanion> = emptyList(),
  navigateTo: (String) -> Unit,
  navigateBack: (() -> Unit)? = null,
  onError: () -> Unit,
  onMessageSend: (String, String?) -> Unit,
  setFirstLoadDone: () -> Unit,
  onSuggestionSend: (String, Int) -> Unit,
  onGameClick: (GameCompanion) -> Unit = {},
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .wrapContentSize(Alignment.TopCenter)
      .imePadding(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    val isLoading = uiState.type == GameGenieUIStateType.LOADING

    when (uiState.type) {
      GameGenieUIStateType.LOADING_CHAT -> LoadingView()
      GameGenieUIStateType.NO_CONNECTION -> NoConnectionView(onRetryClick = onError)
      GameGenieUIStateType.ERROR -> GenericErrorView(onError)
      else -> ChatScreen(
        uiState = uiState,
        firstLoad = firstLoad,
        installedGames = installedGames,
        navigateTo = navigateTo,
        navigateBack = navigateBack,
        onMessageSend = onMessageSend,
        onSuggestionSend = onSuggestionSend,
        setFirstLoadDone = setFirstLoadDone,
        isLoading = isLoading,
        onGameClick = onGameClick,
      )
    }
  }
}

@Composable
fun ChatScreen(
  uiState: GameGenieUIState,
  firstLoad: Boolean,
  installedGames: List<GameCompanion> = emptyList(),
  navigateTo: (String) -> Unit,
  navigateBack: (() -> Unit)?,
  onMessageSend: (String, String?) -> Unit,
  onSuggestionSend: (String, Int) -> Unit,
  setFirstLoadDone: () -> Unit,
  isLoading: Boolean = false,
  selectedGame: GameCompanion? = null,
  onGameClick: (GameCompanion) -> Unit = {},
) {
  val hasUserMessages = uiState.chat.conversation.any { it.user != null }

  val suggestions =
    if (selectedGame != null || hasUserMessages)
      emptyList()
    else listOf(
      Suggestion(stringResource(R.string.genai_example_1_body), null),
      Suggestion(stringResource(R.string.genai_example_2_body), null),
      Suggestion(stringResource(R.string.genai_example_3_body), null)
    )

  Column(
    modifier = Modifier
      .padding(vertical = 4.dp)
      .fillMaxSize()
  ) {
    if (navigateBack != null) {
      ChatBackButton(modifier = Modifier.padding(horizontal = 18.dp)) { navigateBack() }
    }

    if (hasUserMessages) {
      CompanionGamesStrip(
        games = installedGames,
        onGameClick = onGameClick
      )
    }

    Box(modifier = Modifier.weight(1f)) {
      MessageList(
        messages = uiState.chat.conversation,
        firstLoad = firstLoad,
        navigateTo = navigateTo,
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 18.dp),
        suggestions = suggestions,
        setFirstLoadDone = setFirstLoadDone,
        onSuggestionClick = onSuggestionSend,
        installedGames = if (hasUserMessages) emptyList() else installedGames,
        onGameClick = onGameClick
      )
    }
    if (isLoading) {
      Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 18.dp, end = 18.dp, bottom = 8.dp)
      ) {
        ChatParticipantName(
          stringResource(R.string.genai_bottom_navigation_gamegenie_button)
        )
        TypingAnimation()
      }
    }

    TextInputBar(
      onMessageSent = { message, imagePath ->
        onMessageSend(message, null)
      },
      screenshotPath = null,
      onClearScreenshot = {},
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = 18.dp, end = 18.dp, bottom = 8.dp)
    )
  }
}
