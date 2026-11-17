package com.aptoide.android.aptoidegames.play_and_earn.presentation.progress

/** What stretch of time a game's leaderboard covers. */
enum class LeaderboardPeriod { WEEK, MONTH, ALL_TIME }

/** A player of one game, ranked by the Units they earned from that game's missions. */
data class LeaderboardPlayer(
  val name: String,
  val units: Long,
  // Which of the avatar colors the player has; they have no picture.
  val color: Int,
  val isMe: Boolean = false,
)

data class RankedPlayer(
  val rank: Int,
  val player: LeaderboardPlayer,
)

/** What the player is told under their row: how far the next step up is. */
sealed interface LeaderboardTip {
  data object First : LeaderboardTip

  data class ReachRank(val units: Long, val rank: Int) : LeaderboardTip

  data class ReachTop(val units: Long) : LeaderboardTip
}

const val LEADERBOARD_TOP = 10
private const val PODIUM = 3

/**
 * A game's leaderboard as the screen lays it out: the top three on a podium, the rest of the top
 * ten in a list, and the player's own place when they have one. Inside the top ten their row is
 * one of those; below it, it is shown apart, pinned under the list. [me] and [tip] are null for a
 * player the game's missions have paid nothing in the period: they are not in its ranking.
 */
data class LeaderboardView(
  val podium: List<RankedPlayer>,
  val rows: List<RankedPlayer>,
  val me: RankedPlayer?,
  val tip: LeaderboardTip?,
) {
  val isMeBelowTop: Boolean get() = me != null && me.rank > LEADERBOARD_TOP
}

/**
 * Ranks the player among [others] by their [myUnits]; with no Units they are left out. A tie
 * goes to the player: they are placed above whoever has the same Units, so earning one more
 * than the next player is always enough to pass them, which is what the tip promises.
 */
internal fun leaderboardView(
  others: List<LeaderboardPlayer>,
  myUnits: Long,
  myName: String,
  myColor: Int = 0,
): LeaderboardView {
  val sorted = others.sortedByDescending { it.units }
  if (myUnits <= 0) {
    val all = sorted.mapIndexed { index, player -> RankedPlayer(index + 1, player) }
    return LeaderboardView(
      podium = all.take(PODIUM),
      rows = all.drop(PODIUM).take(LEADERBOARD_TOP - PODIUM),
      me = null,
      tip = null,
    )
  }
  val rank = 1 + sorted.count { it.units > myUnits }
  val all = sorted.toMutableList()
    .apply { add(rank - 1, LeaderboardPlayer(myName, myUnits, myColor, isMe = true)) }
    .mapIndexed { index, player -> RankedPlayer(rank = index + 1, player = player) }
  return LeaderboardView(
    podium = all.take(PODIUM),
    rows = all.drop(PODIUM).take(LEADERBOARD_TOP - PODIUM),
    me = all[rank - 1],
    tip = when {
      rank == 1 -> LeaderboardTip.First
      rank <= LEADERBOARD_TOP -> LeaderboardTip.ReachRank(
        units = all[rank - 2].player.units - myUnits + 1,
        rank = rank - 1,
      )

      else -> LeaderboardTip.ReachTop(
        units = all[LEADERBOARD_TOP - 1].player.units - myUnits + 1,
      )
    },
  )
}

/**
 * The Units the player is ranked by in a game's leaderboard for [period]: what that game's own
 * missions paid them in it, as the server says. The demo game was never paid by the server, so
 * what is claimed from it on screen is added, which lets the demo show the climb.
 */
internal fun leaderboardUnits(
  game: GameProgress,
  period: LeaderboardPeriod,
  claimed: Set<String>,
): Long {
  val earned = game.unitsEarned[period] ?: 0
  val demoClaimed = game.missions
    .filter { it.isDemo && it.state(claimed) == CardState.CLAIMED }
    .sumOf { it.units.toLong() }
  return earned + demoClaimed
}
