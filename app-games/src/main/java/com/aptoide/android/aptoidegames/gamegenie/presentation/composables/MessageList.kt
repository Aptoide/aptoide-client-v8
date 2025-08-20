package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.gamegenie.domain.ChatInteraction
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun MessageList(
  messages: List<ChatInteraction>,
  firstLoad: Boolean,
  navigateTo: (String) -> Unit,
  modifier: Modifier = Modifier,
  suggestions: List<String>,
  setFirstLoadDone: () -> Unit,
  onSuggestionClick: (String, Int) -> Unit,
) {
  val listState = rememberLazyListState()
  val playerCache = remember { mutableMapOf<String, YouTubePlayerView>() }
  val messageList = messages.asReversed()

  val lastUserIndex = remember(messageList) { messageList.indexOfFirst { it.user != null } }
  val lastUserHeightPx = remember { mutableIntStateOf(0) }

  LaunchedEffect(lastUserIndex, lastUserHeightPx.intValue, messageList) {
    if (lastUserIndex != -1 && !firstLoad) {
      val viewportHeight =
        listState.layoutInfo.viewportEndOffset - listState.layoutInfo.viewportStartOffset

      val offset = (viewportHeight - lastUserHeightPx.intValue).coerceAtLeast(0)

      listState.animateScrollToItem(index = lastUserIndex, scrollOffset = -offset)
    } else {
      listState.animateScrollToItem(index = 0)
      setFirstLoadDone()
    }
  }

  LazyColumn(
    reverseLayout = true,
    state = listState,
    modifier = modifier
      .padding(vertical = 8.dp),
    contentPadding = PaddingValues(vertical = 8.dp)
  ) {

    itemsIndexed(
      items = messageList,
      key = { idx, _ -> "${messageList.size - idx}" }) { idx, message ->
      message.user?.let { userMessage ->
        MessageBubble(
          message = userMessage,
          isUserMessage = true,
          videoId = null,
          apps = emptyList(), // No apps for user messages
          navigateTo = navigateTo,
          playerCache = playerCache,
          onHeightMeasured = { h ->
            if (idx == lastUserIndex) {
              lastUserHeightPx.intValue = h
            }
          }
        )
      }

      if (idx == messageList.lastIndex) {
        suggestions.forEachIndexed { reversedIndex, suggestion ->
          val actualIndex = suggestions.size - reversedIndex
          SuggestionBox(suggestion, onSuggestionClick, actualIndex)
        }

        MessageBubble(
          message = null, isUserMessage = false,
          videoId = message.videoId,
          apps = message.apps,
          navigateTo = navigateTo,
          playerCache
        )
      } else {
        MessageBubble(
          message = message.gpt,
          isUserMessage = false,
          videoId = message.videoId,
          apps = message.apps,
          navigateTo = navigateTo,
          playerCache = playerCache,
        )
      }
    }
    item {
      PoweredByAi()
    }
  }
}
