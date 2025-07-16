package com.aptoide.android.aptoidegames.play_and_earn

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.design_system.AccentSmallButton
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getLevelOneIcon
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun PlayAndEarnKnowMoreCard(
  modifier: Modifier = Modifier,
  onLetsGoClick: () -> Unit,
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
      Image(
        imageVector = getLevelOneIcon(),
        contentDescription = null
      )

      Text(
        text = "Level 1 achieved!", //TODO: hardcoded string
        style = AGTypography.InputsL,
        color = Palette.White
      )

      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "The more you play, the more you earn! Check your rewards and see your balance grow.", //TODO: hardcoded string
          style = AGTypography.Body,
          color = Palette.White,
          textAlign = TextAlign.Center
        )
      }

      //TODO: fix button
      AccentSmallButton(
        title = "Know More", //TODO: hardcoded string
        onClick = onLetsGoClick,
        modifier = Modifier
          .fillMaxWidth()
          .requiredHeight(32.dp),
      )
    }
  }
}

@Preview
@Composable
fun PlayAndEarnKnowMoreCardPreview() {
  PlayAndEarnKnowMoreCard(
    onLetsGoClick = {}
  )
}
