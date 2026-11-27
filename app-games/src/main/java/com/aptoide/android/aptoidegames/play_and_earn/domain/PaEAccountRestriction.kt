package com.aptoide.android.aptoidegames.play_and_earn.domain

import android.os.SystemClock
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusal
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalCode
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalEvents
import cm.aptoide.pt.play_and_earn.sessions.data.PaEAccountStatusRepository
import cm.aptoide.pt.wallet.datastore.WalletCoreDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Singleton

/** What is known about the signed-in account's standing (AND-951). */
enum class PaEAccountRestrictionState {
  /** Not asked yet, or asked and not answered yet, or nobody is signed in. */
  UNKNOWN,

  /** The backend restricted the account for fraud: Play & Earn shows the restricted screen. */
  RESTRICTED,
  NOT_RESTRICTED,
}

/**
 * Whether the signed-in account is restricted for fraud (AND-951): the one answer every Play &
 * Earn screen follows (see `PaERestrictedGate`). In memory only.
 *
 * - Asked of the backend ([PaEAccountStatusRepository]) when a Play & Earn screen opens
 *   ([refresh]), at most once every [REFRESH_INTERVAL_MS]: a banned user tapping around does not
 *   hammer the backend, and an account support has un-banned gets in again within the minute.
 *   Any answer replaces the last, so "not restricted" lifts a restriction.
 * - Also learnt from any call the backend refused with [PaERefusalCode.ACCOUNT_RESTRICTED]
 *   (session start, heartbeat, redeem, sign-in bonus, balance, exchange rate), all of which are
 *   told through [PaERefusalEvents].
 * - Forgotten when the account changes ([forget]); never asked nor set for a signed-out user.
 */
@Singleton
class PaEAccountRestriction internal constructor(
  private val accountStatus: PaEAccountStatusRepository,
  private val isSignedIn: suspend () -> Boolean,
  refusals: Flow<PaERefusal>,
  private val nowMillis: () -> Long,
  private val scope: CoroutineScope,
) {

  @Inject
  constructor(
    accountStatus: PaEAccountStatusRepository,
    walletCoreDataSource: WalletCoreDataSource,
  ) : this(
    accountStatus = accountStatus,
    isSignedIn = { walletCoreDataSource.getCurrentWalletAddress() != null },
    refusals = PaERefusalEvents.refusals,
    nowMillis = SystemClock::elapsedRealtime,
    scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate),
  )

  private val _state = MutableStateFlow(PaEAccountRestrictionState.UNKNOWN)
  val state: StateFlow<PaEAccountRestrictionState> = _state.asStateFlow()

  // Bumped when the account changes: an answer or refusal for the last account is not this one's.
  private val accountGeneration = AtomicInteger()

  // Touched on the main thread only, as is every caller.
  private var refreshJob: Job? = null
  private var lastAskedAtMillis: Long? = null

  init {
    scope.launch {
      refusals.collect { refusal ->
        if (refusal.code == PaERefusalCode.ACCOUNT_RESTRICTED) onAccountRefusal()
      }
    }
  }

  /** A Play & Earn screen opened: asks the backend, unless it was asked within the minute. */
  fun refresh() {
    if (refreshJob?.isActive == true) return
    val now = nowMillis()
    val lastAskedAt = lastAskedAtMillis
    if (lastAskedAt != null && now - lastAskedAt < REFRESH_INTERVAL_MS) return
    val generation = accountGeneration.get()
    refreshJob = scope.launch {
      // Signed out: nothing to ask, and nothing counted, so signing in asks at once.
      if (!isSignedIn()) return@launch
      lastAskedAtMillis = now
      val restricted = accountStatus.isRestricted()
      if (generation != accountGeneration.get()) return@launch
      _state.value = if (restricted) {
        PaEAccountRestrictionState.RESTRICTED
      } else {
        PaEAccountRestrictionState.NOT_RESTRICTED
      }
    }
  }

  /**
   * The account changed (sign-in or sign-out): what was known belongs to the last one. The next
   * screen asks about the new account at once.
   */
  fun forget() {
    accountGeneration.incrementAndGet()
    refreshJob?.cancel()
    refreshJob = null
    lastAskedAtMillis = null
    _state.value = PaEAccountRestrictionState.UNKNOWN
  }

  private suspend fun onAccountRefusal() {
    val generation = accountGeneration.get()
    if (!isSignedIn() || generation != accountGeneration.get()) return
    _state.value = PaEAccountRestrictionState.RESTRICTED
  }

  companion object {
    const val REFRESH_INTERVAL_MS = 60_000L
  }
}
