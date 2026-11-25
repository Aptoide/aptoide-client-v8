package com.aptoide.android.aptoidegames.play_and_earn.domain.sessions

import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusal
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The one record of the play-blocking refusals Play & Earn sessions got (AND-950): what the
 * Rewards tab shows and what keeps the session manager from asking again.
 *
 * A region refusal holds for its game only (campaigns target countries per game). An account,
 * device or update refusal holds for every game. Only codes that [PaERefusalCode.blocksPlay] are
 * kept. In memory: the app's process forgets them.
 *
 * A refusal is forgotten when a later start of its game succeeds (an every-game one too), when
 * the account changes, and when the tracking service stops for any other reason than a refusal.
 * It stops blocking new starts after [ASK_AGAIN_AFTER_MILLIS], so a user who fixed the cause
 * (travelled, updated the app) is not locked out for as long as the service runs; the Rewards
 * tab keeps showing it until the backend's next answer.
 */
@Singleton
class PaESessionRefusals internal constructor(
  private val nowMillis: () -> Long,
) {

  @Inject
  constructor() : this({ System.currentTimeMillis() })

  private data class Entry(val refusal: PaERefusal, val atMillis: Long)

  private data class State(
    val everyGame: Entry? = null,
    val perGame: Map<String, Entry> = emptyMap(),
  ) {
    fun of(packageName: String): Entry? = everyGame ?: perGame[packageName]
  }

  private val state = MutableStateFlow(State())

  /** The refusal [packageName]'s Rewards tab shows; null when there is none. */
  fun observe(packageName: String): Flow<PaERefusal?> =
    state.map { it.of(packageName)?.refusal }.distinctUntilChanged()

  /** The refusal that still keeps [packageName] from starting a session, if any. */
  fun blockingRefusalOf(packageName: String): PaERefusal? =
    state.value.of(packageName)?.takeIf { it.isRecent() }?.refusal

  /** The account, device or update refusal that still keeps every game from playing, if any. */
  fun everyGameRefusal(): PaERefusal? = state.value.everyGame?.takeIf { it.isRecent() }?.refusal

  fun record(packageName: String, refusal: PaERefusal) {
    if (!refusal.code.blocksPlay) return
    val entry = Entry(refusal, nowMillis())
    state.update {
      if (refusal.code.holdsForEveryGame) {
        it.copy(everyGame = entry)
      } else {
        it.copy(perGame = it.perGame + (packageName to entry))
      }
    }
  }

  /** The backend accepted [packageName]'s session: nothing refused applies to it any more. */
  fun onSessionStarted(packageName: String) =
    state.update { State(everyGame = null, perGame = it.perGame - packageName) }

  /** The tracking service stopped, or the account changed: start over. */
  fun clear() = state.update { State() }

  private fun Entry.isRecent() = nowMillis() - atMillis < ASK_AGAIN_AFTER_MILLIS

  companion object {
    /**
     * How long a refusal keeps a game from asking the backend again. The tracking service polls
     * every 10 seconds while a game is in front; this bounds a refused game to one session start
     * per 10 minutes, and lets a user who fixed the cause play again without restarting the app.
     */
    val ASK_AGAIN_AFTER_MILLIS = TimeUnit.MINUTES.toMillis(10)

    /** Everything but the region is about the account, the device or the app, not the game. */
    private val PaERefusalCode.holdsForEveryGame: Boolean
      get() = this != PaERefusalCode.REGION_NOT_SUPPORTED
  }
}
