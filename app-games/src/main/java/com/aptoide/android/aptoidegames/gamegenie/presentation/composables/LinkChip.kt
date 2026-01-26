package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun LinkChip(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .clip(RoundedCornerShape(50))
      .background(Palette.Black)
      .clickable(onClick = onClick)
  ) {
    Text(
      text = text,
      style = AGTypography.InputsXXS,
      color = Palette.GreyLight,
      modifier = Modifier
        .padding(horizontal = 6.dp, vertical = 4.dp)
    )
  }
}
