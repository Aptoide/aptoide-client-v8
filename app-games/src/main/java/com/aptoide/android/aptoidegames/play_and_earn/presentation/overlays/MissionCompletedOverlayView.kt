package com.aptoide.android.aptoidegames.play_and_earn.presentation.overlays

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.campaigns.data.paeMission1
import cm.aptoide.pt.campaigns.domain.PaEMission
import com.aptoide.android.aptoidegames.drawables.icons.getAGAppIcon
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getMissionHexagonCompletedIcon
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.PaEProgressIndicator
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.animations.SuccessConfettiAnimation
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import kotlinx.coroutines.delay

@Composable
fun MissionCompletedOverlayView(
  mission: PaEMission
) {
  var visible by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    delay(500L)
    visible = true
  }

  AnimatedVisibility(
    visible = visible,
    enter = fadeIn(tween(400)) + scaleIn(tween(400)),
    exit = fadeOut(tween(400)) + scaleOut(tween(400))
  ) {
    MissionCompletedOverlayViewContent(mission)
  }
}

@Composable
private fun MissionCompletedOverlayViewContent(
  paEMission: PaEMission
) {
  var targetProgress by remember { mutableFloatStateOf(0f) }

  LaunchedEffect(Unit) {
    delay(300L)
    targetProgress = 1f
  }

  val animatedProgress by animateFloatAsState(
    targetValue = targetProgress.coerceIn(0f, 1f),
    animationSpec = tween(durationMillis = 800, easing = EaseOut),
    label = "progress"
  )

  Box(
    modifier = Modifier
      .width(228.dp)
      .wrapContentHeight()
      .background(Palette.Black)
      .border(width = 2.dp, color = Palette.Secondary),
  ) {
    SuccessConfettiAnimation(
      modifier = Modifier.matchParentSize(),
      contentScale = ContentScale.Crop
    )

    Column(
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Image(
          modifier = Modifier.size(20.dp),
          imageVector = getAGAppIcon(color = Palette.White),
          contentDescription = null
        )
        PaEProgressIndicator(
          progress = animatedProgress,
        )
      }

      Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(37.dp))
            .background(Palette.Secondary.copy(alpha = 0.3f))
            .padding(8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          Image(
            imageVector = getMissionHexagonCompletedIcon(),
            contentDescription = null
          )
          Text(
            text = "+ ${paEMission.units} UT",
            style = AGTypography.InputsXS,
            color = Palette.White
          )
        }

        Column {
          Text(
            text = paEMission.title,
            style = AGTypography.InputsL,
            color = Palette.White
          )
          Text(
            text = "Challenge Completed",
            style = AGTypography.InputsXSRegular,
            color = Palette.White
          )
        }
      }
    }
  }
}

@Preview
@Composable
private fun MissionCompletedOverlayViewPreview() {
  MissionCompletedOverlayView(paeMission1)
}
