package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import ConversationHistoryBox
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.gamegenie.domain.ConversationInfo
import com.aptoide.android.aptoidegames.gamegenie.presentation.ConversationHistoryUIState.Idle
import com.aptoide.android.aptoidegames.gamegenie.presentation.ConversationHistoryUIState.Loading
import com.aptoide.android.aptoidegames.gamegenie.presentation.rememberGameGenieHistoryUiState
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.toolbar.getToolBarLogo

@Composable
fun DrawerContent(
  onConversationClick: (String) -> Unit,
  currentChatId: String,
  onNewChatClick: () -> Unit,
  onChatDeleteAnalytics: () -> Unit,
) {

  when (val uiState = rememberGameGenieHistoryUiState()) {
    is Idle -> GameGenieDrawerContent(
      onConversationClick = onConversationClick,
      currentChatId = currentChatId,
      onNewChatClick = onNewChatClick,
      conversations = uiState.pastConversations,
      onDeleteChat = { chatId ->
        uiState.onDeleteChat(chatId)
        onChatDeleteAnalytics()
      }
    )

    Loading -> {}
  }
}

@Composable
private fun GameGenieDrawerContent(
  onConversationClick: (String) -> Unit,
  currentChatId: String,
  onNewChatClick: () -> Unit,
  conversations: List<ConversationInfo>,
  onDeleteChat: (String) -> Unit,
) {
  Box(
    modifier = Modifier.fillMaxSize()
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 8.dp)
    ) {
      Image(
        imageVector = BuildConfig.FLAVOR.getToolBarLogo(Palette.Primary),
        contentDescription = null,
        modifier = Modifier
          .padding(start = 16.dp, top = 8.dp)
      )
      Text(
        stringResource(R.string.genai_history_body),
        style = AGTypography.InputsM,
        color = Palette.GreyLight,
        modifier = Modifier.padding(top = 28.dp, bottom = 16.dp, start = 16.dp)
      )

      LazyColumn(
        modifier = Modifier
          .weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        items(conversations) { conversation ->
          ConversationHistoryBox(
            conversation,
            onConversationClick,
            currentChatId == conversation.id,
            onDeleteChat
          )
        }
      }

      Button(
        onClick = { onNewChatClick() },
        shape = RectangleShape,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
          .height(48.dp),
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier
            .fillMaxWidth()
        ) {
          Text(
            text = stringResource(R.string.genai_new_chat_button),
            style = AGTypography.ChatBold,
            textAlign = TextAlign.Start
          )
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            tint = Palette.GreyDark,
            contentDescription = "Send Message"
          )
        }
      }
    }
  }
}
