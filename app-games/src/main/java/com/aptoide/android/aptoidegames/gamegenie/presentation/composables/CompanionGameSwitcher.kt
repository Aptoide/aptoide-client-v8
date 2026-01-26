package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.aptoide.android.aptoidegames.gamegenie.domain.GameCompanion
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun CompanionGameSwitcherButton(
  game: GameCompanion,
  isExpanded: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .width(52.dp)
      .height(32.dp)
      .background(Palette.Primary.copy(alpha = 0.2f))
      .clickable(onClick = onClick),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center
  ) {
    GameCompanionIcon(
      game = game,
      imageSize = 24,
      showAnimation = false,
      showBorder = false,
      showImage = true,
      clickableEnabled = false,
      showText = false,
    )

    Icon(
      imageVector = if (isExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
      contentDescription = if (isExpanded) "Collapse" else "Expand",
      tint = Palette.Primary,
      modifier = Modifier.size(20.dp)
    )
  }
}

@Composable
fun CompanionGameSwitcherDropdown(
  games: List<GameCompanion>,
  onGameClick: (GameCompanion) -> Unit,
  modifier: Modifier = Modifier,
  selectedGame: GameCompanion? = null
) {
  Column(
    modifier = modifier
      .background(Palette.Black)
      .background(Palette.Primary.copy(alpha = 0.2f))
      .padding(12.dp)
  ) {
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      items(games) { game ->
        val isSelected = selectedGame?.packageName == game.packageName
        GameCompanionIcon(
          game = game,
          onClick = onGameClick,
          textStyle = AGTypography.SmallGames.copy(color = Palette.Primary),
          textSize = AGTypography.SmallGames.fontSize,
          imageSize = 48,
          textHeight = 32,
          showAnimation = false,
          showBorder = isSelected
        )
      }
    }
  }
}

@Composable
fun CompanionGameSwitcherExpandableContent(
  isExpanded: Boolean,
  games: List<GameCompanion>,
  onGameClick: (GameCompanion) -> Unit,
  modifier: Modifier = Modifier,
  selectedGame: GameCompanion? = null,
  horizontalPadding: Int = 18
) {
  Box(
    modifier = modifier
      .zIndex(10f)
      .layout { measurable, constraints ->
        val paddingPx = horizontalPadding.dp.roundToPx()
        val screenWidth = constraints.maxWidth + paddingPx * 2
        val fullWidthConstraints = constraints.copy(
          minWidth = screenWidth,
          maxWidth = screenWidth
        )
        val placeable = measurable.measure(fullWidthConstraints)
        layout(constraints.maxWidth, placeable.height) {
          placeable.place(-paddingPx, 0)
        }
      }
  ) {
    AnimatedVisibility(
      visible = isExpanded && games.isNotEmpty(),
      enter = expandVertically(),
      exit = shrinkVertically()
    ) {
      CompanionGameSwitcherDropdown(
        games = games,
        onGameClick = onGameClick,
        selectedGame = selectedGame,
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}
