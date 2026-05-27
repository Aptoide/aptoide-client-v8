package com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.aptoide.android.aptoidegames.R

@Composable
fun PaERewardHeroArt(
  paERewardType: PaERewardType,
  modifier: Modifier = Modifier,
) {
  val iconRes = when (paERewardType) {
    PaERewardType.ROBUX -> R.drawable.pae_roblox_reward_icon
    PaERewardType.DIAMONDS -> R.drawable.pae_ff_reward_icon
  }
  Box(
    modifier = modifier,
    contentAlignment = Alignment.Center,
  ) {
    Image(
      painter = painterResource(iconRes),
      contentDescription = null,
    )
  }
}
