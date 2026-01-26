package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.gamegenie.domain.GameCompanion
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import kotlin.math.roundToInt

@Composable
fun CompanionGamesStrip(
  games: List<GameCompanion>,
  onGameClick: (GameCompanion) -> Unit,
  modifier: Modifier = Modifier
) {
  if (games.isEmpty()) return

  var isExpanded by remember { mutableStateOf(false) }
  var headerBottomPx by remember { mutableStateOf(0) }
  var containerTopPx by remember { mutableStateOf(0) }
  var stripWidthPx by remember { mutableStateOf(0) }

  val density = LocalDensity.current
  val iconSizePx = with(density) { 32.dp.toPx() }
  val spacingPx = with(density) { 4.dp.toPx() }
  val requiredWidthPx = (games.size * iconSizePx) + ((games.size - 1).coerceAtLeast(0) * spacingPx)
  val shouldShowShadow = stripWidthPx > 0 && requiredWidthPx > stripWidthPx

  Box(
    modifier = modifier
      .fillMaxWidth()
      .background(Palette.Black)
      .onGloballyPositioned { coordinates ->
        containerTopPx = coordinates.positionInWindow().y.roundToInt()
      }
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .padding(horizontal = 16.dp)
          .onGloballyPositioned { coordinates ->
            val y = coordinates.positionInWindow().y
            headerBottomPx = (y + coordinates.size.height).roundToInt()
          },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.weight(1f)
        ) {
          Image(
            imageVector = getCompanionChessIcon(),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
          )

          Text(
            text = stringResource(R.string.gamegenie_companion_master_fav_games),
            style = AGTypography.Chat,
            color = Palette.Primary,
            modifier = Modifier.width(80.dp)
          )

          Box(
            modifier = Modifier
              .weight(1f)
              .height(32.dp)
              .clipToBounds()
              .onSizeChanged { size: IntSize ->
                stripWidthPx = size.width
              },
            contentAlignment = Alignment.CenterStart
          ) {
            androidx.compose.animation.AnimatedVisibility(
              visible = !isExpanded,
              enter = slideInHorizontally { it },
              exit = slideOutHorizontally { it }
            ) {
              LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically
              ) {
                items(games) { game ->
                  GameCompanionIcon(
                    game = game,
                    onClick = onGameClick,
                    imageSize = 32,
                    showAnimation = false,
                    showBorder = false,
                    showImage = true,
                    clickableEnabled = true,
                    showText = false
                  )
                }
              }
            }
            if (shouldShowShadow) {
              Box(
                modifier = Modifier
                  .align(Alignment.CenterEnd)
                  .width(24.dp)
                  .fillMaxHeight()
                  .background(
                    Brush.horizontalGradient(
                      colors = listOf(Color.Transparent, Palette.Black, Palette.Black),
                      startX = 0f,
                      endX = 100f
                    )
                  )
              )
            }
          }
        }

        Icon(
          imageVector = if (isExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
          contentDescription = if (isExpanded) "Collapse" else "Expand",
          tint = Palette.Primary,
          modifier = Modifier
            .size(32.dp)
            .clickable { isExpanded = !isExpanded }
        )
      }

      AnimatedVisibility(
        visible = !isExpanded,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Palette.Primary)
        )
      }
    }

    val dropdownVisibleState = remember { MutableTransitionState(false) }
    dropdownVisibleState.targetState = isExpanded

    if (dropdownVisibleState.currentState || dropdownVisibleState.targetState || dropdownVisibleState.isIdle.not()) {
      Popup(
        alignment = Alignment.TopStart,
        offset = IntOffset(0, (headerBottomPx - containerTopPx).coerceAtLeast(0)),
        properties = PopupProperties(
          focusable = isExpanded,
          dismissOnBackPress = true,
          dismissOnClickOutside = true
        ),
        onDismissRequest = { isExpanded = false }
      ) {
        AnimatedVisibility(
          visibleState = dropdownVisibleState,
          enter = expandVertically(),
          exit = shrinkVertically()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .background(Palette.Black)
          ) {
            CompanionDropDown(
              games = games,
              onGameClick = { game ->
                onGameClick(game)
                isExpanded = false
              },
              modifier = Modifier.fillMaxWidth()
            )
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Palette.Primary)
            )
          }
        }
      }
    }
  }
}
