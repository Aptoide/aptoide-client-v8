package com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SignInRewardCard(
  packageName: String?,
  modifier: Modifier = Modifier,
) {
  val rewardType = PaERewardType.fromPackageName(packageName) ?: return
  val viewModel = hiltViewModel<SignInRewardViewModel>()
  val rewardState by viewModel.rewardState.collectAsState()
  val unclaimed = rewardState as? RewardState.Unclaimed ?: return

  ClaimableRewardCard(
    rewardType = rewardType,
    formattedAmount = unclaimed.reward.rewardAmount,
    onCollectClick = { viewModel.claim(unclaimed.reward.copy(paERewardType = rewardType)) },
    modifier = modifier,
  )
}
