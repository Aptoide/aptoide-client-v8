package com.aptoide.android.aptoidegames.play_and_earn.presentation.progress

import cm.aptoide.pt.campaigns.domain.PaELeaderboard
import cm.aptoide.pt.campaigns.domain.PaELeaderboardEntry
import cm.aptoide.pt.campaigns.domain.PaEMissionProgressType
import com.aptoide.android.aptoidegames.R
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

private val DEMO_PLAYERS = listOf(
  "NightOwl_PT", "xKiraz", "Tugas4Ever", "MariaG_22", "DiceKing", "Rui.Plays", "LunaStar",
  "Pedro_GG", "SofiaQuack", "ZeDoPipo", "TheRealNuno", "Bia_Gamer", "FoxHunter7", "Inês.exe",
  "Lisboa_Legend", "CapitaoTuga", "Marta.Plays", "DarkPhoenix", "JoaoDoPorto", "Kika_Pro",
  "SnackAttack", "Tiago_TTV", "AnaBanana", "PixelPirate", "Gui_Gamer", "QueenCarol",
  "MrBacalhau", "RitaRush", "VascoDaGama_", "Leo.exe", "Beatriz_BR", "CyberCamelo",
  "DuckSlayer", "Nuno_NPC", "SaraSniper", "ElTouro",
)

/**
 * The seed of one game's leaderboard in one week: each game has its own players and times, the
 * same all week, a new set the next.
 */
internal fun demoLeaderboardSeed(packageName: String, week: Int): Int =
  31 * week + packageName.hashCode()

// What the player has played of the demo game this week, in line with its play-time missions.
internal const val DEMO_DUCK_MY_SECONDS = 8400L

private const val DEMO_TOP = 10
private const val MINUTE = 60L
private const val HOUR = 60 * MINUTE

/**
 * A game's week that looks lived in: ten players between roughly twenty and three hours, the
 * same for a given [seed] (see [demoLeaderboardSeed]), with the player placed where their play
 * time [mySeconds] puts them (inside the top, or further down with their own row; left out when
 * they have not played the game).
 */
internal fun demoLeaderboard(mySeconds: Long, seed: Int): PaELeaderboard {
  val random = Random(seed)
  var seconds = (17 * HOUR + random.nextLong(4 * HOUR))
  val others = DEMO_PLAYERS.shuffled(random).take(DEMO_TOP).map { name ->
    val entry = name to (seconds / MINUTE) * MINUTE
    seconds = (seconds * (0.78 + random.nextDouble() * 0.14)).toLong()
    entry
  }
  val ahead = others.count { it.second >= mySeconds }
  val inTop = mySeconds > 0 && ahead < DEMO_TOP

  val listed = buildList {
    others.forEach { (name, time) -> add(Triple(name, time, false)) }
    if (inTop) add(ahead, Triple(ME, mySeconds, true))
  }.take(DEMO_TOP)
  val entries = listed.mapIndexed { index, (name, time, isMe) ->
    PaELeaderboardEntry(rank = index + 1, player = name, playSeconds = time, isMe = isMe)
  }
  val me = when {
    mySeconds <= 0 -> null
    inTop -> entries.first { it.isMe }
    // Below the top: somewhere in a crowd of players with less time than the tenth.
    else -> PaELeaderboardEntry(
      rank = DEMO_TOP + 1 + random.nextInt(8, 60),
      player = ME,
      playSeconds = mySeconds,
      isMe = true,
    )
  }
  return PaELeaderboard(entries = entries, me = me)
}

private const val ME = "You"
