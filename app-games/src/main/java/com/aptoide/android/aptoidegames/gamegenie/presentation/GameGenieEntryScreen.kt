package com.aptoide.android.aptoidegames.gamegenie.presentation

import androidx.compose.ui.graphics.PathEffect
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.gamegenie.domain.GameCompanion
import com.aptoide.android.aptoidegames.gamegenie.presentation.composables.AnimationComposable
import com.aptoide.android.aptoidegames.gamegenie.presentation.composables.GameCompanionList
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun GameGenieEntryScreen(
  myGames: List<GameCompanion> = emptyList(),
  onChooseGeneral: () -> Unit,
  onChooseCompanion: (GameCompanion) -> Unit,
) {
  BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    val totalHeight = maxHeight
    val spacingTop = 16.dp
    val spacingMiddle = 8.dp
    val spacingBottom = 16.dp

    val availableHeight = totalHeight - spacingTop - spacingMiddle - spacingBottom
    val topBoxHeight = availableHeight / 2
    val bottomBoxMinHeight = availableHeight - topBoxHeight

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
    ) {
      Spacer(modifier = Modifier.height(spacingTop))
      ChooseGeneralBox(
        onClick = onChooseGeneral,
        modifier = Modifier.height(topBoxHeight)
      )
      Spacer(modifier = Modifier.height(spacingMiddle))
      ChooseCompanionBox(
        onClick = onChooseCompanion,
        myGames = myGames,
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(min = bottomBoxMinHeight)
      )
      Spacer(modifier = Modifier.height(spacingBottom))
    }
  }
}

@Composable
fun PlaceholderBox(
  modifier: Modifier = Modifier,
  backgroundColor: Color,
  borderColor: Color,
  gifRes: Int? = null,
  onClick: (() -> Unit)? = null,
  content: @Composable () -> Unit = {},
) {
  Box(
    modifier = modifier
      .padding(start = 22.dp, end = 22.dp)
      .background(backgroundColor.copy(alpha = 0.1f))
      .drawBehind {
        val dash = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        drawRoundRect(
          color = borderColor,
          size = size,
          style = Stroke(
            width = 4f,
            pathEffect = dash
          )
        )
      }
      .then(
        if (onClick != null)
          Modifier.clickable { onClick() }
        else
          Modifier
      ),
      contentAlignment = if (gifRes != null) Alignment.Center else Alignment.TopCenter
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = if (gifRes == null) Arrangement.Top else Arrangement.Center
    ) {
      if (gifRes != null) {
        AnimationComposable(
          resId = gifRes,
          modifier = Modifier.size(50.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = stringResource(R.string.gamegenie_entry_screen_general_section),
          color = Palette.White,
          style = AGTypography.InputsL,
          textAlign = TextAlign.Center,
          modifier = Modifier
            .width(203.dp)
        )
      } else {
        Text(
          text = stringResource(R.string.gamegenie_entry_screen_companion_section),
          color = Palette.White,
          style = AGTypography.InputsL,
          textAlign = TextAlign.Center,
          modifier = Modifier
            .width(277.dp)
            .padding(top = 50.dp)
        )

        content()
      }
    }
  }
}

@Composable
fun ChooseGeneralBox(
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
) {
  PlaceholderBox(
    modifier = modifier,
    backgroundColor = Palette.Primary,
    borderColor = Palette.Primary,
    gifRes = R.raw.game_genie_search_icon,
    onClick = onClick
  )
}

@Composable
fun ChooseCompanionBox(
  modifier: Modifier = Modifier,
  myGames: List<GameCompanion>,
  onClick: (GameCompanion) -> Unit,
) {
  PlaceholderBox(
    modifier = modifier,
    backgroundColor = Palette.Primary,
    borderColor = Palette.Primary,
    onClick = null
  ) {
    GameCompanionList(myGames) {
      onClick(it)
    }
  }
}
