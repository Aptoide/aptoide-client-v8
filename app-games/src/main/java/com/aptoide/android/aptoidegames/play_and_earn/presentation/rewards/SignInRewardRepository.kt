package com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards

import cm.aptoide.pt.campaigns.data.PaEMissionsRepository
import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.campaigns.domain.PaEMissionStatus
import cm.aptoide.pt.play_and_earn.events.data.EventAlreadySubmittedException
import cm.aptoide.pt.play_and_earn.events.data.EventsRepository
import cm.aptoide.pt.play_and_earn.events.domain.EventType
import cm.aptoide.pt.play_and_earn.exchange.domain.GetExchangeRateUseCase
import com.aptoide.android.aptoidegames.play_and_earn.WalletUnitsRefresher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
  val units: Int,
)

/** Backend's view of the user's reward: unknown until fetched, unclaimed (with data) or claimed. */
sealed interface RewardState {
  /** Mission state not resolved yet (fetch in flight or failed) — offer surfaces stay hidden. */
  data object Loading : RewardState
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

  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

  private val _rewardState = MutableStateFlow<RewardState>(RewardState.Loading)
  val rewardState: StateFlow<RewardState> = _rewardState.asStateFlow()

  private val _claimSuccessEvent = MutableSharedFlow<PendingPaEReward>()
  val claimSuccessEvent: SharedFlow<PendingPaEReward> = _claimSuccessEvent.asSharedFlow()

  private var refreshJob: Job? = null

  init {
    refresh()
  }

  /**
   * Fetches the FIRST_SIGN_IN event mission and resolves the reward amount (from its units, via
   * the exchange rate) and claimed state (from its progress). Failures keep the state [Loading] —
   * offer surfaces call this again on entry, so a failed startup fetch gets retried. Once resolved
   * the state is never re-fetched: the missions endpoint doesn't report claimed progress yet, so a
   * re-fetch could regress an in-session Claimed back to Unclaimed.
   */
  fun refresh() {
    if (_rewardState.value != RewardState.Loading || refreshJob?.isActive == true) return
    refreshJob = scope.launch {
      val mission = paeMissionsRepository.getEventMissions().getOrNull()
        ?.firstOrNull { it.eventType() == EventType.FIRST_SIGN_IN }
        ?: return@launch // stays Loading; retried on the next offer-surface entry

      _rewardState.value = if (mission.progress?.status == PaEMissionStatus.COMPLETED) {
        RewardState.Claimed
      } else {
        RewardState.Unclaimed(
          PendingPaEReward(
            paERewardType = PaERewardType.ROBUX,
            rewardAmount = formatRewardAmount(mission.units),
            units = mission.units,
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
      // Register the sign in reward claim as a Play & Earn event.
      eventsRepository.submitEvent(
        eventType = EventType.FIRST_SIGN_IN,
      ).onSuccess {
        // Claiming grants units on the backend — refresh the toolbar balance.
        walletUnitsRefresher.invalidate()
        _rewardState.value = RewardState.Claimed
        _claimSuccessEvent.emit(reward)
      }.onFailure { error ->
        // 409: the backend already granted this reward (stale local state) — sync to Claimed so
        // every offer surface hides, but without the success dialog. Any other failure keeps the
        // state Unclaimed so the offer remains claimable (retry).
        if (error is EventAlreadySubmittedException) {
          _rewardState.value = RewardState.Claimed
        }
      }
    }
  }
}

// Reads the `event_type` argument of an event mission, mapping it to a known EventType (or null).
private fun PaEMission.eventType(): EventType? =
  arguments.get("event_type")
    ?.takeIf { !it.isJsonNull }
    ?.asString
    ?.let { runCatching { EventType.valueOf(it) }.getOrNull() }
