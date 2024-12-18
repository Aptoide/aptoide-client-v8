package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.gamegenie.domain.ChatInteraction

@Composable
fun MessageList(
  messages: List<ChatInteraction>,
  navigateTo: (String) -> Unit,
  modifier: Modifier = Modifier,
  onAllAppsFail: () -> Unit,
  suggestions: List<String>,
  onSuggestionClick: (String) -> Unit,
) {
  val listState = rememberLazyListState()

  LazyColumn(
    state = listState,
    verticalArrangement = Arrangement.Bottom,
    modifier = modifier
      .padding(vertical = 8.dp),
    contentPadding = PaddingValues(vertical = 8.dp)
  ) {
    itemsIndexed(items = messages, key = { idx, message -> message.apps }) { idx, message ->
      if (idx == 0) {
        MessageBubble(
          message = null, isUserMessage = false,
          apps = message.apps.map { app -> app.packageName },
          navigateTo = navigateTo,
          onAllAppsFail = onAllAppsFail,
        )
        suggestions.forEach { suggestion ->
          SuggestionBox(suggestion, onSuggestionClick)
        }
      } else {
        MessageBubble(
          message = message.gpt, isUserMessage = false,
          apps = message.apps.map { app -> app.packageName },
          navigateTo = navigateTo,
          onAllAppsFail = onAllAppsFail,
        )
      }

      message.user?.let { userMessage ->
        MessageBubble(
          message = userMessage,
          isUserMessage = true,
          apps = emptyList(), // No apps for user messages
          navigateTo = navigateTo,
          onAllAppsFail = onAllAppsFail
        )
      }
    }
  }
}
