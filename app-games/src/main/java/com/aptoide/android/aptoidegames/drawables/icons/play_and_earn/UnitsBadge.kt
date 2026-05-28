package com.aptoide.android.aptoidegames.drawables.icons.play_and_earn

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun UnitsBadge(units: String) {
  Row(
    modifier = Modifier
      .height(26.dp)
      .border(2.dp, Palette.Yellow100)
      .background(Palette.Yellow100.copy(alpha = 0.1f))
      .padding(PaddingValues(start = 6.dp, end = 8.dp)),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    Image(
      imageVector = getSmallCoinIcon(),
      contentDescription = null
    )
    Text(
      text = units,
      style = AGTypography.InputsS,
      color = Palette.Yellow100
    )
  }
}

@Preview
@Composable
private fun UnitsBadgePreview() {
  UnitsBadge(units = "310")
}
