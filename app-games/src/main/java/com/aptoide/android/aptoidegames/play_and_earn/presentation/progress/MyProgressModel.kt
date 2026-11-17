package com.aptoide.android.aptoidegames.play_and_earn.presentation.progress

import cm.aptoide.pt.campaigns.domain.PaEGameStreak
import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.campaigns.domain.PaEMissionProgressType
import cm.aptoide.pt.campaigns.domain.PaEMissionStatus
import cm.aptoide.pt.campaigns.domain.PaEMissionType
import cm.aptoide.pt.campaigns.domain.PaEUserMission

/**
 * The sections a game's missions are listed under, in the order they show. A game's own in-game
 * missions (levels, enemies, any other event its developer reports) are a third kind that is
 * not shown yet.
 */
enum class MissionKind { PURCHASE, PLAY_TIME }

const val DAYS_IN_STREAK = 7

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
) {
  val fraction: Float get() = if (target <= 0) 0f else (current.toFloat() / target).coerceIn(0f, 1f)
}

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

/** A game's week: the days it was played and what the streak pays on which day. */
data class GameWeekStreak(
  val daysPlayed: Int,
  val playedToday: Boolean,
  // Day of the streak (1 to 7) to the units paid on reaching it.
  val rewards: Map<Int, Int>,
) {
  val isComplete: Boolean get() = daysPlayed >= DAYS_IN_STREAK
}

data class GameProgress(
  val packageName: String,
  val name: String,
  val icon: String?,
  val streak: GameWeekStreak,
  val missions: List<MissionCard>,
)

enum class StreakTileState { PLAYED, TODAY, UPCOMING }

data class StreakTile(
  val day: Int,
  val state: StreakTileState,
  val rewardUnits: Int?,
)

/**
 * Play time is its own mission type. A purchase mission is an event mission, confirmed by the
 * developer's MMP: either flagged when it is created (`"category": "purchase"` in its arguments)
 * or recognised by the MMP event it counts (`af_purchase`, `purchase`...). Anything else (the
 * game's other events, checkpoints, its streak) has no section here.
 */
internal fun PaEMission.kind(): MissionKind? = when (type) {
  PaEMissionType.PLAY_TIME -> MissionKind.PLAY_TIME
  PaEMissionType.EVENT -> MissionKind.PURCHASE.takeIf {
    argument(CATEGORY_KEY).equals(PURCHASE, ignoreCase = true) ||
      argument(EVENT_NAME_KEY).orEmpty().contains(PURCHASE, ignoreCase = true)
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
    id = "game|$packageName|$title",
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
 * its target day is reached. [todayIndex] is today's place in the week, Monday being 0.
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
    .associate { it.progress.target to it.units },
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
      rewardUnits = streak.rewards[day],
    )
  }

/** The number on a game's logo: how many of its missions wait to be claimed. */
internal fun GameProgress.readyCount(claimed: Set<String>): Int =
  missions.count { it.state(claimed) == CardState.READY }

/**
 * The balance the screen shows. The wallet already holds every completed mission's reward, so
 * what is still to be claimed is held back and added as the player claims it: the coins that
 * fly to the balance then make it grow by what they carry.
 */
internal fun shownBalance(
  walletBalance: Long?,
  cards: List<MissionCard>,
  claimed: Set<String>,
): Long? = walletBalance?.let { balance ->
  val waiting = cards
    .distinctBy { it.id }
    .filter { it.state(claimed) == CardState.READY }
    .sumOf { it.units.toLong() }
  (balance - waiting).coerceAtLeast(0)
}

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
