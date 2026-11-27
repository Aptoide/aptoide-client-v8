package com.aptoide.android.aptoidegames.play_and_earn.domain

import android.os.SystemClock
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusal
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalCode
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalEvents
import cm.aptoide.pt.play_and_earn.sessions.data.PaEAccountStatus
import cm.aptoide.pt.play_and_earn.sessions.data.PaEAccountStatusRepository
import cm.aptoide.pt.wallet.datastore.WalletCoreDataSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Singleton

/** What is known about the signed-in account's standing (AND-951). */
enum class PaEAccountRestrictionState {
  /** Not asked yet, asked and not answered, the backend could not tell, or nobody signed in. */
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
 *   A clear answer replaces the last, so "not restricted" lifts a restriction. A call that could
 *   not tell changes nothing, and may be tried again after [RETRY_AFTER_FAILURE_MS].
 * - Also learnt from any call the backend refused with [PaERefusalCode.ACCOUNT_RESTRICTED]
 *   (session start, heartbeat, redeem, sign-in bonus, balance, exchange rate), all of which are
 *   told through [PaERefusalEvents]. The backend is then asked at once: its answer about the
 *   current account is the authority (a refusal may still belong to the last account), and
 *   when it cannot tell, the refusal's word stands.
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
    scope = CoroutineScope(
      SupervisorJob() + Dispatchers.Main.immediate + CoroutineExceptionHandler { _, e ->
        Timber.w(e, "Play & Earn account restriction failed")
      }
    ),
  )

  private val _state = MutableStateFlow(PaEAccountRestrictionState.UNKNOWN)
  val state: StateFlow<PaEAccountRestrictionState> = _state.asStateFlow()

  private val _settled = MutableStateFlow(false)

  /**
   * True once something was heard about the current account: a status call ended, whatever it
   * answered, or a refusal said it is restricted. A screen that must not show anything before
   * the answer waits for this, and does not wait for ever when the backend cannot tell.
   */
  val settled: StateFlow<Boolean> = _settled.asStateFlow()

  // Bumped when the account changes: a refusal handled across that change is not this account's.
  private val accountGeneration = AtomicInteger()

  // The status call whose answer counts: bumped by each new call and by an account change, so
  // an answer that arrives after either is ignored, even if it could not be cancelled.
  // Touched on the main thread only, as is every caller, like the two below.
  private var latestRequest = 0
  private var refreshJob: Job? = null
  private var nextAskAtMillis: Long? = null

  init {
    scope.launch {
      refusals.collect { refusal ->
        try {
          if (refusal.code == PaERefusalCode.ACCOUNT_RESTRICTED) onAccountRefusal()
        } catch (e: CancellationException) {
          throw e
        } catch (e: Exception) {
          // One refusal that could not be handled must not stop the next being heard.
          Timber.w(e, "Couldn't handle a Play & Earn account refusal")
        }
      }
    }
  }

  /**
   * A Play & Earn screen opened: asks the backend, unless it was asked within the minute (or,
   * after a call that could not tell, within [RETRY_AFTER_FAILURE_MS]) or is being asked now.
   */
  fun refresh() = ask(force = false)

  /**
   * The account changed (sign-in or sign-out): what was known belongs to the last one. The next
   * screen asks about the new account at once.
   */
  fun forget() {
    accountGeneration.incrementAndGet()
    latestRequest++
    refreshJob?.cancel()
    refreshJob = null
    nextAskAtMillis = null
    _state.value = PaEAccountRestrictionState.UNKNOWN
    _settled.value = false
  }

  /** @param force ask now, whatever was asked before; a call on its way no longer counts. */
  private fun ask(force: Boolean) {
    if (!force) {
      if (refreshJob?.isActive == true) return
      val nextAskAt = nextAskAtMillis
      if (nextAskAt != null && nowMillis() < nextAskAt) return
    }
    refreshJob?.cancel()
    val request = ++latestRequest
    refreshJob = scope.launch {
      // Signed out: nothing to ask, and nothing counted, so signing in asks at once.
      if (!signedIn() || request != latestRequest) return@launch
      nextAskAtMillis = nowMillis() + REFRESH_INTERVAL_MS
      val status = accountStatus.getStatus()
      if (request != latestRequest) return@launch
      when (status) {
        PaEAccountStatus.RESTRICTED -> _state.value = PaEAccountRestrictionState.RESTRICTED
        PaEAccountStatus.NOT_RESTRICTED ->
          _state.value = PaEAccountRestrictionState.NOT_RESTRICTED
        // What was known stays known; the next screen may try again soon.
        PaEAccountStatus.UNKNOWN -> nextAskAtMillis = nowMillis() + RETRY_AFTER_FAILURE_MS
      }
      _settled.value = true
    }
  }

  private suspend fun onAccountRefusal() {
    val generation = accountGeneration.get()
    if (!signedIn() || generation != accountGeneration.get()) return
    _state.value = PaEAccountRestrictionState.RESTRICTED
    _settled.value = true
    ask(force = true)
  }

  // A wallet that cannot be read is no one signed in: nothing is asked nor restricted.
  private suspend fun signedIn(): Boolean = try {
    isSignedIn()
  } catch (e: CancellationException) {
    throw e
  } catch (e: Exception) {
    Timber.w(e, "Couldn't read whether someone is signed in")
    false
  }

  companion object {
    const val REFRESH_INTERVAL_MS = 60_000L
    const val RETRY_AFTER_FAILURE_MS = 10_000L
  }
}
