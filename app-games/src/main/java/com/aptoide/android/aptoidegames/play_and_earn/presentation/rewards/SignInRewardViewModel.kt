package com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SignInRewardViewModel @Inject constructor(
  private val signInRewardRepository: SignInRewardRepository,
) : ViewModel() {

  val rewardState: StateFlow<RewardState> = signInRewardRepository.rewardState

  val claimSuccessEvent: SharedFlow<PendingPaEReward> = signInRewardRepository.claimSuccessEvent

  fun claim(reward: PendingPaEReward) {
    signInRewardRepository.claimReward(reward)
  }
}
