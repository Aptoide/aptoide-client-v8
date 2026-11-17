package com.aptoide.android.aptoidegames.play_and_earn.presentation.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.campaigns.domain.PaELeaderboard
import cm.aptoide.pt.campaigns.domain.PaELeaderboardEntry
import cm.aptoide.pt.campaigns.domain.PaEMissionPeriod
import cm.aptoide.pt.campaigns.domain.PaEMissionProgressType
import cm.aptoide.pt.campaigns.domain.PaEUserMission
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.ScreenData
import com.aptoide.android.aptoidegames.AptoideAsyncImage
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.design_system.IndeterminateCircularLoading
import com.aptoide.android.aptoidegames.error_views.GenericErrorView
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.FixedColors
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

const val myProgressRoute = "myProgress"

fun myProgressScreen() = ScreenData.withAnalytics(
  route = myProgressRoute,
  screenAnalyticsName = "MyProgress",
) { _, _, navigateBack ->
  MyProgressScreen(
    uiState = rememberMyProgressUiState(),
    navigateBack = navigateBack,
  )
}

@Composable
fun MyProgressScreen(
  uiState: MyProgressUiState,
  navigateBack: () -> Unit,
) {
  Column(modifier = Modifier.fillMaxSize()) {
    AppGamesTopBar(
      navigateBack = navigateBack,
      title = stringResource(R.string.play_and_earn_progress_title),
    )
    when (uiState) {
      MyProgressUiState.Loading -> Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
      ) {
        IndeterminateCircularLoading(color = Palette.Primary)
      }

      is MyProgressUiState.Error -> GenericErrorView(onRetryClick = uiState.retry)

      is MyProgressUiState.Idle -> MyProgressContent(uiState)
    }
  }
}

@Composable
private fun MyProgressContent(state: MyProgressUiState.Idle) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp)
      .padding(bottom = 32.dp),
    verticalArrangement = Arrangement.spacedBy(24.dp),
  ) {
    TotalsRow(
      lifetimePlaySeconds = state.lifetimePlaySeconds,
      lifetimeUnits = state.lifetimeUnits,
    )

    Section(title = stringResource(R.string.play_and_earn_progress_missions_title)) {
      if (state.missions.isEmpty()) {
        EmptyText(stringResource(R.string.play_and_earn_progress_missions_empty))
      } else {
        state.missions.forEach { MissionItem(it) }
      }
    }

    Section(title = stringResource(R.string.play_and_earn_progress_streaks_title)) {
      if (state.streaks.isEmpty()) {
        EmptyText(stringResource(R.string.play_and_earn_progress_streaks_empty))
      } else {
        state.streaks.forEach { StreakItem(it) }
      }
    }

    state.leaderboard?.let { leaderboard ->
      Section(title = stringResource(R.string.play_and_earn_progress_leaderboard_title)) {
        LeaderboardList(leaderboard)
      }
    }
  }
}

@Composable
private fun Section(
  title: String,
  content: @Composable () -> Unit,
) {
  Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
    Text(
      text = title,
      style = AGTypography.Title,
      color = Palette.White,
    )
    content()
  }
}

@Composable
private fun EmptyText(text: String) {
  Text(
    text = text,
    style = AGTypography.Body,
    color = Palette.GreyLight,
  )
}

@Composable
private fun TotalsRow(
  lifetimePlaySeconds: Long,
  lifetimeUnits: Long,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    TotalCard(
      label = stringResource(R.string.play_and_earn_progress_units_earned_label),
      value = lifetimeUnits.toString(),
      modifier = Modifier.weight(1f),
    )
    TotalCard(
      label = stringResource(R.string.play_and_earn_progress_play_time_label),
      value = playTimeText(lifetimePlaySeconds),
      modifier = Modifier.weight(1f),
    )
  }
}

@Composable
private fun TotalCard(
  label: String,
  value: String,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .background(Palette.GreyDark)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    Text(
      text = value,
      style = AGTypography.Title,
      color = Palette.Primary,
      maxLines = 1,
    )
    Text(
      text = label,
      style = AGTypography.InputsM,
      color = Palette.GreyLight,
    )
  }
}

@Composable
private fun MissionItem(mission: PaEUserMission) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(Palette.GreyDark)
      .then(
        if (mission.isNext) Modifier.border(2.dp, Palette.Primary) else Modifier
      )
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      mission.period?.let { Tag(text = periodText(it), highlighted = false) }
      if (mission.isNext) {
        Tag(
          text = stringResource(R.string.play_and_earn_progress_mission_next),
          highlighted = true,
        )
      }
      Spacer(modifier = Modifier.weight(1f))
      Text(
        text = stringResource(R.string.play_and_earn_progress_mission_reward, mission.units),
        style = AGTypography.InputsL,
        color = Palette.Primary,
      )
    }
    Text(
      text = mission.title,
      style = AGTypography.SubHeadingM,
      color = Palette.White,
    )
    ProgressBar(fraction = mission.progress.getNormalizedProgress())
    Text(
      text = if (mission.isCompleted) {
        stringResource(R.string.play_and_earn_progress_mission_completed)
      } else {
        missionProgressText(mission)
      },
      style = AGTypography.InputsM,
      color = if (mission.isCompleted) Palette.Primary else Palette.GreyLight,
    )
  }
}

