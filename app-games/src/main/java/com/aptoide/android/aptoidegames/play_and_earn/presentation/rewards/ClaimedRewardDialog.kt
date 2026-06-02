package com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.extensions.toAnnotatedString
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.design_system.AccentButton
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getCorrectHexagon
import com.aptoide.android.aptoidegames.home.rememberRewardsDestination
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.animations.RewardsStarsAnimation
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun ClaimedRewardDialog(navigate: (String) -> Unit) {
  val viewModel = hiltViewModel<SignInRewardViewModel>()
  val rewardsDestination = rememberRewardsDestination()
  var activeReward by remember { mutableStateOf<PendingPaEReward?>(null) }
  LaunchedEffect(viewModel) {
    viewModel.claimSuccessEvent.collect { activeReward = it }
  }

  activeReward?.let {
    ClaimRewardDialogContent(
      reward = it,
      onDismiss = { activeReward = null },
      onEarnMore = {
        activeReward = null
        navigate(rewardsDestination)
      },
    )
  }
}

@Composable
private fun ClaimRewardDialogContent(
  reward: PendingPaEReward,
  onDismiss: () -> Unit,
  onEarnMore: () -> Unit,
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false),
  ) {
    Box(
      modifier = Modifier
        .padding(horizontal = 16.dp, vertical = 64.dp)
        .width(328.dp)
        .wrapContentHeight()
        .background(color = Palette.GreyDark),
      contentAlignment = Alignment.Center,
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        PaERewardSuccessArt(paERewardType = reward.paERewardType)
        RewardEarnedText(
          rewardAmount = reward.rewardAmount,
          paERewardType = reward.paERewardType,
        )
        AccentButton(
          modifier = Modifier.fillMaxWidth(),
          onClick = onEarnMore,
          title = stringResource(R.string.play_and_earn_earn_more_button),
        )
      }
    }
  }
}

@Composable
fun PaERewardSuccessArt(
  paERewardType: PaERewardType,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier,
    contentAlignment = Alignment.Center,
  ) {
    Box(contentAlignment = Alignment.Center) {
      Image(
        painter = painterResource(paERewardType.iconRes),
        contentDescription = null,
        modifier = Modifier.size(96.dp, 108.dp)
      )
      Image(
        imageVector = getCorrectHexagon(),
        contentDescription = null,
        modifier = Modifier
          .align(Alignment.TopEnd)
          .size(width = 53.dp, height = 58.dp)
          .offset(x = 20.dp),
      )
    }
    Box(
      modifier = Modifier
        .size(0.dp)
        .wrapContentSize(unbounded = true)
    ) {
      RewardsStarsAnimation(modifier = Modifier.size(208.dp))
    }
  }
}

@Composable
private fun RewardEarnedText(
  rewardAmount: String,
  paERewardType: PaERewardType,
) {
  val template = stringResource(
    R.string.play_and_earn_reward_claim_message,
    rewardAmount,
    stringResource(paERewardType.displayNameRes),
  )
  val annotatedString = template.toAnnotatedString(SpanStyle(color = Palette.Yellow))
  Text(
    modifier = Modifier.fillMaxWidth(),
    text = annotatedString,
    color = Palette.White,
    style = AGTypography.Title,
    textAlign = TextAlign.Center,
  )
}

@Preview
@Composable
private fun ClaimRewardDialogPreview(
  @PreviewParameter(PaERewardTypeProvider::class) paERewardType: PaERewardType,
) {
  AptoideTheme {
    ClaimRewardDialogContent(
      reward = PendingPaEReward(
        paERewardType = paERewardType,
        rewardAmount = "$0.50",
      ),
      onDismiss = {},
      onEarnMore = {},
    )
  }
}
