package com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards

import cm.aptoide.pt.campaigns.data.PaEMissionsRepository
import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.campaigns.domain.PaEMissionStatus
import cm.aptoide.pt.play_and_earn.events.data.EventsRepository
import cm.aptoide.pt.play_and_earn.events.domain.EventType
import cm.aptoide.pt.play_and_earn.exchange.domain.GetExchangeRateUseCase
import com.aptoide.android.aptoidegames.play_and_earn.WalletUnitsRefresher
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
import java.math.RoundingMode
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
  private val paeMissionsRepository: PaEMissionsRepository,
  private val getExchangeRateUseCase: GetExchangeRateUseCase,
  private val walletUnitsRefresher: WalletUnitsRefresher,
) {

  // Placeholder shown until the backend FIRST_SIGN_IN mission resolves; also the fallback if it fails.
  private val defaultReward = PendingPaEReward(
    paERewardType = PaERewardType.ROBUX,
    rewardAmount = PAE_DEFAULT_REWARD_AMOUNT,
  )

  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

  private val _rewardState = MutableStateFlow<RewardState>(RewardState.Unclaimed(defaultReward))
  val rewardState: StateFlow<RewardState> = _rewardState.asStateFlow()

  private val _claimSuccessEvent = MutableSharedFlow<PendingPaEReward>()
  val claimSuccessEvent: SharedFlow<PendingPaEReward> = _claimSuccessEvent.asSharedFlow()

  init {
    refreshSignInReward()
  }

  // Fetches the FIRST_SIGN_IN event mission and refines the reward amount (from its units, via the
  // exchange rate) and claimed state (from its progress). Keeps the default reward on failure.
  private fun refreshSignInReward() {
    scope.launch {
      val mission = paeMissionsRepository.getEventMissions().getOrNull()
        ?.firstOrNull { it.eventType() == EventType.FIRST_SIGN_IN }
        ?: return@launch

      _rewardState.value = if (mission.progress?.status == PaEMissionStatus.COMPLETED) {
        RewardState.Claimed
      } else {
        RewardState.Unclaimed(
          PendingPaEReward(
            paERewardType = PaERewardType.ROBUX,
            rewardAmount = formatRewardAmount(mission.units),
          )
        )
      }
    }
  }

  // Converts reward units to their monetary value via the exchange rate, falling back to the
  // default amount when the rate is unavailable or non-positive.
  private suspend fun formatRewardAmount(units: Int): String {
    val money = getExchangeRateUseCase().getOrNull()?.moneyValueOf(units.toLong())
    return if (money != null && money.signum() > 0) {
      "$" + money.setScale(2, RoundingMode.HALF_UP).toPlainString()
    } else {
      PAE_DEFAULT_REWARD_AMOUNT
    }
  }

  fun claimReward(reward: PendingPaEReward) {
    scope.launch {
      // Register the sign in reward claim as a Play & Earn event. Optimistic write for now.
      eventsRepository.submitEvent(
        eventType = EventType.FIRST_SIGN_IN,
      )
      // Claiming grants units on the backend — refresh the toolbar balance.
      walletUnitsRefresher.invalidate()

      _rewardState.value = RewardState.Claimed
      _claimSuccessEvent.emit(reward)
    }
  }
}

// Reads the `event_type` argument of an event mission, mapping it to a known EventType (or null).
private fun PaEMission.eventType(): EventType? =
  arguments.get("event_type")
    ?.takeIf { !it.isJsonNull }
    ?.asString
    ?.let { runCatching { EventType.valueOf(it) }.getOrNull() }
