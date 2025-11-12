package com.aptoide.android.aptoidegames.play_and_earn.presentation.app_view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.campaigns.domain.PaEMissionStatus
import cm.aptoide.pt.campaigns.presentation.PaEMissionsUiState
import cm.aptoide.pt.campaigns.presentation.rememberPaEMissions
import cm.aptoide.pt.feature_apps.data.randomApp
import com.aptoide.android.aptoidegames.AptoideAsyncImage
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getMissionHexagonCompletedIcon
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getSmallCoinIcon
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.PaEProgressIndicator
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.getAppXPAnnotatedString
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun AppRewardsView(
  packageName: String,
) {
  val (missionsState, reload) = rememberPaEMissions(packageName)
  val lifecycleOwner = LocalLifecycleOwner.current

  DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_RESUME) {
        reload()
      }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose {
      lifecycleOwner.lifecycle.removeObserver(observer)
    }
  }

  when (missionsState) {
    is PaEMissionsUiState.Idle -> {
      Column {
        CheckpointsSection(missionsState.paeMissions.checkpoints)
        MissionsSection(missionsState.paeMissions.missions)
      }
    }

    PaEMissionsUiState.Empty,
    PaEMissionsUiState.Error,
    PaEMissionsUiState.Loading,
    PaEMissionsUiState.NoConnection -> Unit
  }
}

@Composable
private fun MissionsSection(missions: List<PaEMission>) {
  RewardsSection(
    title = stringResource(R.string.play_and_earn_missions_title),
    items = missions,
    itemContent = { mission -> MissionItem(mission) }
  )
}

@Composable
private fun CheckpointsSection(checkpoints: List<PaEMission>) {
  RewardsSection(
    title = stringResource(R.string.play_and_earn_checkpoints_title),
    items = checkpoints,
    itemContent = { checkpoint -> CheckpointItem(checkpoint) },
  )
}

@Composable
private fun RewardsSection(
  title: String,
  items: List<PaEMission>,
  itemContent: @Composable (PaEMission) -> Unit
) {
  // Sort items: ongoing first, then completed
  val sortedItems = items.sortedBy {
    it.progress?.status == PaEMissionStatus.COMPLETED
  }

  Column(
    modifier = Modifier.padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    RewardsSectionHeader(title)
    Column {
      sortedItems.forEachIndexed { index, item ->
        itemContent(item)

        if (index < sortedItems.size - 1) {
          val isCurrentCompleted = item.progress?.status == PaEMissionStatus.COMPLETED
          val isNextCompleted =
            sortedItems[index + 1].progress?.status == PaEMissionStatus.COMPLETED

          if (isCurrentCompleted && isNextCompleted) {
            Divider(
              modifier = Modifier.padding(vertical = 4.dp),
              color = Palette.GreyDark,
              thickness = 1.dp
            )
          } else {
            Spacer(modifier = Modifier.height(16.dp))
          }
        }
      }
    }
  }
}

@Composable
private fun CheckpointItem(checkpoint: PaEMission) {
  val isCompleted = checkpoint.progress?.status == PaEMissionStatus.COMPLETED

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .background(if (isCompleted) Color.Transparent else Palette.GreyDark),
    contentAlignment = Alignment.Center
  ) {
    Row(
      modifier = Modifier.padding(all = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Image(
        painter = painterResource(R.drawable.checkpoint_icon),
        contentDescription = null,
        modifier = Modifier.size(64.dp),
        alpha = if (isCompleted) 0.5f else 1f,
        colorFilter = if (isCompleted) ColorFilter.colorMatrix(
          ColorMatrix().apply { setToSaturation(0f) }
        ) else null
      )

      if (isCompleted) {
        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Text(
            text = checkpoint.title,
            style = AGTypography.InputsM,
            color = Palette.Grey
          )
          Text(
            text = "${checkpoint.progress?.target ?: 0} XP",
            style = AGTypography.InputsXS,
            color = Palette.Grey
          )
        }
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Text(
            text = "+ ${checkpoint.units} UNITS",
            style = AGTypography.InputsS,
            color = Palette.SecondaryLight
          )
          Image(
            imageVector = getMissionHexagonCompletedIcon(),
            contentDescription = null,
            modifier = Modifier
              .size(21.dp, 24.dp)
              .shadow(
                elevation = 6.dp,
                shape = CircleShape,
                spotColor = Color(0x80C279FF),
                clip = false
              )
          )
        }
      } else {
        Column(
          modifier = Modifier.padding(top = 5.dp, bottom = 5.dp, end = 8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = checkpoint.title,
            style = AGTypography.InputsM,
            color = Palette.White
          )
          PaEProgressIndicator(
            progress = checkpoint.progress?.getNormalizedProgress() ?: 0f,
          )
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = getAppXPAnnotatedString(
                checkpoint.progress?.current ?: 0,
                checkpoint.progress?.target ?: 0
              ),
              style = AGTypography.InputsXS,
              color = Palette.White
            )
            Text(
              text = "+ ${checkpoint.units} UNITS",
              style = AGTypography.InputsS,
              color = Palette.SecondaryLight
            )
          }
        }
      }
    }
  }
}

@Composable
private fun MissionItem(mission: PaEMission) {
  val isCompleted = mission.progress?.status == PaEMissionStatus.COMPLETED

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .background(if (isCompleted) Color.Transparent else Palette.GreyDark),
    contentAlignment = Alignment.Center
  ) {
    Row(
      modifier = Modifier.padding(all = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      AptoideAsyncImage(
        modifier = Modifier
          .size(64.dp)
          .graphicsLayer {
            alpha = if (isCompleted) 0.5f else 1f
          },
        data = R.drawable.book,
        contentDescription = null,
        colorFilter = if (isCompleted) ColorFilter.colorMatrix(
          ColorMatrix().apply { setToSaturation(0f) }
        ) else null
      )
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Text(
          text = mission.title,
          style = AGTypography.InputsM,
          color = if (isCompleted) Palette.Grey else Palette.White
        )
        Text(
          text = mission.description ?: "",
          style = AGTypography.Body,
          color = if (isCompleted) Palette.Grey else Palette.GreyLight,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )
      }

      if (isCompleted) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Text(
            text = "+ ${mission.units} UNITS",
            style = AGTypography.InputsXS,
            color = Palette.SecondaryLight
          )
          Image(
            imageVector = getMissionHexagonCompletedIcon(),
            contentDescription = null,
            modifier = Modifier
              .size(21.dp, 24.dp)
              .shadow(
                elevation = 6.dp,
                shape = CircleShape,
                spotColor = Color(0x80C279FF),
                clip = false
              )
          )
        }
      } else {
        Text(
          modifier = Modifier.padding(end = 8.dp),
          text = "+ ${mission.units} UNITS",
          style = AGTypography.InputsXS,
          color = Palette.SecondaryLight
        )
      }
    }
  }
}

@Composable
private fun RewardsSectionHeader(title: String) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    Image(
      imageVector = getSmallCoinIcon(),
      contentDescription = null
    )
    Text(
      text = title,
      style = AGTypography.InputsL,
      color = Palette.White
    )
  }
}

@Preview
@Composable
fun AppRewardsViewPreview() {
  AppRewardsView(packageName = randomApp.packageName)
}