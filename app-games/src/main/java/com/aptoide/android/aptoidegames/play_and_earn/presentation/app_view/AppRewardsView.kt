package com.aptoide.android.aptoidegames.play_and_earn.presentation.app_view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.campaigns.domain.PaEMissionStatus
import cm.aptoide.pt.campaigns.presentation.PaEMissionsUiState
import cm.aptoide.pt.campaigns.presentation.rememberPaEMissions
import com.aptoide.android.aptoidegames.AptoideAsyncImage
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getMissionHexagonCompletedIcon
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getMissionHexagonPendingIcon
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getSmallCoinIcon
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.PaEProgressIndicator
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.getAppXPAnnotatedString
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun AppRewardsView(
  packageName: String,
) {
  val missionsState = rememberPaEMissions(packageName)

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
  Column(
    modifier = Modifier.padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    RewardsSectionHeader("Missions")
    Column {
      missions.forEach {
        MissionItem(it)
      }
    }
  }
}

@Composable
private fun CheckpointsSection(checkpoints: List<PaEMission>) {
  Column(
    modifier = Modifier.padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    RewardsSectionHeader("Checkpoints")
    Column(
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      checkpoints.forEach {
        CheckpointItem(it)
      }
    }
  }
}

@Composable
private fun CheckpointItem(checkpoint: PaEMission) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .background(Palette.GreyDark),
    contentAlignment = Alignment.Center
  ) {
    Row(
      modifier = Modifier.padding(start = 8.dp, end = 16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Image(
        painter = painterResource(R.drawable.checkpoint_icon),
        contentDescription = null,
        modifier = Modifier.size(64.dp)
      )
      Column(
        modifier = Modifier.padding(vertical = 13.dp),
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

@Composable
private fun MissionItem(mission: PaEMission) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .drawBehind {
        val strokeWidth = 1.dp.toPx()
        drawLine(
          color = Palette.GreyDark,
          start = Offset(0f, size.height),
          end = Offset(size.width, size.height),
          strokeWidth = strokeWidth
        )
      },
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      AptoideAsyncImage(
        modifier = Modifier.size(64.dp),
        data = R.drawable.book,
        contentDescription = null
      )
      Column {
        Text(
          text = mission.title,
          style = AGTypography.InputsM,
          color = Palette.White
        )
        Text(
          text = mission.description ?: "",
          style = AGTypography.Body,
          color = Palette.GreyLight
        )
      }

      Spacer(modifier = Modifier.weight(1f))
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
          imageVector = if (mission.progress?.status == PaEMissionStatus.COMPLETED) {
            getMissionHexagonCompletedIcon()
          } else {
            getMissionHexagonPendingIcon()
          },
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
  AppRewardsView(packageName = "com.mobile.legends")
}