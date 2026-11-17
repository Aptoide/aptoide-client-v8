package com.aptoide.android.aptoidegames.play_and_earn.presentation.progress

import cm.aptoide.pt.campaigns.domain.PaEGameStreak
import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.campaigns.domain.PaEMissionProgressType
import cm.aptoide.pt.campaigns.domain.PaEMissionStatus
import cm.aptoide.pt.campaigns.domain.PaEMissionType
import cm.aptoide.pt.campaigns.domain.PaEUserMission

/**
 * The sections a game's missions are listed under, in the order they show. In-game missions are
 * the events a game's developer reports through its MMP that are not purchases (levels, enemies
 * defeated...).
 */
enum class MissionKind { IN_GAME, PURCHASE, PLAY_TIME }

const val DAYS_IN_STREAK = 7

/** How a mission's amounts read when they are not a plain number or a time ("Level 7"). */
data class CardAmounts(
  val current: String,
  val target: String,
  val toGo: String,
)

/** One of a game's missions, as the screen shows it. */
data class MissionCard(
  // Stable for one mission of one game: what "claimed" is remembered by.
  val id: String,
  val kind: MissionKind,
  val title: String,
  val description: String?,
  val units: Int,
  val current: Int,
  val target: Int,
  val unit: PaEMissionProgressType,
  val completed: Boolean,
  val amounts: CardAmounts? = null,
) {
  val fraction: Float get() = if (target <= 0) 0f else (current.toFloat() / target).coerceIn(0f, 1f)

  /** Demo data (Hackathon 2026): not a mission of the server's, so nothing was paid for it. */
  val isDemo: Boolean get() = id.startsWith(DEMO_ID_PREFIX)
}

const val DEMO_ID_PREFIX = "demo|"

/** What a game's mission is remembered as claimed by, here and in the in-game banner. */
fun gameMissionClaimId(packageName: String, title: String): String = "game|$packageName|$title"

/**
 * The reward is credited by the server the moment a mission completes; Claim only collects it on
 * screen. So a completed mission is READY until the player taps Claim, then CLAIMED.
 */
enum class CardState { READY, IN_PROGRESS, CLAIMED }

fun MissionCard.state(claimed: Set<String>): CardState = when {
  !completed -> CardState.IN_PROGRESS
  id in claimed -> CardState.CLAIMED
  else -> CardState.READY
}

/** Ready to claim first, then in progress, the claimed ones last; the server's order within. */
fun List<MissionCard>.sortedForDisplay(claimed: Set<String>): List<MissionCard> =
  sortedBy {
    when (it.state(claimed)) {
      CardState.READY -> 0
      CardState.IN_PROGRESS -> 1
      CardState.CLAIMED -> 2
    }
  }

/** What reaching a day of the streak gives. */
sealed interface StreakReward {
  data class Units(val units: Int) : StreakReward

  /**
   * Something inside the game (coins, a weapon). [shortLabel] goes on the tile under the picture,
   * or nothing when the picture speaks for itself; [glow] is for the one worth showing off.
   */
  data class Item(
    val label: String,
    val shortLabel: String?,
    val imageRes: Int,
    val glow: Boolean = false,
  ) : StreakReward
}

/** A game's week: the days it was played and what the streak gives on which day. */
data class GameWeekStreak(
  val daysPlayed: Int,
  val playedToday: Boolean,
  // Day of the streak (1 to 7) to what reaching it gives.
  val rewards: Map<Int, StreakReward>,
) {
  val isComplete: Boolean get() = daysPlayed >= DAYS_IN_STREAK
}

data class GameProgress(
  val packageName: String,
  val name: String,
  val icon: String?,
  val streak: GameWeekStreak,
  val missions: List<MissionCard>,
  // A logo shipped with the app, for the demo game.
  val iconRes: Int? = null,
)

enum class StreakTileState { PLAYED, TODAY, UPCOMING }

data class StreakTile(
  val day: Int,
  val state: StreakTileState,
  val reward: StreakReward?,
)

/**
 * Play time is its own mission type. The rest of a game's missions are events its developer's MMP
 * confirms: a purchase when flagged as one (`"category": "purchase"` in its arguments) or when
 * the MMP event it counts says so (`af_purchase`, `purchase`...), an in-game mission otherwise.
 * Checkpoints and the game's streak have no section here.
 */
internal fun PaEMission.kind(): MissionKind? = when (type) {
  PaEMissionType.PLAY_TIME -> MissionKind.PLAY_TIME
  PaEMissionType.EVENT ->
    if (argument(CATEGORY_KEY).equals(PURCHASE, ignoreCase = true) ||
      argument(EVENT_NAME_KEY).orEmpty().contains(PURCHASE, ignoreCase = true)
    ) {
      MissionKind.PURCHASE
    } else {
      MissionKind.IN_GAME
    }

  else -> null
}

