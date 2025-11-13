package com.aptoide.android.aptoidegames.play_and_earn.presentation.permissions

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.extensions.toAnnotatedString
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.design_system.AccentSmallButton
import com.aptoide.android.aptoidegames.play_and_earn.presentation.analytics.rememberPaEAnalytics
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar
import kotlinx.coroutines.launch

const val playAndEarnPermissionsRoute = "playAndEarnPermissions"

fun playAndEarnPermissionsScreen() = ScreenData.withAnalytics(
  route = playAndEarnPermissionsRoute,
  screenAnalyticsName = "PlayAndEarnPermissions",
) { _, navigate, navigateBack ->

  PlayAndEarnPermissionsScreen(navigateBack)
}

@Composable
private fun PlayAndEarnPermissionsScreen(
  navigateBack: () -> Unit
) {
  val coroutineScope = rememberCoroutineScope()
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  val paeAnalytics = rememberPaEAnalytics()

  DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_RESUME) {
        if (context.hasOverlayPermission() && context.hasUsageStatsPermissionStatus()) {
          navigateBack()
        }
      }
    }

    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose {
      lifecycleOwner.lifecycle.removeObserver(observer)
    }
  }

  Column(
    modifier = Modifier.fillMaxSize()
  ) {
    AppGamesTopBar(navigateBack = navigateBack, title = "Start Earning")
    PaEPermissionsScreenContent(
      onPermissionClick = {
        paeAnalytics.sendPaEPermissionLetsDoItClick()
        coroutineScope.launch {
          context.startActivity(Intent(context, OverlayPermissionActivity::class.java))
        }
      }
    )
  }
}

@Composable
private fun PaEPermissionsScreenContent(
  onPermissionClick: () -> Unit
) {
  val composition by rememberLottieComposition(
    LottieCompositionSpec.RawRes(R.raw.play_and_earn_permissions_animation)
  )

  val progress by animateLottieCompositionAsState(
    composition = composition,
    iterations = LottieConstants.IterateForever
  )

  val permissionsString = stringResource(R.string.play_and_earn_permissions)

  val annotatedString =
    permissionsString.toAnnotatedString(SpanStyle(color = Palette.SecondaryLight))

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
        text = "You’re almost there!",
        style = AGTypography.Title,
        color = Palette.Yellow100,
        textAlign = TextAlign.Center
      )

      Text(
        text = annotatedString,
        style = AGTypography.SubHeadingS,
        color = Palette.White,
        textAlign = TextAlign.Center
      )

      //TODO: fix button
      AccentSmallButton(
        title = "Let’s do it!", //TODO: hardcoded string
        onClick = onPermissionClick,
        modifier = Modifier
          .fillMaxWidth()
          .requiredHeight(48.dp)
      )
    }
  }
}

@Preview
@Composable
private fun PlayAndEarnPermissionsScreenPreview() {
  PlayAndEarnPermissionsScreen(navigateBack = {})
}