@Composable
private fun Tag(
  text: String,
  highlighted: Boolean,
) {
  Text(
    text = text,
    style = AGTypography.InputsXS,
    // On the brand accent the label keeps one color in both themes.
    color = if (highlighted) FixedColors.Dark else Palette.White,
    modifier = Modifier
      .background(if (highlighted) Palette.Primary else Palette.Grey)
      .padding(horizontal = 6.dp, vertical = 2.dp),
  )
}

@Composable
private fun ProgressBar(fraction: Float) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(8.dp)
      .background(Palette.Grey),
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth(fraction.coerceIn(0f, 1f))
        .fillMaxHeight()
        .background(Palette.Primary),
    )
  }
}

@Composable
private fun StreakItem(streak: GameStreak) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(Palette.GreyDark)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Row(
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      streak.icon?.let {
        AptoideAsyncImage(
          data = it,
          contentDescription = null,
          modifier = Modifier.size(32.dp),
        )
      }
      Text(
        text = streak.name,
        style = AGTypography.SubHeadingM,
        color = Palette.White,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.weight(1f),
      )
      Text(
        text = stringResource(
          R.string.play_and_earn_progress_streak_days,
          streak.daysPlayed,
          streak.days.size,
        ),
        style = AGTypography.InputsM,
        color = Palette.GreyLight,
      )
    }
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      streak.days.forEachIndexed { index, played ->
        Column(
          modifier = Modifier.weight(1f),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .aspectRatio(1f)
              .background(if (played) Palette.Primary else Palette.Grey),
          )
          Text(
            text = weekdayLetter(index),
            style = AGTypography.InputsXS,
            color = if (played) Palette.White else Palette.GreyLight,
          )
        }
      }
    }
  }
}

@Composable
private fun LeaderboardList(leaderboard: PaELeaderboard) {
  val rows = leaderboardRows(leaderboard)
  if (rows.isEmpty()) {
    EmptyText(stringResource(R.string.play_and_earn_progress_leaderboard_empty))
    return
  }
  Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
    rows.forEach { LeaderboardRow(it) }
  }
}

@Composable
private fun LeaderboardRow(entry: PaELeaderboardEntry) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(Palette.GreyDark)
      .then(if (entry.isMe) Modifier.border(2.dp, Palette.Primary) else Modifier)
      .padding(horizontal = 16.dp, vertical = 12.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = entry.rank.toString(),
      style = AGTypography.SubHeadingM,
      color = Palette.Primary,
      textAlign = TextAlign.Center,
      modifier = Modifier.width(32.dp),
    )
    Text(
      text = if (entry.isMe) {
        stringResource(R.string.play_and_earn_progress_leaderboard_you)
      } else {
        entry.player
      },
      style = AGTypography.InputsL,
      color = Palette.White,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier.weight(1f),
    )
    Text(
      text = playTimeText(entry.playSeconds),
      style = AGTypography.InputsM,
      color = Palette.GreyLight,
    )
  }
}

@Composable
private fun playTimeText(seconds: Long): String {
  val time = playTimeOf(seconds)
  return if (time.hours > 0) {
    stringResource(R.string.play_and_earn_progress_play_time_hours, time.hours, time.minutes)
  } else {
    stringResource(R.string.play_and_earn_progress_play_time_minutes, time.minutes)
  }
}

@Composable
private fun periodText(period: PaEMissionPeriod): String = when (period) {
  PaEMissionPeriod.WEEKLY -> stringResource(R.string.play_and_earn_progress_mission_weekly)
  PaEMissionPeriod.MONTHLY -> stringResource(R.string.play_and_earn_progress_mission_monthly)
}

@Composable
private fun missionProgressText(mission: PaEUserMission): String {
  val current = mission.progress.current ?: 0
  val target = mission.progress.target
  return when (mission.progress.type) {
    PaEMissionProgressType.SECONDS -> stringResource(
      R.string.play_and_earn_progress_mission_amount,
      playTimeText(current.toLong()),
      playTimeText(target.toLong()),
    )

    PaEMissionProgressType.COUNT -> stringResource(
      R.string.play_and_earn_progress_mission_days,
      current,
      target,
    )

    PaEMissionProgressType.GXP -> stringResource(
      R.string.play_and_earn_progress_mission_amount,
      current.toString(),
      target.toString(),
    )
  }
}

// Monday first, in the device's language, so no weekday strings to translate.
private fun weekdayLetter(index: Int): String =
  DayOfWeek.of(index % 7 + 1).getDisplayName(TextStyle.NARROW, Locale.getDefault())

@PreviewDark
@Composable
private fun MyProgressScreenPreview(
  @PreviewParameter(MyProgressUiStateProvider::class) uiState: MyProgressUiState.Idle,
) {
  MyProgressScreen(
    uiState = uiState,
    navigateBack = {},
  )
}
