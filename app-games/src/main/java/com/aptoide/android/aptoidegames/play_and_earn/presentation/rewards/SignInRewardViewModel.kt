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

  // Reward types whose games-feed impression has already been logged, so recomposition / LazyRow
  // item re-entry doesn't re-fire the `shown` event for the same card.
  private val shownLogged = mutableSetOf<PaERewardType>()

  fun claim(reward: PendingPaEReward) {
    signInRewardRepository.claimReward(reward)
  }

  /** Retries the mission fetch if the reward state is still unresolved (no-op otherwise). */
  fun refresh() {
    signInRewardRepository.refresh()
  }

  /** Runs [log] only the first time the card for [rewardType] is shown this session. */
  fun onRewardCardShown(rewardType: PaERewardType, log: () -> Unit) {
    if (shownLogged.add(rewardType)) log()
  }
}
