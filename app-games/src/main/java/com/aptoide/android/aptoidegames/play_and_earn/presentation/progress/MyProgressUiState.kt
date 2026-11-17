package com.aptoide.android.aptoidegames.play_and_earn.presentation.progress

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import cm.aptoide.pt.campaigns.domain.PaELeaderboard
import cm.aptoide.pt.campaigns.domain.PaELeaderboardEntry
import cm.aptoide.pt.campaigns.domain.PaEMissionProgressType

sealed interface MyProgressUiState {

  data class Idle(
    // Null when the wallet could not be read: the balance chip is then left out.
    val walletBalance: Long?,
    // The installed games that have missions.
    val games: List<GameProgress>,
    // Null when it could not be loaded: the rest of the screen still shows.
    val leaderboard: PaELeaderboard?,
    val lifetimePlaySeconds: Long,
    val lifetimeUnits: Long,
    val claimed: Set<String>,
  ) : MyProgressUiState {
    val allCards: List<MissionCard> get() = games.flatMap { it.missions }
  }

  data object Loading : MyProgressUiState

  data class Error(val retry: () -> Unit) : MyProgressUiState
}

class MyProgressUiStateProvider : PreviewParameterProvider<MyProgressUiState.Idle> {
  override val values: Sequence<MyProgressUiState.Idle> = sequenceOf(
    MyProgressUiState.Idle(
      walletBalance = 1340,
      games = listOf(
        GameProgress(
          packageName = "com.dice.roll",
          name = "Dice Roll",
          icon = null,
          streak = GameWeekStreak(
            daysPlayed = 3,
            playedToday = false,
            rewards = listOf(25, 40, 50, 60, 75, 100, 150)
              .mapIndexed { index, units -> index + 1 to StreakReward.Units(units) }
              .toMap(),
          ),
          missions = listOf(
            previewCard("Make 1 purchase", MissionKind.PURCHASE, 0, 1, 150),
            previewCard("Play 30 minutes", MissionKind.PLAY_TIME, 1800, 1800, 50),
            previewCard("Play 2 hours", MissionKind.PLAY_TIME, 2700, 7200, 150),
          ),
        ),
        demoDuckSurvival(),
        GameProgress(
          packageName = "com.cell.survivor",
          name = "Cell Survivor",
          icon = null,
          streak = GameWeekStreak(daysPlayed = 0, playedToday = false, rewards = emptyMap()),
          missions = listOf(previewCard("Play 5 hours", MissionKind.PLAY_TIME, 4500, 18000, 250)),
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
      lifetimePlaySeconds = 5400,
      lifetimeUnits = 320,
      claimed = emptySet(),
    ),
    MyProgressUiState.Idle(
      walletBalance = null,
      games = emptyList(),
      leaderboard = null,
      lifetimePlaySeconds = 0,
      lifetimeUnits = 0,
      claimed = emptySet(),
    ),
  )
}

private fun previewCard(
  title: String,
  kind: MissionKind,
  current: Int,
  target: Int,
  units: Int,
) = MissionCard(
  id = title,
  kind = kind,
  title = title,
  description = when (kind) {
    MissionKind.PURCHASE -> "Any item in the game"
    MissionKind.PLAY_TIME -> "Total time played"
    MissionKind.IN_GAME -> "Progress inside the game"
  },
  units = units,
  current = current,
  target = target,
  unit = if (kind == MissionKind.PURCHASE) {
    PaEMissionProgressType.COUNT
  } else {
    PaEMissionProgressType.SECONDS
  },
  completed = current >= target,
)
