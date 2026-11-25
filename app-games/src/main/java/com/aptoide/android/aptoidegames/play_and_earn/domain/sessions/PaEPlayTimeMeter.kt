package com.aptoide.android.aptoidegames.play_and_earn.domain.sessions

import cm.aptoide.pt.usage_stats.PackageUsageState

/**
 * A clock that only moves forward: `SystemClock.elapsedRealtime()` on a device. Never the wall
 * clock, which the user can change.
 */
fun interface PaEMonotonicClock {
  fun elapsedRealtimeMillis(): Long
}

/** Whether the phone is in use. Neither signal needs a permission. */
interface PaEScreenState {
  /** `PowerManager.isInteractive`: the screen is on. False once the screen goes off. */
  val isInteractive: Boolean

  /**
   * `KeyguardManager.isKeyguardLocked`: the lock screen is showing (also when an app is shown
   * over it). False on phones without a lock screen, where a screen-on phone is unlocked anyway.
   */
  val isKeyguardLocked: Boolean
}

/**
 * One poll's result.
 * @property screenInUse the screen is on and unlocked.
 * @property playingPackage the game played at this poll: alone on screen, screen on, unlocked.
 * @property playedMillis the time since the previous poll credited to [playingPackage].
 */
data class PaEPlayMeasurement(
  val screenInUse: Boolean,
  val playingPackage: String?,
  val playedMillis: Long,
) {
  fun playedMillisFor(packageName: String?): Long =
    if (packageName != null && packageName == playingPackage) playedMillis else 0L
}

/**
 * The app the play-time meter is told is alone in the foreground: null when nothing is in front,
 * the state is unknown, or another app is resumed at the same time (split-screen).
 */
fun PackageUsageState.soleVisiblePackage(): String? =
  (this as? PackageUsageState.ForegroundPackage)?.takeUnless { it.sharesScreen }?.packageName

/**
 * Measures real play time (AND-955), replacing the fixed 10 seconds added on every poll.
 *
 * Called once per poll with [soleVisiblePackage]. The time between two polls is credited to a
 * game only when, at both polls, the screen was on, the phone unlocked and that game alone in
 * the foreground, and the screen did not go off in between. A screen state that can't be read
 * counts as not in use.
 *
 * A poll loop suspended for a while (process frozen, doze) is not play: an interval counts at
 * most [maxCreditPerPollMillis] (the poll interval plus a margin) plus however long the previous
 * poll's own work took (see [onPollWorkDone]), so a slow heartbeat doesn't eat play time.
 *
 * What happens between two polls is not seen beyond a screen-off: a short trip to another app
 * and back within one interval still counts. The backend caps every heartbeat at the real time
 * since the wallet's last credit, so this is bounded by one poll interval.
 *
 * Thread-safe: the poll loop measures while the screen-off receiver and a restart call in from
 * the main thread.
 */
class PaEPlayTimeMeter(
  private val clock: PaEMonotonicClock,
  private val screen: PaEScreenState,
  private val maxCreditPerPollMillis: Long,
) {

  private var lastPlayingPackage: String? = null
  private var lastPollAtMillis: Long? = null
  private var lastPollWorkMillis = 0L
  private var pollInProgress = false
  private var screenWentOff = false

  @Synchronized
  fun measure(soleVisiblePackage: String?): PaEPlayMeasurement {
    val now = clock.elapsedRealtimeMillis()
    val screenInUse = runCatching { screen.isInteractive && !screen.isKeyguardLocked }
      .getOrDefault(false)
    val playing = soleVisiblePackage?.takeIf { screenInUse }
    val interrupted = screenWentOff
    screenWentOff = false

    val previousPollAt = lastPollAtMillis
    val played = if (
      playing != null && playing == lastPlayingPackage && previousPollAt != null && !interrupted
    ) {
      (now - previousPollAt).coerceIn(0L, maxCreditPerPollMillis + lastPollWorkMillis)
    } else {
      0L
    }

    lastPlayingPackage = playing
    lastPollAtMillis = now
    lastPollWorkMillis = 0L
    pollInProgress = true
    return PaEPlayMeasurement(screenInUse, playing, played)
  }

  /**
   * The poll that last called [measure] finished its work (usage query, session creation,
   * heartbeat). That duration is added to the next interval's cap: the loop wasn't suspended.
   */
  @Synchronized
  fun onPollWorkDone() {
    // Only right after a measure: a poll that failed before measuring widens nothing.
    if (!pollInProgress) return
    pollInProgress = false
    val pollAt = lastPollAtMillis ?: return
    lastPollWorkMillis = (clock.elapsedRealtimeMillis() - pollAt).coerceAtLeast(0L)
  }

  /** The screen went off: the interval in progress is not play, whatever the next poll sees. */
  @Synchronized
  fun onScreenOff() {
    screenWentOff = true
  }

  /** Forgets the previous poll, so the next one starts a new interval. */
  @Synchronized
  fun reset() {
    lastPlayingPackage = null
    lastPollAtMillis = null
    lastPollWorkMillis = 0L
    pollInProgress = false
    screenWentOff = false
  }
}
