package com.aptoide.android.aptoidegames.play_and_earn.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.design_system.AGContentButton
import com.aptoide.android.aptoidegames.design_system.AccentButton
import com.aptoide.android.aptoidegames.design_system.AccentSmallButton
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getSmallCoinIcon
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

@Preview(
  showSystemUi = true,
  uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun AGButtonPreview() {
  AptoideTheme {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      PaESmallCoinButton(title = "Install", onClick = {})
      PaELargeCoinButton(title = "Install", onClick = {})
      PaESmallTextButton(title = "Install", onClick = {})
      PaELargeTextButton(title = "Install", onClick = {})
    }
  }
}

@Composable
fun PaELargeCoinButton(
  title: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
) = AGContentButton(
  onClick = onClick,
  modifier = modifier.height(48.dp),
  enabled = enabled,
  color = Palette.Secondary,
) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = title,
      maxLines = 1,
      textAlign = TextAlign.Center,
      style = AGTypography.InputsL,
      color = Palette.White
    )
    Icon(
      imageVector = getSmallCoinIcon(),
      contentDescription = null,
      modifier = Modifier.size(12.dp, 14.dp),
      tint = Color.Unspecified
    )
  }
}

@Composable
fun PaESmallCoinButton(
  title: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
) = AGContentButton(
  onClick = onClick,
  modifier = modifier.height(32.dp),
  enabled = enabled,
  color = Palette.Secondary,
) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = title,
      maxLines = 1,
      textAlign = TextAlign.Center,
      style = AGTypography.InputsS,
      color = Palette.White
    )
    Icon(
      imageVector = getSmallCoinIcon(),
      contentDescription = null,
      tint = Color.Unspecified
    )
  }
}

@Composable
fun PaESmallTextButton(
  title: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
) = AccentSmallButton(
  onClick = onClick,
  modifier = modifier,
  enabled = enabled,
  title = title,
)

@Composable
fun PaELargeTextButton(
  title: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
) = AccentButton(
  onClick = onClick,
  modifier = modifier,
  enabled = enabled,
  title = title,
)
