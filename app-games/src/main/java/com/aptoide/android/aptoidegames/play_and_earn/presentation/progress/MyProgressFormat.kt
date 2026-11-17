package com.aptoide.android.aptoidegames.play_and_earn.presentation.progress


/** A stretch of play time as whole hours and the minutes left over. */
internal data class PlayTime(val hours: Long, val minutes: Long)

/**
 * Rounded down to the minute, except that any play time at all shows as at least one minute: a
 * player who just started must not read "0 min" next to a mission that already moved.
 */
internal fun playTimeOf(seconds: Long): PlayTime {
  if (seconds <= 0) return PlayTime(hours = 0, minutes = 0)
  val totalMinutes = (seconds / 60).coerceAtLeast(1)
  return PlayTime(hours = totalMinutes / 60, minutes = totalMinutes % 60)
}

/** Play & Earn is shown to this build, and there is a player to show the progress of. */
internal fun showPaEMyProgress(
  playAndEarnVisible: Boolean,
  signedIn: Boolean,
): Boolean = playAndEarnVisible && signedIn
