package com.aptoide.android.aptoidegames.play_and_earn.presentation.overlays

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.drawables.icons.getAGAppIcon
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import kotlinx.coroutines.delay

@Composable
fun WelcomeBackOverlayView() {
  var visible by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    delay(500L)
    visible = true
  }

  Box(
    modifier = Modifier.size(162.dp, 87.dp)
  ) {
    AnimatedVisibility(
      visible = visible,
      enter = fadeIn(tween(400)) + scaleIn(tween(400)),
      exit = fadeOut(tween(400)) + scaleOut(tween(400))
    ) {
      WelcomeBackOverlayViewContent()
    }
  }
}

@Composable
fun WelcomeBackOverlayViewContent() {
  Box(
    modifier = Modifier
      .size(162.dp, 87.dp)
      .background(Palette.Black)
      .border(2.dp, Palette.Secondary),
    contentAlignment = Alignment.Center
  ) {
    Column(
      verticalArrangement = Arrangement.spacedBy(10.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Image(
          imageVector = getAGAppIcon(Palette.White),
          contentDescription = null,
          modifier = Modifier.size(18.dp)
        )
        Text(
          text = "Play & Earn",
          style = AGTypography.InputsL,
          color = Palette.White
        )
      }
      Text(
        text = "Welcome Back",
        style = AGTypography.InputsM,
        color = Palette.Primary
      )
    }
  }
}

@Preview
@Composable
private fun WelcomeBackOverlayViewPreview() {
  WelcomeBackOverlayView()
}
