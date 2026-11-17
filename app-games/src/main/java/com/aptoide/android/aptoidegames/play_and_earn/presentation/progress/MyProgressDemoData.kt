package com.aptoide.android.aptoidegames.play_and_earn.presentation.progress

import cm.aptoide.pt.campaigns.domain.PaEMissionProgressType
import com.aptoide.android.aptoidegames.R
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.random.Random

// Demo data for the Hackathon 2026 presentation. None of it comes from the server and none of it
// is paid: it shows what the screen looks like for a game whose developer has integrated all
// three kinds of mission, and with a leaderboard full of players. Remove before a real release.
internal const val SHOW_DEMO_DATA = true

// The real game's package, so its Play button opens it when it is installed.
internal const val DEMO_DUCK_PACKAGE = "com.mrtgd2us.google"

// The order the games are shown in for the presentation; any other game comes after, by name.
internal val DEMO_GAME_ORDER = listOf(
  "com.mobile.legends",
  "com.aptoide.diceroll.sdk",
  DEMO_DUCK_PACKAGE,
)

private fun demoId(key: String) = "$DEMO_ID_PREFIX$key"

/** Duck Survival, the designer's showcase game: in-game missions, purchases and play time. */
internal fun demoDuckSurvival() = GameProgress(
  packageName = DEMO_DUCK_PACKAGE,
  name = "Duck Survival",
  icon = null,
  iconRes = R.drawable.pae_demo_duck_logo,
  isDemo = true,
  // Three days played: today is day 4.
  streak = GameWeekStreak(
    daysPlayed = 3,
    playedToday = false,
    rewards = mapOf(
      1 to StreakReward.Units(30),
      2 to StreakReward.Units(40),
      3 to StreakReward.Units(50),
      4 to StreakReward.Units(60),
      5 to StreakReward.Item(
        label = "100 coins in the game",
        shortLabel = "100 coins",
        imageRes = R.drawable.pae_demo_duck_money,
      ),
      6 to StreakReward.Units(100),
      7 to StreakReward.Item(
        label = "Special weapon: Golden Shotgun",
        shortLabel = null,
        imageRes = R.drawable.pae_demo_duck_weapon,
        glow = true,
      ),
    ),
  ),
  missions = listOf(
    MissionCard(
      id = demoId("duck-level"),
      kind = MissionKind.IN_GAME,
      title = "Reach level 10",
      description = "Level up your duck",
      units = 200,
      current = 7,
      target = 10,
      unit = PaEMissionProgressType.COUNT,
      completed = false,
      amounts = CardAmounts(current = "Level 7", target = "Level 10", toGo = "3"),
    ),
    MissionCard(
      id = demoId("duck-survive"),
      kind = MissionKind.IN_GAME,
      title = "Survive 5 minutes",
      description = "In a single run",
      units = 100,
      current = 300,
      target = 300,
      unit = PaEMissionProgressType.COUNT,
      completed = true,
      amounts = CardAmounts(current = "5:00", target = "5:00", toGo = "0 min"),
    ),
    MissionCard(
      id = demoId("duck-foxes"),
      kind = MissionKind.IN_GAME,
      title = "Defeat 50 foxes",
      description = "Across all runs",
      units = 150,
      current = 20,
      target = 50,
      unit = PaEMissionProgressType.COUNT,
      completed = false,
    ),
    MissionCard(
      id = demoId("duck-purchase"),
      kind = MissionKind.PURCHASE,
      title = "Make 1 purchase",
      description = "Any item in Duck Survival",
      units = 150,
      current = 0,
      target = 1,
      unit = PaEMissionProgressType.COUNT,
      completed = false,
    ),
    MissionCard(
      id = demoId("duck-play-1h"),
      kind = MissionKind.PLAY_TIME,
      title = "Play 1 hour",
      description = "Total time played",
      units = 100,
      current = 3600,
      target = 3600,
      unit = PaEMissionProgressType.SECONDS,
      completed = true,
    ),
    MissionCard(
      id = demoId("duck-play-5h"),
      kind = MissionKind.PLAY_TIME,
      title = "Play 5 hours",
      description = "Total time played",
      units = 250,
      current = 8400,
      target = 18000,
      unit = PaEMissionProgressType.SECONDS,
      completed = false,
    ),
  ),
)

// The prototype's players: short gamer names, reused with a number once they run out.
private val DEMO_PLAYERS = listOf(
  "Nova", "Kiro", "Mika", "Rex", "Luna", "Zed", "Axel", "Yuki", "Bolt", "Sage", "Pixel", "Ruby",
  "Finn", "Echo", "Jax", "Mira", "Otto", "Vex", "Lola", "Dash", "Neo", "Pip", "Kai", "Rin", "Tux",
  "Ash", "Gus", "Ivy", "Max", "Zoe",
)

internal const val DEMO_AVATAR_COLORS = 5
private const val DEMO_PLAYER_COUNT = 60

// What the best player of a game has earned from its missions in each period.
private fun topUnits(period: LeaderboardPeriod): Double = when (period) {
  LeaderboardPeriod.WEEK -> 3200.0
  LeaderboardPeriod.MONTH -> 9800.0
  LeaderboardPeriod.ALL_TIME -> 34000.0
}

/**
 * What the player is taken to have earned from a game's missions before today, so they start
 * somewhere down the ranking and climb as they claim.
 */
internal fun demoMyBaseUnits(period: LeaderboardPeriod): Long = when (period) {
  LeaderboardPeriod.WEEK -> 340
  LeaderboardPeriod.MONTH -> 1150
  LeaderboardPeriod.ALL_TIME -> 2900
}

/**
 * Sixty players of one game in one period, each a little behind the one before, as in the
 * prototype. The same for a given game and period, different between games and periods.
 */
internal fun demoLeaderboardPlayers(
  packageName: String,
  period: LeaderboardPeriod,
): List<LeaderboardPlayer> {
  val random = Random(31 * packageName.hashCode() + period.ordinal)
  val names = DEMO_PLAYERS.shuffled(random)
  return List(DEMO_PLAYER_COUNT) { index ->
    val repeat = index / names.size
    LeaderboardPlayer(
      name = names[index % names.size] + if (repeat > 0) "${index % 9 + 1}" else "",
      units = (topUnits(period) * 0.93.pow(index) * (0.97 + random.nextDouble() * 0.06))
        .roundToLong(),
      color = random.nextInt(DEMO_AVATAR_COLORS),
    )
  }.sortedByDescending { it.units }
}
