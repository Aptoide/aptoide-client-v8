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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.extensions.ScreenData
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.design_system.IndeterminateCircularLoading
import com.aptoide.android.aptoidegames.error_views.GenericErrorView
import com.aptoide.android.aptoidegames.play_and_earn.presentation.analytics.rememberPaEAnalytics
import com.aptoide.android.aptoidegames.play_and_earn.presentation.permissions.playAndEarnPermissionsRoute
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
  val signInVM = hiltViewModel<GoogleSignInViewModel>()
  val uiState by signInVM.uiState.collectAsState()

  val paeAnalytics = rememberPaEAnalytics()

  GoogleSignInEventHandler(onSuccess = { navigate(playAndEarnPermissionsRoute) })

  Column(
    modifier = Modifier.fillMaxSize()
  ) {
    AppGamesTopBar(
      navigateBack = navigateBack,
      title = stringResource(R.string.play_and_earn_start_earning_title)
    )
    PaESignInScreenContent(
      uiState = uiState,
      onSignInClick = {
        paeAnalytics.sendPaEGoogleLoginClick()
        signInVM.signIn()
      },
      onRetryClick = { signInVM.reset() }
    )
  }
}

@Composable
private fun PaESignInScreenContent(
  uiState: GoogleSignInUiState,
  onSignInClick: () -> Unit,
  onRetryClick: () -> Unit
) {
  when (uiState) {
    is GoogleSignInUiState.Error -> GenericErrorView(onRetryClick = onRetryClick)
    is GoogleSignInUiState.HandleAuthorization -> PaESignInScreenWaiting()
    GoogleSignInUiState.Idle -> PaESignInScreenIdle(onSignInClick = onSignInClick)
    GoogleSignInUiState.Success -> PaESignInScreenIdle(onSignInClick = onSignInClick)
  }
}

@Composable
private fun PaESignInScreenIdle(onSignInClick: () -> Unit) {
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
        text = stringResource(R.string.play_and_earn_level_up_rewards_title),
        style = AGTypography.Title,
        color = Palette.Yellow100,
        textAlign = TextAlign.Center
      )

      Text(
        text = stringResource(R.string.play_and_earn_login_unlock_access_body),
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

@Composable
private fun PaESignInScreenWaiting() {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 66.dp, horizontal = 24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(62.dp)
  ) {
    Spacer(modifier = Modifier.fillMaxHeight(0.1f))

    IndeterminateCircularLoading(color = Palette.Primary)

    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
      Text(
        text = stringResource(R.string.play_and_earn_waiting_title),
        style = AGTypography.Title,
        color = Palette.Primary,
        textAlign = TextAlign.Center
      )

      Text(
        text = stringResource(R.string.play_and_earn_getting_ready_body),
        style = AGTypography.SubHeadingM,
        color = Palette.White,
        textAlign = TextAlign.Center
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
