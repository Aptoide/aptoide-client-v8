package com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getDiamondShine
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.PaESectionHeader
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun PaEHowItWorksSection() {
  val scrollState = rememberScrollState()
  Column(
    modifier = Modifier.padding(all = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    PaESectionHeader(
      icon = getDiamondShine(),
      text = stringResource(R.string.play_and_earn_how_it_works_title),
    )

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(scrollState),
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      PaEHowItWorksCard(
        text = stringResource(R.string.play_and_earn_pick_featured_game_body),
        iconRes = R.drawable.sword
      )
      PaEHowItWorksCard(
        text = stringResource(R.string.play_and_earn_xp_contributes_checkpoints_body),
        iconRes = R.drawable.map
      )
      PaEHowItWorksCard(
        text = stringResource(R.string.play_and_earn_unlock_rewards_convert_balance_body),
        iconRes = R.drawable.chest
      )
      PaEHowItWorksCard(
        text = stringResource(R.string.play_and_earn_choose_game_start_playing_body),
        iconRes = R.drawable.potion
      )
    }
  }
}

@Composable
fun PaEHowItWorksCard(
  text: String,
  iconRes: Int
) {
  Box(
    modifier = Modifier
      .size(height = 176.dp, width = 144.dp)
      .background(Palette.Secondary),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(all = 16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Image(
        modifier = Modifier.size(88.dp),
        painter = painterResource(iconRes),
        contentDescription = null
      )
      Text(
        text = text,
        style = AGTypography.InputsM,
        color = Palette.White,
        textAlign = TextAlign.Center
      )
    }
  }
}

@Preview
@Composable
private fun PaEHowItWorksSectionPreview() {
  PaEHowItWorksSection()
}
