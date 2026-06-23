package com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards

import cm.aptoide.pt.play_and_earn.events.data.EventsRepository
import cm.aptoide.pt.play_and_earn.events.domain.EventType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

data class PendingPaEReward(
  val paERewardType: PaERewardType,
  val rewardAmount: String,
)

/** Backend's view of the user's reward: either unclaimed (with the reward data) or claimed. */
sealed interface RewardState {
  data class Unclaimed(val reward: PendingPaEReward) : RewardState
  data object Claimed : RewardState
}

@Singleton
class SignInRewardRepository @Inject constructor(
  private val eventsRepository: EventsRepository,
) {

  // TODO: swap for a real reward fetched from the backend once available.
  private val defaultReward = PendingPaEReward(
    paERewardType = PaERewardType.ROBUX,
    rewardAmount = PAE_DEFAULT_REWARD_AMOUNT,
  )

  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

  private val _rewardState = MutableStateFlow<RewardState>(RewardState.Unclaimed(defaultReward))
  val rewardState: StateFlow<RewardState> = _rewardState.asStateFlow()

  private val _claimSuccessEvent = MutableSharedFlow<PendingPaEReward>()
  val claimSuccessEvent: SharedFlow<PendingPaEReward> = _claimSuccessEvent.asSharedFlow()

  fun claimReward(reward: PendingPaEReward) {
    scope.launch {
      // Register the sign in reward claim as a Play & Earn event. Optimistic write for now.
      eventsRepository.submitEvent(
        eventType = EventType.FIRST_SIGN_IN,
      )

      _rewardState.value = RewardState.Claimed
      _claimSuccessEvent.emit(reward)
    }
  }
}
