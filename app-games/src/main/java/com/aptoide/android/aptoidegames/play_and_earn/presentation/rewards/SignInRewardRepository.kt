package com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards

import cm.aptoide.pt.aptoide_network.play_and_earn.toPaERefusal
import cm.aptoide.pt.campaigns.data.PaEMissionsRepository
import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.campaigns.domain.PaEMissionStatus
import cm.aptoide.pt.play_and_earn.events.data.EventAlreadySubmittedException
import cm.aptoide.pt.play_and_earn.events.data.EventsRepository
import cm.aptoide.pt.play_and_earn.events.domain.EventType
import cm.aptoide.pt.play_and_earn.exchange.domain.GetExchangeRateUseCase
import com.aptoide.android.aptoidegames.play_and_earn.WalletUnitsRefresher
import com.aptoide.android.aptoidegames.play_and_earn.data.PaESignInBonusRefusal
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
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import timber.log.Timber
import java.math.RoundingMode
import javax.inject.Inject
import javax.inject.Singleton

data class PendingPaEReward(
  val paERewardType: PaERewardType,
  val rewardAmount: String,
  val units: Int,
)

/**
 * Backend's view of the user's reward: unknown until fetched, unclaimed (with data), claimed or
 * refused.
 */
sealed interface RewardState {
  /** Mission state not resolved yet (fetch in flight or failed) — offer surfaces stay hidden. */
  data object Loading : RewardState
  data class Unclaimed(val reward: PendingPaEReward) : RewardState
  data object Claimed : RewardState

  /**
   * The backend refused the bonus for a reason retrying cannot fix (AND-953): a restricted
   * account, an unsupported region or device, or an outdated app. Offer surfaces hide it, as if
   * there were none, until the refusal expires ([PaESignInBonusRefusal]) or the account changes.
   */
  data object Refused : RewardState
}

@Singleton
class SignInRewardRepository internal constructor(
  private val eventsRepository: EventsRepository,
  private val paeMissionsRepository: PaEMissionsRepository,
  private val getExchangeRateUseCase: GetExchangeRateUseCase,
  private val walletUnitsRefresher: WalletUnitsRefresher,
  private val bonusRefusal: PaESignInBonusRefusal,
  private val scope: CoroutineScope,
) {

  @Inject
  constructor(
    eventsRepository: EventsRepository,
    paeMissionsRepository: PaEMissionsRepository,
    getExchangeRateUseCase: GetExchangeRateUseCase,
    walletUnitsRefresher: WalletUnitsRefresher,
    bonusRefusal: PaESignInBonusRefusal,
  ) : this(
    eventsRepository = eventsRepository,
    paeMissionsRepository = paeMissionsRepository,
    getExchangeRateUseCase = getExchangeRateUseCase,
    walletUnitsRefresher = walletUnitsRefresher,
    bonusRefusal = bonusRefusal,
    scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate),
  )

  private val _rewardState = MutableStateFlow<RewardState>(RewardState.Loading)
  val rewardState: StateFlow<RewardState> = _rewardState.asStateFlow()

  private val _claimSuccessEvent = MutableSharedFlow<PendingPaEReward>()
  val claimSuccessEvent: SharedFlow<PendingPaEReward> = _claimSuccessEvent.asSharedFlow()

  private var refreshJob: Job? = null

  init {
    refresh()
    // The account changed (sign-in or sign-out cleared the refusal): offer the bonus again.
    scope.launch {
      bonusRefusal.refusedAt.filter { it == null }.collect {
        if (_rewardState.value == RewardState.Refused) refresh()
      }
    }
  }

  /**
   * Fetches the FIRST_SIGN_IN event mission and resolves the reward amount (from its units, via
   * the exchange rate) and claimed state (from its progress). Failures keep the state [Loading] —
   * offer surfaces call this again on entry, so a failed startup fetch gets retried. Once resolved
   * the state is never re-fetched: the missions endpoint doesn't report claimed progress yet, so a
   * re-fetch could regress an in-session Claimed back to Unclaimed.
   *
   * A refused bonus (AND-953) is not fetched while its refusal holds; once it has expired or was
   * cleared, this fetches it again as from the start.
   */
  fun refresh() {
    val state = _rewardState.value
    if (state != RewardState.Loading && state != RewardState.Refused) return
    if (refreshJob?.isActive == true) return
    refreshJob = scope.launch {
      if (isRefused()) {
        _rewardState.value = RewardState.Refused
        return@launch
      }
      _rewardState.value = RewardState.Loading
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
      // A refused bonus is not asked for again while the refusal holds.
      if (isRefused()) {
        _rewardState.value = RewardState.Refused
        return@launch
      }
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
        // state Unclaimed so the offer remains claimable (retry), except a refusal that blocks
        // Play & Earn (AND-953): retrying cannot help, so the offer ends for a while.
        if (error is EventAlreadySubmittedException) {
          _rewardState.value = RewardState.Claimed
        } else if (error.toPaERefusal()?.code?.blocksPlay == true) {
          _rewardState.value = RewardState.Refused
          runCatching { bonusRefusal.record() }
            .onFailure { Timber.w(it, "Couldn't remember the sign-in bonus refusal") }
        }
      }
    }
  }

  // A store that cannot be read refuses nothing: the bonus is offered and asked for as before.
  private suspend fun isRefused(): Boolean = runCatching { bonusRefusal.isRefused() }
    .onFailure { Timber.w(it, "Couldn't read the sign-in bonus refusal") }
    .getOrDefault(false)
}

// Reads the `event_type` argument of an event mission, mapping it to a known EventType (or null).
private fun PaEMission.eventType(): EventType? =
  arguments.get("event_type")
    ?.takeIf { !it.isJsonNull }
    ?.asString
    ?.let { runCatching { EventType.valueOf(it) }.getOrNull() }
