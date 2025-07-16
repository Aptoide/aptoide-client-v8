package com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.level_badges.getLevelEightBadge
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.level_badges.getLevelFiveBadge
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.level_badges.getLevelFourBadge
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.level_badges.getLevelNineBadge
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.level_badges.getLevelOneBadge
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.level_badges.getLevelSevenBadge
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.level_badges.getLevelSixBadge
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.level_badges.getLevelTenBadge
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.level_badges.getLevelThreeBadge
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.level_badges.getLevelTwoBadge
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.PaESmallTextButton
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import kotlin.random.Random
import kotlin.random.nextInt

@Composable
fun PaEKnowMoreCard(
  currentLevel: Int,
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
) {
  Box(
    modifier = modifier.width(330.dp)
  ) {

    Box(
      modifier = Modifier
        .padding(top = 30.dp)
        .matchParentSize()
        .background(Palette.GreyDark)
        .border(width = 2.dp, color = Color(0xFF676D89))
        .align(Alignment.BottomCenter)
    )


    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .padding(bottom = 24.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      getCurrentLevelBadge(currentLevel)?.let {
        Image(
          modifier = Modifier.size(102.dp, 60.dp),
          imageVector = it,
          contentDescription = null
        )
      }

      Text(
        text = "Level $currentLevel achieved!",
        style = AGTypography.InputsL,
        color = Palette.White
      )

      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "The more you play, the more you earn! Check your rewards and see your balance grow.",
          style = AGTypography.Body,
          color = Palette.White,
          textAlign = TextAlign.Center
        )
      }

      PaESmallTextButton(
        title = "Know More",
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}

private fun getCurrentLevelBadge(level: Int): ImageVector? = when (level) {
  1 -> getLevelOneBadge()
  2 -> getLevelTwoBadge()
  3 -> getLevelThreeBadge()
  4 -> getLevelFourBadge()
  5 -> getLevelFiveBadge()
  6 -> getLevelSixBadge()
  7 -> getLevelSevenBadge()
  8 -> getLevelEightBadge()
  9 -> getLevelNineBadge()
  10 -> getLevelTenBadge()
  else -> null
}

@Preview
@Composable
private fun PaEKnowMoreCardPreview() {
  PaEKnowMoreCard(
    onClick = {},
    currentLevel = Random.nextInt(1..10)
  )
}
