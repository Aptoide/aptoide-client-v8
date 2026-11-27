package com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards

import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalCode
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
import kotlinx.coroutines.CancellationException
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
import timber.log.Timber
import java.math.RoundingMode
import javax.inject.Inject
import java.util.concurrent.atomic.AtomicInteger
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
   * account, an unsupported region or device, or an outdated app; or this phone already received
   * it on another account (AND-964). Offer surfaces hide it, as if there were none, until the
   * refusal expires ([PaESignInBonusRefusal]) or the account changes.
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

  private val _refusedEvent = MutableSharedFlow<PaERefusalCode>()

  /**
   * Why a claim the user just made was refused for good (AND-953), once per refusal, so the
   * surface can say so. A bonus found refused later (app restart, another surface) says nothing.
   * Also a VPN (AND-964), which keeps the bonus claimable: the user must know to turn it off.
   */
  val refusedEvent: SharedFlow<PaERefusalCode> = _refusedEvent.asSharedFlow()

  private var refreshJob: Job? = null

  // Bumped when the account changes: a claim answered after that belongs to the last account.
  private val accountGeneration = AtomicInteger()

  init {
    refresh()
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
    load(checkRefusal = true)
  }

  /**
   * The account changed (sign-in or sign-out): the last account's refusal does not hold for the
   * next, so it is forgotten and a refused bonus is offered again. A claim still in flight for
   * the last account can no longer end the bonus.
   */
  suspend fun forgetRefusal() {
    accountGeneration.incrementAndGet()
    softly("forget the sign-in bonus refusal", Unit) { bonusRefusal.clear() }
    if (_rewardState.value == RewardState.Refused) {
      refreshJob?.cancel()
      // The store is not asked again: a clear that failed must not hide the offer.
      load(checkRefusal = false)
    }
  }

  private fun load(checkRefusal: Boolean) {
    refreshJob = scope.launch {
      if (checkRefusal && isRefused()) {
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
    val generation = accountGeneration.get()
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
        // Play & Earn (AND-953) or the device limit (AND-964): retrying cannot help, so the offer
        // ends. A VPN (AND-964) is told but keeps the offer: off, the next tap goes through.
        if (error is EventAlreadySubmittedException) {
          _rewardState.value = RewardState.Claimed
        } else {
          val code = error.toPaERefusal()?.code
          // A refusal for the account that has since signed out or in is not this one's.
          if (code != null && generation == accountGeneration.get()) onRefused(code)
        }
      }
    }
  }

  private suspend fun onRefused(code: PaERefusalCode) {
    when {
      code == PaERefusalCode.VPN_NOT_ALLOWED -> Unit

      code == PaERefusalCode.SIGN_IN_BONUS_DEVICE_LIMIT -> {
        _rewardState.value = RewardState.Refused
        // Not remembered: the offer still ends for this session.
        softly("remember the sign-in bonus device limit", Unit) {
          bonusRefusal.recordDeviceLimit()
        }
      }

      code.blocksPlay -> {
        _rewardState.value = RewardState.Refused
        // Not remembered: the offer still ends for this session.
        softly("remember the sign-in bonus refusal", Unit) { bonusRefusal.record() }
      }

      // A limit (e.g. too many attempts) is a failure like any other: claimable, untold.
      else -> return
    }
    _refusedEvent.emit(code)
  }

  // A store that cannot be read refuses nothing: the bonus is offered and asked for as before.
  private suspend fun isRefused(): Boolean =
    softly("read the sign-in bonus refusal", false) { bonusRefusal.isRefused() }

  // The refusal store is a convenience: when it fails, the claim flow goes on as without it.
  private suspend fun <T> softly(what: String, fallback: T, block: suspend () -> T): T = try {
    block()
  } catch (e: CancellationException) {
    throw e
  } catch (e: Exception) {
    Timber.w(e, "Couldn't $what")
    fallback
  }
}

// Reads the `event_type` argument of an event mission, mapping it to a known EventType (or null).
private fun PaEMission.eventType(): EventType? =
  arguments.get("event_type")
    ?.takeIf { !it.isJsonNull }
    ?.asString
    ?.let { runCatching { EventType.valueOf(it) }.getOrNull() }
