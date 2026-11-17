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
