package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Preview
@Composable
fun PlayInGameOverlayTooltip() = Box(
  contentAlignment = Alignment.Center
) {
  Image(
    painter = painterResource(R.drawable.play_overlay_tooltip),
    contentDescription = "Tooltip overlay",
    modifier = Modifier
      .size(width = 161.dp, height = 48.dp)
      .alpha(0.8f)
  )
  Box {
    Text(
      text = stringResource(R.string.gamegenie_overlay_tooltip),
      style = AGTypography.ChatBold,
      color = Palette.White,
      modifier = Modifier
        .offset(y = 4.dp)
    )  }
}
