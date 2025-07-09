package com.aptoide.android.aptoidegames.play_and_earn.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.drawables.icons.getForward
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getPaESmallLogo
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun PaEBundleHeader(
  onClick: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Image(
          imageVector = getPaESmallLogo(),
          contentDescription = null,
        )

        Text(
          text = "Play & Earn",
          style = AGTypography.Title,
          color = Palette.White
        )
      }

      Row(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(
          text = "Earning Now",
          style = AGTypography.InputsM,
          color = Palette.White
        )

        Image(
          modifier = Modifier.size(18.dp),
          imageVector = getForward(Palette.White, Palette.Secondary),
          contentDescription = null,
        )
      }
    }

    Text(
      text = "Earn rewards by playing new games!",
      style = AGTypography.InputsM,
      color = Palette.White
    )
  }
}