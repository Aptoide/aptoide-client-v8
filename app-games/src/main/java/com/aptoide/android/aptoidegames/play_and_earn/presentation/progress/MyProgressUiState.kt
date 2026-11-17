package com.aptoide.android.aptoidegames.play_and_earn.presentation.progress

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import cm.aptoide.pt.campaigns.domain.PaELeaderboard
import cm.aptoide.pt.campaigns.domain.PaELeaderboardEntry
import cm.aptoide.pt.campaigns.domain.PaEMissionPeriod
import cm.aptoide.pt.campaigns.domain.PaEMissionProgress
import cm.aptoide.pt.campaigns.domain.PaEMissionProgressType
import cm.aptoide.pt.campaigns.domain.PaEMissionStatus
import cm.aptoide.pt.campaigns.domain.PaEMissionType
import cm.aptoide.pt.campaigns.domain.PaEUserMission

sealed interface MyProgressUiState {

  data class Idle(
    val lifetimePlaySeconds: Long,
    val lifetimeUnits: Long,
    val missions: List<PaEUserMission>,
    val streaks: List<GameStreak>,
    // Null when it could not be loaded: the rest of the screen still shows.
    val leaderboard: PaELeaderboard?,
  ) : MyProgressUiState

  data object Loading : MyProgressUiState

  data class Error(val retry: () -> Unit) : MyProgressUiState
}

/** One game's week: seven days, Monday first. */
data class GameStreak(
  val packageName: String,
  val name: String,
  val icon: String?,
  val days: List<Boolean>,
  val daysPlayed: Int,
)

class MyProgressUiStateProvider : PreviewParameterProvider<MyProgressUiState.Idle> {
  override val values: Sequence<MyProgressUiState.Idle> = sequenceOf(
    MyProgressUiState.Idle(
      lifetimePlaySeconds = 5400,
      lifetimeUnits = 320,
      missions = listOf(
        previewMission("Play 10 minutes this week", 420, 600, isNext = true),
        previewMission(
          title = "Play every day this week",
          current = 2,
          target = 7,
          type = PaEMissionType.STREAK,
        ),
        previewMission(
          title = "Play 1 hour this month",
          current = 0,
          target = 3600,
          period = PaEMissionPeriod.MONTHLY,
        ),
        previewMission("Play 5 minutes this week", 300, 300),
      ),
      streaks = listOf(
        GameStreak(
          packageName = "com.example.game",
          name = "Dice Roll",
          icon = null,
          days = listOf(true, true, false, false, false, false, false),
          daysPlayed = 2,
        ),
      ),
      leaderboard = PaELeaderboard(
        entries = listOf(
          PaELeaderboardEntry(1, "0x1a1a…1a1a", 9000, false),
          PaELeaderboardEntry(2, "0xfc20…015e", 7200, true),
          PaELeaderboardEntry(3, "0x2b2b…2b2b", 600, false),
        ),
        me = PaELeaderboardEntry(2, "0xfc20…015e", 7200, true),
      ),
    ),
    MyProgressUiState.Idle(
      lifetimePlaySeconds = 0,
      lifetimeUnits = 0,
      missions = emptyList(),
      streaks = emptyList(),
      leaderboard = null,
    ),
  )
}

private fun previewMission(
  title: String,
  current: Int,
  target: Int,
  type: PaEMissionType = PaEMissionType.PLAY_TIME,
  period: PaEMissionPeriod = PaEMissionPeriod.WEEKLY,
  isNext: Boolean = false,
) = PaEUserMission(
  title = title,
  description = null,
  icon = null,
  type = type,
  units = 20,
  period = period,
  progress = PaEMissionProgress(
    current = current,
    target = target,
    type = if (type == PaEMissionType.PLAY_TIME) {
      PaEMissionProgressType.SECONDS
    } else {
      PaEMissionProgressType.COUNT
    },
    status = when {
      current >= target -> PaEMissionStatus.COMPLETED
      current > 0 -> PaEMissionStatus.IN_PROGRESS
      else -> PaEMissionStatus.PENDING
    },
  ),
  isNext = isNext,
)
