package com.aptoide.android.aptoidegames.play_and_earn

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.ScreenData
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getGoogleIcon
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar

const val playAndEarnLoginRoute = "playAndEarnLogin"

fun playAndEarnLoginScreen() = ScreenData.withAnalytics(
  route = playAndEarnLoginRoute,
  screenAnalyticsName = "PlayAndEarnLogin",
) { _, navigate, navigateBack ->

  PlayAndEarnLoginScreen(navigate, navigateBack)
}

@Composable
private fun PlayAndEarnLoginScreen(
  navigate: (String) -> Unit,
  navigateBack: () -> Unit
) {
  Column(
    modifier = Modifier.fillMaxSize()
  ) {
    AppGamesTopBar(navigateBack = navigateBack, title = "Start Earning")
    PlayAndEarnLoginScreenContent(navigate = navigate)
  }
}

@Composable
private fun PlayAndEarnLoginScreenContent(navigate: (String) -> Unit) {
  val composition by rememberLottieComposition(
    LottieCompositionSpec.RawRes(R.raw.play_and_earn_login_animation)
  )

  val progress by animateLottieCompositionAsState(
    composition = composition,
    iterations = LottieConstants.IterateForever
  )

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 66.dp, horizontal = 24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(40.dp)
  ) {
    Spacer(modifier = Modifier.fillMaxHeight(0.1f))

    LottieAnimation(
      composition = composition,
      progress = { progress },
      modifier = Modifier,
      contentScale = ContentScale.Crop,
    )

    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
      Text(
        text = "Level up your rewards!",
        style = AGTypography.Title,
        color = Palette.Yellow,
        textAlign = TextAlign.Center
      )

      Text(
        text = "Login to unlock access to incredible prizes while you play!",
        style = AGTypography.SubHeadingS,
        color = Palette.White,
        textAlign = TextAlign.Center
      )

      Button(
        onClick = { navigate(playAndEarnPermissionsRoute) },
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, color = Palette.GreyLight),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = getGoogleIcon(),
            contentDescription = null,
            tint = Color.Unspecified
          )
          Text(
            text = "Sign in with Google",
            style = AGTypography.InputsS,
            color = Palette.White,
            textAlign = TextAlign.Center
          )
        }
      }
    }
  }
}

@Preview
@Composable
private fun PlayAndEarnLoginScreenPreview() {
  PlayAndEarnLoginScreen(
    navigate = {},
    navigateBack = {}
  )
}
