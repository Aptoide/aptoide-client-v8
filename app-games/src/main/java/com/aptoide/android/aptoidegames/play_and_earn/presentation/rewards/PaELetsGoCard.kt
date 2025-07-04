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
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.design_system.AccentSmallButton
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getStartEarningIcon
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.animations.PaEAnimatedGift
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun PaELetsGoCard(
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
        .border(width = 2.dp, color = Palette.Blue200)
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
        modifier = Modifier.size(89.dp, 64.dp),
        imageVector = getStartEarningIcon(),
        contentDescription = null
      )

      Text(
        text = "Get started now!", //TODO: hardcoded string
        style = AGTypography.InputsL,
        color = Palette.Yellow100
      )

      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        PaEAnimatedGift(
          modifier = Modifier.size(29.dp, 32.dp)
        )

        Text(
          text = "Log in and grant permissions to unlock access to incredible prizes while you play!", //TODO: hardcoded string
          style = AGTypography.Body,
          color = Palette.White
        )
      }

      //TODO: fix button
      AccentSmallButton(
        title = "Let's Go", //TODO: hardcoded string
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
private fun PaELetsGoCardPreview() {
  PaELetsGoCard(
    onLetsGoClick = {}
  )
}
