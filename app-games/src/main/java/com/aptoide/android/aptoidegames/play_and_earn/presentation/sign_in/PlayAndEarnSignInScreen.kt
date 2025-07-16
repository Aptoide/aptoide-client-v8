package com.aptoide.android.aptoidegames.play_and_earn.presentation.sign_in

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar

const val playAndEarnSignInRoute = "playAndEarnSignIn"

fun playAndEarnSignInScreen() = ScreenData.withAnalytics(
  route = playAndEarnSignInRoute,
  screenAnalyticsName = "PlayAndEarnSignIn",
) { _, navigate, navigateBack ->

  PlayAndEarnSignInScreen(navigate, navigateBack)
}

@Composable
private fun PlayAndEarnSignInScreen(
  navigate: (String) -> Unit,
  navigateBack: () -> Unit
) {
  Column(
    modifier = Modifier.fillMaxSize()
  ) {
    AppGamesTopBar(navigateBack = navigateBack, title = "Start Earning")
    PaESignInScreenContent(onSignInClick = {})
  }
}

@Composable
private fun PaESignInScreenContent(
  onSignInClick: () -> Unit
) {
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
        color = Palette.Yellow100,
        textAlign = TextAlign.Center
      )

      Text(
        text = "Login to unlock access to incredible prizes while you play!",
        style = AGTypography.SubHeadingS,
        color = Palette.White,
        textAlign = TextAlign.Center
      )

      Image(
        painter = painterResource(R.drawable.google_sign_in_button),
        contentDescription = null,
        modifier = Modifier.clickable(enabled = true, onClick = onSignInClick),
        contentScale = ContentScale.Fit
      )
    }
  }
}

@Preview
@Composable
private fun PaESignInScreenPreview() {
  PlayAndEarnSignInScreen(
    navigate = {},
    navigateBack = {}
  )
}
