package cm.aptoide.pt.campaigns.data

import cm.aptoide.pt.campaigns.data.model.PaEGameStreakJson
import cm.aptoide.pt.campaigns.data.model.PaELeaderboardEntryJson
import cm.aptoide.pt.campaigns.data.model.PaELeaderboardJson
import cm.aptoide.pt.campaigns.data.model.PaEUserMissionJson
import cm.aptoide.pt.campaigns.data.model.PaEUserProgressJson
import cm.aptoide.pt.campaigns.domain.PaEGameStreak
import cm.aptoide.pt.campaigns.domain.PaELeaderboard
import cm.aptoide.pt.campaigns.domain.PaELeaderboardEntry
import cm.aptoide.pt.campaigns.domain.PaEMissionPeriod
import cm.aptoide.pt.campaigns.domain.PaEMissionProgress
import cm.aptoide.pt.campaigns.domain.PaEMissionProgressType
import cm.aptoide.pt.campaigns.domain.PaEMissionStatus
import cm.aptoide.pt.campaigns.domain.PaEMissionType
import cm.aptoide.pt.campaigns.domain.PaEUserMission
import cm.aptoide.pt.campaigns.domain.PaEUserProgress
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber

internal class DefaultPaEProgressRepository(
  private val paeProgressApi: PaEProgressApi,
  private val dispatcher: CoroutineDispatcher,
) : PaEProgressRepository {

  override suspend fun getMyProgress(): Result<PaEUserProgress> = withContext(dispatcher) {
    try {
      Result.success(paeProgressApi.getMyProgress().toDomainModel())
    } catch (e: Throwable) {
      Timber.w(e, "Failed to load P&E user progress")
      Result.failure(e)
    }
  }

  override suspend fun getWeeklyLeaderboard(limit: Int): Result<PaELeaderboard> =
    withContext(dispatcher) {
      try {
        Result.success(paeProgressApi.getWeeklyLeaderboard(limit).toDomainModel())
      } catch (e: Throwable) {
        Timber.w(e, "Failed to load P&E weekly leaderboard")
        Result.failure(e)
      }
    }
}

private const val DAYS_IN_WEEK = 7

private fun PaEUserProgressJson.toDomainModel() = PaEUserProgress(
  lifetimePlaySeconds = lifetimePlaySeconds ?: 0L,
  lifetimeUnits = lifetimeUnits ?: 0L,
  missions = missions.orEmpty().mapNotNull(PaEUserMissionJson::toDomainModel),
  streaks = streaks.orEmpty().mapNotNull(PaEGameStreakJson::toDomainModel),
)

// A mission this app can't present (no title, a type or a goal it doesn't know) is left out, so a
// newer backend can't empty the screen.
private fun PaEUserMissionJson.toDomainModel(): PaEUserMission? {
  val title = title?.takeIf { it.isNotBlank() } ?: return null
  val type = PaEMissionType.entries.find { it.name.equals(this.type, ignoreCase = true) }
    ?: return null
  val target = progress?.target?.takeIf { it > 0 } ?: return null
  val status = PaEMissionStatus.entries
    .find { it.name.equals(progress.status, ignoreCase = true) }
    ?: PaEMissionStatus.PENDING
  return PaEUserMission(
    title = title,
    description = description?.takeIf { it.isNotBlank() },
    icon = icon?.takeIf { it.isNotBlank() },
    type = type,
    units = units ?: 0,
    period = PaEMissionPeriod.fromApi(period),
    progress = PaEMissionProgress(
      current = progress.current ?: 0,
      target = target,
      type = PaEMissionProgressType.entries
        .find { it.name.equals(progress.type, ignoreCase = true) }
        ?: if (type == PaEMissionType.PLAY_TIME) {
          PaEMissionProgressType.SECONDS
        } else {
          PaEMissionProgressType.COUNT
        },
      status = status,
    ),
    isNext = isNext == true && status != PaEMissionStatus.COMPLETED,
  )
}

// Always seven squares, Monday first, whatever the backend sent.
private fun PaEGameStreakJson.toDomainModel(): PaEGameStreak? {
  val packageName = packageName?.takeIf { it.isNotBlank() } ?: return null
  val week = List(DAYS_IN_WEEK) { days.orEmpty().getOrNull(it) == true }
  return PaEGameStreak(
    packageName = packageName,
    days = week,
    daysPlayed = week.count { it },
  )
}

private fun PaELeaderboardJson.toDomainModel() = PaELeaderboard(
  entries = entries.orEmpty().mapNotNull(PaELeaderboardEntryJson::toDomainModel),
  me = me?.toDomainModel(),
)

private fun PaELeaderboardEntryJson.toDomainModel(): PaELeaderboardEntry? {
  return PaELeaderboardEntry(
    rank = rank ?: return null,
    player = player?.takeIf { it.isNotBlank() } ?: return null,
    playSeconds = playSeconds ?: 0L,
    isMe = isMe == true,
  )
}
