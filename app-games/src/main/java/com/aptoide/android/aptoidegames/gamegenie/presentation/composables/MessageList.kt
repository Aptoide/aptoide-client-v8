package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.gamegenie.domain.ChatInteraction
import com.aptoide.android.aptoidegames.gamegenie.domain.GameCompanion
import com.aptoide.android.aptoidegames.gamegenie.domain.Suggestion
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop

@Composable
fun MessageList(
  messages: List<ChatInteraction>,
  firstLoad: Boolean,
  navigateTo: (String) -> Unit,
  modifier: Modifier = Modifier,
  suggestions: List<Suggestion> = emptyList(),
  setFirstLoadDone: () -> Unit,
  onSuggestionClick: (String, Int) -> Unit = { _, _ -> },
  onFollowUpClick: (String) -> Unit = {},
  isCompanion: Boolean = false,
  gameName: String = "",
  installedGames: List<GameCompanion> = emptyList(),
  onGameClick: (GameCompanion) -> Unit = {},
  isStreaming: Boolean = false,
) {
  val listState = rememberLazyListState()
  val playerCache = remember { mutableMapOf<String, YouTubePlayerView>() }

  val prevSize = remember { mutableIntStateOf(-1) }
  val prevLastUserText = remember { mutableStateOf<String?>(null) }
  val prevViewportHeight = remember { mutableIntStateOf(0) }

  val lastUserText = messages.lastOrNull()?.user?.text

  val userTookControl = remember { mutableStateOf(false) }
  LaunchedEffect(lastUserText) { userTookControl.value = false }

  LaunchedEffect(listState) {
    snapshotFlow { listState.layoutInfo.viewportSize.height }
      .distinctUntilChanged()
      .collectLatest { viewportHeight ->
        val prev = prevViewportHeight.intValue
        if (viewportHeight in 1 until prev && messages.isNotEmpty()) {
          scrollToBottom(listState, messages.lastIndex)
        }
        prevViewportHeight.intValue = viewportHeight
      }
  }

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

  val streamingGptLength = if (isStreaming) messages.lastOrNull()?.gpt?.length ?: 0 else 0
  LaunchedEffect(isStreaming, streamingGptLength) {
    if (isStreaming && messages.isNotEmpty() && !userTookControl.value) {
      listState.scrollToItem(messages.lastIndex, Int.MAX_VALUE)
    }
  }

  val lastAppsCount = messages.lastOrNull()?.apps?.size ?: 0
  val lastVideoId = messages.lastOrNull()?.videoId
  val lastFollowUpsCount = messages.lastOrNull()?.followUps?.size ?: 0
  LaunchedEffect(isStreaming, lastAppsCount, lastVideoId, lastFollowUpsCount, messages.size) {
    if (isStreaming || messages.isEmpty()) return@LaunchedEffect
    if (lastAppsCount == 0 && lastVideoId == null && lastFollowUpsCount == 0) return@LaunchedEffect
    if (userTookControl.value) return@LaunchedEffect

    listState.scrollToItem(messages.lastIndex, Int.MAX_VALUE)

    var settleBudget = 1_000L
    snapshotFlow {
      listState.layoutInfo.visibleItemsInfo.sumOf { it.size } to
        listState.layoutInfo.totalItemsCount
    }
      .distinctUntilChanged()
      .drop(1)
      .collectLatest {
        if (settleBudget <= 0L) return@collectLatest
        if (!userTookControl.value) {
          listState.scrollToItem(messages.lastIndex, Int.MAX_VALUE)
        }
        delay(50)
        settleBudget -= 50
      }
  }

  LazyColumn(
    state = listState,
    modifier = modifier
      .padding(bottom = 8.dp)
      .pointerInput(Unit) {
        awaitEachGesture {
          awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
          userTookControl.value = true
        }
      },
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
          isStreaming = isStreaming && idx == messages.lastIndex,
        )
      }

      if (
        idx == messages.lastIndex &&
        message.user == null &&
        message.followUps.isNotEmpty() &&
        !isStreaming
      ) {
        LazyRow(
          contentPadding = PaddingValues(top = 16.dp, bottom = 4.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          itemsIndexed(message.followUps) { _, followUp ->
            FollowUpBox(
              suggestion = followUp,
              onClick = onFollowUpClick
            )
          }
        }
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