private fun PaEMission.argument(key: String): String? =
  runCatching { arguments.get(key)?.takeIf { it.isJsonPrimitive }?.asString }.getOrNull()

private const val CATEGORY_KEY = "category"
private const val EVENT_NAME_KEY = "event_name"
private const val PURCHASE = "purchase"

/** Null for a mission this screen has no section for. */
internal fun PaEMission.toCard(packageName: String): MissionCard? {
  val kind = kind() ?: return null
  val target = progress?.target?.takeIf { it > 0 } ?: return null
  return MissionCard(
    id = gameMissionClaimId(packageName, title),
    kind = kind,
    title = title,
    description = description,
    units = units,
    current = (progress?.current ?: 0).coerceIn(0, target),
    target = target,
    unit = progress?.type ?: PaEMissionProgressType.COUNT,
    completed = progress?.status == PaEMissionStatus.COMPLETED,
  )
}

/**
 * A game's streak this week. [week] is what the server recorded for the game (absent when it was
 * not played this week); [streakMissions] are that game's own streak missions, each paying when
 * its target day is reached: one per day makes a reward on every tile. [todayIndex] is today's
 * place in the week, Monday being 0.
 */
internal fun gameWeekStreak(
  week: PaEGameStreak?,
  streakMissions: List<PaEUserMission>,
  todayIndex: Int,
): GameWeekStreak = GameWeekStreak(
  daysPlayed = (week?.daysPlayed ?: 0).coerceIn(0, DAYS_IN_STREAK),
  playedToday = week?.days?.getOrNull(todayIndex) == true,
  rewards = streakMissions
    .filter { it.type == PaEMissionType.STREAK && it.progress.target in 1..DAYS_IN_STREAK }
    .associate { it.progress.target to StreakReward.Units(it.units) },
)

/**
 * The seven tiles: the days already played fill up from day 1, and the next one is today's until
 * the game is played today.
 */
internal fun streakTiles(streak: GameWeekStreak): List<StreakTile> =
  (1..DAYS_IN_STREAK).map { day ->
    StreakTile(
      day = day,
      state = when {
        day <= streak.daysPlayed -> StreakTileState.PLAYED
        day == streak.daysPlayed + 1 && !streak.playedToday -> StreakTileState.TODAY
        else -> StreakTileState.UPCOMING
      },
      reward = streak.rewards[day],
    )
  }

/** The number on a game's logo: how many of its missions wait to be claimed. */
internal fun GameProgress.readyCount(claimed: Set<String>): Int =
  missions.count { it.state(claimed) == CardState.READY }

/**
 * The balance the screen shows, which ends level with the wallet's once everything is claimed.
 * The wallet already holds every completed mission's reward, so what is still to be claimed is
 * held back and added as the player claims it: the coins that fly to the balance make it grow by
 * exactly what they carry. A demo mission was never paid, so claiming it only adds on screen.
 */
internal fun shownBalance(
  walletBalance: Long?,
  cards: List<MissionCard>,
  claimed: Set<String>,
): Long? = walletBalance?.let { balance ->
  val (demo, real) = cards.distinctBy { it.id }.partition { it.isDemo }
  val waiting = real
    .filter { it.state(claimed) == CardState.READY }
    .sumOf { it.units.toLong() }
  val demoClaimed = demo
    .filter { it.state(claimed) == CardState.CLAIMED }
    .sumOf { it.units.toLong() }
  (balance - waiting).coerceAtLeast(0) + demoClaimed
}

/**
 * The missions to mark as claimed the first time the screen opens. Whatever was completed before
 * there was anything to claim was paid, and likely spent, long ago: offering it now would hold
 * back from the balance units the wallet no longer has.
 */
internal fun claimsToBaseline(cards: List<MissionCard>): Set<String> =
  cards.filter { it.completed && !it.isDemo }.map { it.id }.toSet()

/** The game opened first: the one with most to claim, else the first listed. */
internal fun defaultGame(games: List<GameProgress>, claimed: Set<String>): String? =
  games.maxByOrNull { it.readyCount(claimed) }?.packageName

/** A mission's time, as the design writes it: minutes under an hour, hours from there on. */
internal sealed interface Duration {
  data class Minutes(val minutes: Long) : Duration

  /** Whole hours, or one decimal when it is not a whole number ("1.5"). */
  data class Hours(val text: String) : Duration
}

internal fun durationOf(seconds: Long): Duration {
  if (seconds < SECONDS_IN_HOUR) {
    val minutes = seconds.coerceAtLeast(0) / 60
    return Duration.Minutes(if (seconds > 0) minutes.coerceAtLeast(1) else 0)
  }
  val tenths = seconds * 10 / SECONDS_IN_HOUR
  return Duration.Hours(if (tenths % 10 == 0L) "${tenths / 10}" else "${tenths / 10}.${tenths % 10}")
}

private const val SECONDS_IN_HOUR = 3600L
