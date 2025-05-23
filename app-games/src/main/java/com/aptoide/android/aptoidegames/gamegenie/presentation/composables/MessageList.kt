package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

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
  suggestions: List<String>,
  onSuggestionClick: (String, Int) -> Unit,
) {
  val listState = rememberLazyListState()


  LazyColumn(
    reverseLayout = true,
    state = listState,
    modifier = modifier
      .padding(vertical = 8.dp),
    contentPadding = PaddingValues(vertical = 8.dp)
  ) {

    itemsIndexed(
      items = messages,
      key = { idx, message -> "$idx-${message.apps}" }) { idx, message ->
      message.user?.let { userMessage ->
        MessageBubble(
          message = userMessage,
          isUserMessage = true,
          videoId = null,
          apps = emptyList(), // No apps for user messages
          navigateTo = navigateTo
        )
      }

      if (idx == messages.lastIndex) {
        suggestions.forEachIndexed { reversedIndex, suggestion ->
          val actualIndex = suggestions.size - reversedIndex
          SuggestionBox(suggestion, onSuggestionClick, actualIndex)
        }

        MessageBubble(
          message = null, isUserMessage = false,
          videoId = message.videoId,
          apps = message.apps,
          navigateTo = navigateTo
        )
      } else {
        MessageBubble(
          message = message.gpt, isUserMessage = false,
          videoId = message.videoId,
          apps = message.apps,
          navigateTo = navigateTo
        )
      }
    }
    item {
      PoweredByAi()
    }
  }
}
