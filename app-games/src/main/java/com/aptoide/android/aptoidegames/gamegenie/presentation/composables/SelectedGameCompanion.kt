package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.drawables.icons.getLeftArrow
import com.aptoide.android.aptoidegames.gamegenie.domain.GameCompanion
import com.aptoide.android.aptoidegames.theme.Palette
import kotlin.math.roundToInt

@Composable
fun SelectedGameCompanion(
  game: GameCompanion,
  navigateBack: () -> Unit,
  installedGames: List<GameCompanion> = emptyList(),
  isDropdownExpanded: Boolean = false,
  onDropdownToggle: () -> Unit = {},
  onBackButtonBottomPositioned: ((Int) -> Unit)? = null
) {
  val hasMultipleGames = installedGames.size > 1

  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    BackButton(
      onClick = navigateBack,
      onBottomPositioned = onBackButtonBottomPositioned
    )

    GameNameText(
      gameName = game.name,
      modifier = Modifier.weight(1f)
    )

    GameSwitcherSlot(
      game = game,
      hasOtherGames = hasMultipleGames,
      isExpanded = isDropdownExpanded,
      onToggle = onDropdownToggle
    )
  }
}

@Composable
private fun BackButton(
  onClick: () -> Unit,
  onBottomPositioned: ((Int) -> Unit)?
) {
  Image(
    imageVector = getLeftArrow(Palette.Primary, Palette.Black),
    contentDescription = stringResource(id = R.string.button_back_title),
    contentScale = ContentScale.Crop,
    modifier = Modifier
      .padding(vertical = 12.dp)
      .clickable(onClick = onClick)
      .size(32.dp)
      .onGloballyPositioned { coordinates ->
        val bottom = coordinates.positionInWindow().y + coordinates.size.height
        onBottomPositioned?.invoke(bottom.roundToInt())
      }
  )
}

@Composable
private fun GameNameText(
  gameName: String,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .heightIn(min = 56.dp)
      .padding(horizontal = 8.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = gameName,
      color = Palette.White,
      fontSize = 18.sp,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
  }
}

@Composable
private fun GameSwitcherSlot(
  game: GameCompanion,
  hasOtherGames: Boolean,
  isExpanded: Boolean,
  onToggle: () -> Unit
) {
  Box(
    modifier = Modifier
      .padding(vertical = 12.dp)
      .width(52.dp)
      .height(32.dp)
  ) {
    if (hasOtherGames) {
      CompanionGameSwitcherButton(
        game = game,
        isExpanded = isExpanded,
        onClick = onToggle
      )
    } else {
      Box(
        modifier = Modifier
          .size(32.dp)
          .align(Alignment.Center)
      ) {
        GameCompanionIcon(
          game = game,
          imageSize = 32,
          showAnimation = false,
          showBorder = false,
          showImage = true,
          clickableEnabled = false,
          showText = false
        )
      }
    }
  }
}
