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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.aptoide.android.aptoidegames.gamegenie.domain.GameCompanion
import com.aptoide.android.aptoidegames.gamegenie.domain.Suggestion
import com.aptoide.android.aptoidegames.gamegenie.presentation.composables.ChatBackButton
import com.aptoide.android.aptoidegames.gamegenie.presentation.composables.ChatParticipantName
import com.aptoide.android.aptoidegames.gamegenie.presentation.composables.MessageList
import com.aptoide.android.aptoidegames.gamegenie.presentation.composables.TextInputBar
import com.aptoide.android.aptoidegames.gamegenie.presentation.composables.companion.ChatbotViewCompanion
import com.aptoide.android.aptoidegames.home.LoadingView

const val genieRoute = "chatbot"

private enum class EntryChoice {
  General,
  Companion
}

fun gameGenieScreen() = ScreenData.withAnalytics(
  route = genieRoute,
  screenAnalyticsName = "gamegenie",
  arguments = emptyList()
) { _, navigate, _ ->

  val viewModel = hiltViewModel<GameGenieViewModel>()
  val uiState by viewModel.uiState.collectAsState()
  val analytics = rememberGameGenieAnalytics()
  val firstLoad by viewModel.firstLoad.collectAsState(true)

  var selectedEntry by remember { mutableStateOf<EntryChoice?>(null) }

  ConversationsDrawer(
    mainScreen = {
      when (selectedEntry) {
        null -> {
          GameGenieEntryScreen(
            myGames = viewModel.installedGames.collectAsState().value,
            onChooseGeneral = {
              analytics.sendGameGenieEntryScreenSearch()
              viewModel.resetSelectedGame()
              selectedEntry = EntryChoice.General
            },
            onChooseCompanion = { selectedGame ->
              analytics.sendGameGenieCompanionClick(selectedGame.packageName)
              viewModel.updateLoadingState()
              viewModel.setSelectedGame(selectedGame)
              viewModel.loadCompanionChat(selectedGame.packageName)
              selectedEntry = EntryChoice.Companion
            }
          )
        }

        EntryChoice.General -> {
          ChatbotView(
            firstLoad = firstLoad,
            uiState = uiState,
            navigateTo = navigate,
            navigateBack = {
              viewModel.resetSelectedGame()
              selectedEntry = null
            },
            onError = viewModel::reload,
            onMessageSend = { message ->
              viewModel.sendMessage(message)
              analytics.sendGameGenieMessageSent()
            },
            setFirstLoadDone = viewModel::setFirstLoadDone,
            onSuggestionSend = { message, index ->
              viewModel.sendMessage(message)
              analytics.sendGameGenieSuggestionClick(index)
            }
          )
        }

        EntryChoice.Companion -> {
          viewModel.selectedGame.collectAsState().value?.let {
            val companionSuggestions by viewModel.companionSuggestions.collectAsState()
            ChatbotViewCompanion(
              selectedGame = it,
              firstLoad = firstLoad,
              navigateBack = {
                viewModel.resetSelectedGame()
                selectedEntry = null
              },
              uiState = uiState,
              navigateTo = navigate,
              onError = viewModel::reload,
              setFirstLoadDone = viewModel::setFirstLoadDone,
              onMessageSend = { message ->
                viewModel.sendMessage(message)
                analytics.sendGameGenieMessageSent()
              },
              suggestions = companionSuggestions,
              onSuggestionClick = { message, index ->
                viewModel.sendMessage(message)
                analytics.sendGameGenieSuggestionClick(index)
              }
            )
          }
        }
      }
    },
    loadConversationFn = { id ->
      selectedEntry = EntryChoice.General
      viewModel.resetSelectedGame()
      viewModel.loadConversation(id)
    },
    currentChatId = uiState.chat.id,
    newChatFn = viewModel::emptyChat,
  )
}

@Composable
fun ChatbotView(
  firstLoad: Boolean,
  uiState: GameGenieUIState,
  navigateTo: (String) -> Unit,
  navigateBack: (() -> Unit)? = null,
  onError: () -> Unit,
  onMessageSend: (String) -> Unit,
  setFirstLoadDone: () -> Unit,
  onSuggestionSend: (String, Int) -> Unit,
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
        navigateTo = navigateTo,
        navigateBack = navigateBack,
        onMessageSend = onMessageSend,
        onSuggestionSend = onSuggestionSend,
        setFirstLoadDone = setFirstLoadDone,
        isLoading = isLoading,
      )
    }
  }
}

@Composable
fun ChatScreen(
  uiState: GameGenieUIState,
  firstLoad: Boolean,
  navigateTo: (String) -> Unit,
  navigateBack: (() -> Unit)?,
  onMessageSend: (String) -> Unit,
  onSuggestionSend: (String, Int) -> Unit,
  setFirstLoadDone: () -> Unit,
  isLoading: Boolean = false,
  selectedGame: GameCompanion? = null,
) {
  val suggestions =
    if (selectedGame != null)
      emptyList()
    else listOf(
      Suggestion(stringResource(R.string.genai_example_1_body), null),
      Suggestion(stringResource(R.string.genai_example_2_body), null),
      Suggestion(stringResource(R.string.genai_example_3_body), null)
    )

  Column(
    modifier = Modifier
      .padding(vertical = 4.dp, horizontal = 18.dp)
      .fillMaxSize()
  ) {
    if (navigateBack != null) {
      ChatBackButton { navigateBack() }
    }
    MessageList(
      messages = uiState.chat.conversation,
      firstLoad = firstLoad,
      navigateTo = navigateTo,
      modifier = Modifier
        .weight(1f),
      suggestions = suggestions,
      setFirstLoadDone = setFirstLoadDone,
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
