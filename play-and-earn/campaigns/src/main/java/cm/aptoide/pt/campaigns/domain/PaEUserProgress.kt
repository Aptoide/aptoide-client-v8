package cm.aptoide.pt.campaigns.domain

/**
 * What a player has done in Play & Earn as a whole, not in one game: the "My progress" screen.
 */
data class PaEUserProgress(
  val lifetimePlaySeconds: Long,
  // Units ever earned. Not the balance: redeeming does not lower it.
  val lifetimeUnits: Long,
  val missions: List<PaEUserMission>,
  val streaks: List<PaEGameStreak>,
  // Only the games whose missions have paid the player something.
  val gameUnits: List<PaEGameUnits> = emptyList(),
)

/**
 * The Units one game's own missions (not its streak) have paid the player: this week, this month
 * and ever. What a per-game leaderboard ranks them by.
 */
data class PaEGameUnits(
  val packageName: String,
  val week: Long,
  val month: Long,
  val allTime: Long,
)

/** A mission that belongs to the player and starts again every week or month. */
data class PaEUserMission(
  val title: String,
  val description: String?,
  val icon: String?,
  val type: PaEMissionType,
  val units: Int,
  // Null for a period this app doesn't know (a newer backend): the mission still shows.
  val period: PaEMissionPeriod?,
  // First day of the period (ISO date), which tells one week's mission from the next week's.
  val periodStart: String? = null,
  // The game the mission belongs to (its weekly streak); null for one across every game.
  val packageName: String? = null,
  val progress: PaEMissionProgress,
  // The one to present as "next mission": the first not completed yet.
  val isNext: Boolean,
) {
  val isCompleted: Boolean get() = progress.status == PaEMissionStatus.COMPLETED
}

enum class PaEMissionPeriod {
  WEEKLY,
  MONTHLY;

  companion object {
    fun fromApi(value: String?): PaEMissionPeriod? =
      entries.find { it.name.equals(value?.trim(), ignoreCase = true) }
  }
}

/** The days of the current week (Monday first) on which one game was played. */
data class PaEGameStreak(
  val packageName: String,
  val days: List<Boolean>,
  val daysPlayed: Int,
)

data class PaELeaderboard(
  val entries: List<PaELeaderboardEntry>,
  // The player's own row, also when below the listed top; null if they haven't played this week.
  val me: PaELeaderboardEntry?,
)

data class PaELeaderboardEntry(
  val rank: Int,
  val player: String,
  val playSeconds: Long,
  val isMe: Boolean,
)
