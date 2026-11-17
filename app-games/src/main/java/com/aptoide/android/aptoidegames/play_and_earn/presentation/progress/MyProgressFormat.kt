package com.aptoide.android.aptoidegames.play_and_earn.presentation.progress

import cm.aptoide.pt.campaigns.domain.PaELeaderboard
import cm.aptoide.pt.campaigns.domain.PaELeaderboardEntry

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

/**
 * The rows the leaderboard shows: the listed top, then the player's own row when they are
 * ranked below it, so they always see where they stand.
 */
internal fun leaderboardRows(leaderboard: PaELeaderboard): List<PaELeaderboardEntry> {
  val me = leaderboard.me
  return if (me == null || leaderboard.entries.any { it.isMe }) {
    leaderboard.entries
  } else {
    leaderboard.entries + me
  }
}

/** Play & Earn is shown to this build, and there is a player to show the progress of. */
internal fun showPaEMyProgress(
  playAndEarnVisible: Boolean,
  signedIn: Boolean,
): Boolean = playAndEarnVisible && signedIn
