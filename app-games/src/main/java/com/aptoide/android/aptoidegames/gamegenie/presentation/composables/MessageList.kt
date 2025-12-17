package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.gamegenie.domain.ChatInteraction
import com.aptoide.android.aptoidegames.gamegenie.domain.Suggestion
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun MessageList(
  messages: List<ChatInteraction>,
  firstLoad: Boolean,
  navigateTo: (String) -> Unit,
  modifier: Modifier = Modifier,
  suggestions: List<Suggestion> = emptyList(),
  setFirstLoadDone: () -> Unit,
  onSuggestionClick: (String, Int) -> Unit = { _, _ -> },
  isCompanion: Boolean = false,
  gameName: String = "",
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
      .padding(bottom = 8.dp),
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
        if (suggestions.isNotEmpty()) {
          LazyRow(
            contentPadding = PaddingValues(top = 16.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            itemsIndexed(suggestions) { reversedIndex, suggestion ->
              val actualIndex = suggestions.size - reversedIndex
              SuggestionBox(
                suggestion = suggestion.suggestion,
                onClick = onSuggestionClick,
                index = actualIndex,
                emoji = suggestion.emoji
              )
            }
          }
        }

        MessageBubble(
          message = null, isUserMessage = false,
          videoId = message.videoId,
          apps = message.apps,
          navigateTo = navigateTo,
          playerCache = playerCache,
          isCompanion = isCompanion,
          gameName = gameName,
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

    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 88.dp),
        contentAlignment = Alignment.Center
      ) {
        if (!isCompanion) {
          AnimationComposable(
            modifier = Modifier.size(175.dp),
            resId = R.raw.game_genie_chat_big_animation
          )

          Text(
            text = stringResource(R.string.genai_bottom_navigation_gamegenie_button),
            style = AGTypography.InputsL,
            color = Palette.Primary,
            modifier = Modifier
              .align(Alignment.Center)
              .offset(y = 70.dp)
          )
        }
      }
    }
  }
}
