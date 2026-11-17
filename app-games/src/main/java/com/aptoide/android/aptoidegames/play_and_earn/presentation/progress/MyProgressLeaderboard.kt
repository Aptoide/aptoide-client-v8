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
 * ten in a list, and the player's own place. Inside the top ten their row is one of those; below
 * it, it is shown apart, pinned under the list.
 */
data class LeaderboardView(
  val podium: List<RankedPlayer>,
  val rows: List<RankedPlayer>,
  val me: RankedPlayer,
  val tip: LeaderboardTip,
) {
  val isMeInTop: Boolean get() = me.rank <= LEADERBOARD_TOP
}

/**
 * Ranks the player among [others] by their [myUnits]. A tie goes to the player: they are placed
 * above whoever has the same Units, so earning one more than the next player is always enough
 * to pass them, which is what the tip promises.
 */
internal fun leaderboardView(
  others: List<LeaderboardPlayer>,
  myUnits: Long,
  myName: String,
  myColor: Int = 0,
): LeaderboardView {
  val sorted = others.sortedByDescending { it.units }
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
 * The Units that count for the player in a game's leaderboard: what they claimed from that
 * game's missions, on top of [base] (what they are taken to have earned before).
 */
internal fun leaderboardUnits(
  game: GameProgress,
  claimed: Set<String>,
  base: Long,
): Long = base + game.missions
  .filter { it.state(claimed) == CardState.CLAIMED }
  .sumOf { it.units.toLong() }
