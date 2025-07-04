package com.aptoide.android.aptoidegames.play_and_earn.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.getRandomString
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getDiamondShine
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun PaESectionHeader(
  icon: ImageVector,
  text: String,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Image(
      imageVector = icon,
      contentDescription = null
    )
    Text(
      text = text,
      style = AGTypography.InputsL,
      color = Palette.White
    )
  }
}

@Preview
@Composable
private fun PaESectionHeaderPreview() {
  PaESectionHeader(
    icon = getDiamondShine(),
    text = getRandomString(1..3)
  )
}
