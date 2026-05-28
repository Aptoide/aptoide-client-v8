package com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.toAnnotatedString
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.animations.RewardsStarsAnimation
import com.aptoide.android.aptoidegames.play_and_earn.presentation.unit_exchange_flow.exchangeDisplayName
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

private val CardWidth = 136.dp

@Composable
fun ClaimableRewardCard(
  rewardType: PaERewardType,
  formattedAmount: String,
  onCollectClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier.width(CardWidth),
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Box(
      modifier = Modifier
        .size(CardWidth)
        .background(Palette.Secondary),
      contentAlignment = Alignment.Center,
    ) {
      Image(
        painter = painterResource(R.drawable.roblox_feature_graphic),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }),
        alpha = 0.2f,
        modifier = Modifier.matchParentSize(),
      )
      Image(
        painter = painterResource(rewardType.iconRes),
        contentDescription = null,
        modifier = Modifier.size(72.dp),
      )
      Box(
        modifier = Modifier
          .size(0.dp)
          .wrapContentSize(unbounded = true)
      ) {
        RewardsStarsAnimation(modifier = Modifier.width(CardWidth))
      }
    }

    val message = stringResource(
      id = R.string.play_and_earn_reward_earned_message,
      formattedAmount,
      stringResource(rewardType.displayNameRes),
    ).toAnnotatedString(
      AGTypography.InputsL.toSpanStyle().copy(color = Palette.Yellow100)
    )
    Text(
      text = message,
      style = AGTypography.InputsL,
      color = Palette.White,
    )

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(Palette.Secondary)
        .clickable(onClick = onCollectClick)
        .padding(horizontal = 16.dp, vertical = 9.dp),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = stringResource(R.string.play_and_earn_tap_to_collect),
        style = AGTypography.InputsS,
        color = Palette.White,
      )
    }
  }
}

@Preview
@Composable
private fun ClaimableRewardCardPreview(
  @PreviewParameter(PaERewardTypeProvider::class) rewardType: PaERewardType,
) {
  ClaimableRewardCard(
    rewardType = rewardType,
    formattedAmount = "$0.50",
    onCollectClick = {},
  )
}
