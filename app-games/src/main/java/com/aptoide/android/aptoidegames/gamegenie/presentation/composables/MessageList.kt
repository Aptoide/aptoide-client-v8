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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.gamegenie.domain.ChatInteraction
import com.aptoide.android.aptoidegames.gamegenie.domain.GameCompanion
import com.aptoide.android.aptoidegames.gamegenie.domain.Suggestion
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

  val prevSize = remember { mutableIntStateOf(-1) }
  val prevLastUserText = remember { mutableStateOf<String?>(null) }

  val lastUserText = messages.lastOrNull()?.user?.text

  LaunchedEffect(firstLoad, messages.size, lastUserText) {
    if (messages.isEmpty()) {
      setFirstLoadDone()
      prevSize.intValue = 0
      prevLastUserText.value = null
      return@LaunchedEffect
    }

    val currentSize = messages.size
    val currentLastUserText = lastUserText

    when {
      firstLoad -> {
        scrollToBottom(listState, messages.lastIndex)
        setFirstLoadDone()
      }

      currentSize > prevSize.intValue && prevSize.intValue >= 0 -> {
        scrollLastGptToTopWithMargin(listState, messages.lastIndex)
      }

      currentLastUserText != prevLastUserText.value -> {
        scrollToBottom(listState, messages.lastIndex)
      }
    }

    prevSize.intValue = currentSize
    prevLastUserText.value = currentLastUserText
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
        )
      }
    }
  }
}

private suspend fun scrollToBottom(
  listState: LazyListState,
  lastIndex: Int,
) {
  listState.animateScrollToItem(lastIndex, Int.MAX_VALUE)
}

private suspend fun scrollLastGptToTopWithMargin(
  listState: LazyListState,
  lastIndex: Int,
) {
  listState.animateScrollToItem(lastIndex, 0)
}
