package com.aptoide.android.aptoidegames.gamegenie.presentation.composables.companion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.gamegenie.domain.Suggestion
import com.aptoide.android.aptoidegames.gamegenie.domain.GameCompanion
import com.aptoide.android.aptoidegames.gamegenie.presentation.GameGenieUIState
import com.aptoide.android.aptoidegames.gamegenie.presentation.TypingAnimation
import com.aptoide.android.aptoidegames.gamegenie.presentation.composables.ChatParticipantName
import com.aptoide.android.aptoidegames.gamegenie.presentation.composables.MessageList
import com.aptoide.android.aptoidegames.gamegenie.presentation.composables.SelectedGameCompanion
import com.aptoide.android.aptoidegames.gamegenie.presentation.composables.TextInputBar

@Composable
fun ChatScreenCompanion(
  selectedGame: GameCompanion,
  firstLoad: Boolean,
  navigateBack: () -> Unit,
  uiState: GameGenieUIState,
  navigateTo: (String) -> Unit,
  setFirstLoadDone: () -> Unit,
  onMessageSend: (String) -> Unit,
  isLoading: Boolean = false,
  suggestions: List<Suggestion> = emptyList(),
  onSuggestionClick: (String, Int) -> Unit = { _, _ -> },
) {
  Column(
    modifier = Modifier
      .padding(horizontal = 18.dp)
      .fillMaxSize()
  ) {
    val showImage = uiState.chat.conversation.size < 2

    SelectedGameCompanion(selectedGame, showImage) {
      navigateBack()
    }

    MessageList(
      messages = uiState.chat.conversation,
      firstLoad = firstLoad,
      navigateTo = navigateTo,
      setFirstLoadDone = setFirstLoadDone,
      isCompanion = true,
      modifier = Modifier.weight(1f),
      gameName = selectedGame.name,
      suggestions = suggestions,
      onSuggestionClick = onSuggestionClick
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
