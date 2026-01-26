package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.gamegenie.domain.ChatInteraction
import com.aptoide.android.aptoidegames.gamegenie.domain.GameCompanion
import com.aptoide.android.aptoidegames.gamegenie.domain.UserMessage
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
  installedGames: List<GameCompanion> = emptyList(),
  onGameClick: (GameCompanion) -> Unit = {}
) {
  val listState = rememberLazyListState()
  val playerCache = remember { mutableMapOf<String, YouTubePlayerView>() }

  val lastUserIndex = remember(messages) { messages.indexOfLast { it.user != null } }
  val lastUserHeightPx = remember { mutableIntStateOf(0) }
  val lastHandledSize = remember { mutableIntStateOf(0) }

  LaunchedEffect(lastUserIndex, messages.size, firstLoad) {
    if (messages.isEmpty()) {
      setFirstLoadDone()
      lastHandledSize.intValue = 0
      return@LaunchedEffect
    }

    if (firstLoad) {
      listState.scrollToItem(
        index = messages.lastIndex,
        scrollOffset = Int.MAX_VALUE
      )
      lastHandledSize.intValue = messages.size
      setFirstLoadDone()
      return@LaunchedEffect
    }

    if (messages.size == lastHandledSize.intValue) {
      return@LaunchedEffect
    }

    lastHandledSize.intValue = messages.size

    val targetIndex = messages.lastIndex

    listState.animateScrollToItem(
      index = targetIndex,
      scrollOffset = 0
    )
  }

  LazyColumn(
    state = listState,
    modifier = modifier
      .padding(bottom = 8.dp),
    contentPadding = PaddingValues(vertical = 8.dp)
  ) {
    itemsIndexed(
      items = messages,
      key = { idx, _ -> "msg_$idx" }
    ) { idx, message ->
      if (idx == 0) {
        MessageBubble(
          message = null,
          isUserMessage = false,
          videoId = message.videoId,
          apps = message.apps,
          navigateTo = navigateTo,
          playerCache = playerCache,
          isCompanion = isCompanion,
          gameName = gameName,
        )

        AnimatedVisibility(
          visible = suggestions.isNotEmpty(),
          enter = slideInHorizontally { it },
          exit = slideOutHorizontally { it }
        ) {
          Column {
            LazyRow(
              contentPadding = PaddingValues(top = 16.dp, bottom = 4.dp),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              itemsIndexed(suggestions) { suggestionIndex, suggestion ->
                SuggestionBox(
                  suggestion = suggestion.suggestion,
                  onClick = onSuggestionClick,
                  index = suggestionIndex + 1,
                  emoji = suggestion.emoji
                )
              }
            }

            CompanionGameChoice(
              title = stringResource(R.string.gamegenie_companion_tap_fav_game),
              games = installedGames,
              onGameClick = onGameClick,
              modifier = Modifier.fillMaxWidth()
            )
          }
        }
      } else {
        MessageBubble(
          message = message.gpt,
          isUserMessage = false,
          videoId = message.videoId,
          apps = message.apps,
          navigateTo = navigateTo,
          playerCache = playerCache,
          gameName = gameName,
        )
      }

      message.user?.let { userMessage ->
        MessageBubble(
          message = userMessage.text,
          image = userMessage.image,
          isUserMessage = true,
          videoId = null,
          apps = emptyList(),
          navigateTo = navigateTo,
          playerCache = playerCache,
          onHeightMeasured = { h ->
            if (idx == lastUserIndex) {
              lastUserHeightPx.intValue = h
            }
          }
        )
      }
    }
  }
}
