package com.aptoide.android.aptoidegames.play_and_earn.presentation.app_view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusal
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalCode
import cm.aptoide.pt.extensions.PreviewDark
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

/**
 * Replaces the missions on the game's Rewards tab when the backend refused its session
 * (AND-950): they would not count, and the message says why.
 */
@Composable
fun PaERefusedView(
  message: PaERefusalMessage,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Text(
      text = stringResource(message.title),
      style = AGTypography.Title,
      color = Palette.AccentText,
      textAlign = TextAlign.Center,
    )
    Text(
      text = stringResource(message.body),
      style = AGTypography.DescriptionGames,
      color = Palette.White,
      textAlign = TextAlign.Center,
    )
  }
}

@PreviewDark
@Composable
private fun PaERefusedRegionPreview() {
  AptoideTheme {
    PaERefusedView(PaERefusal(PaERefusalCode.REGION_NOT_SUPPORTED).toMessage())
  }
}

@PreviewDark
@Composable
private fun PaERefusedAccountPreview() {
  AptoideTheme {
    PaERefusedView(PaERefusal(PaERefusalCode.ACCOUNT_RESTRICTED).toMessage())
  }
}
