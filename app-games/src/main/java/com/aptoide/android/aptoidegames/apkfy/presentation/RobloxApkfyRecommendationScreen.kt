package com.aptoide.android.aptoidegames.apkfy.presentation

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.domain.AppSource
import cm.aptoide.pt.feature_apps.presentation.AppUiState
import cm.aptoide.pt.feature_apps.presentation.rememberApp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.OverrideAnalyticsAPKFY
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.drawables.icons.getBonusIconRight
import com.aptoide.android.aptoidegames.drawables.icons.getCrownIcon
import com.aptoide.android.aptoidegames.error_views.GenericErrorView
import com.aptoide.android.aptoidegames.feature_apps.presentation.AppItem
import com.aptoide.android.aptoidegames.installer.presentation.AppIconWProgress
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewApkfyRecommendation
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewShort
import com.aptoide.android.aptoidegames.mmp.WithUTM
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar

const val robloxApkfyRecommendationRoute = "robloxApkfyRecommendation"

private const val RECOMMENDATION_PACKAGE_NAME = "com.moonton.mobilehero"

fun RobloxApkfyRecommendationScreen() = ScreenData.withAnalytics(
  route = robloxApkfyRecommendationRoute,
  screenAnalyticsName = "RobloxApkfyRecommendation"
) { _, navigate, navigateBack ->
  val apkfyState = rememberApkfyState()
  val apkfyAnalytics = rememberApkfyAnalytics()
  val (recommendationState, _) = rememberApp(
    source = AppSource.of(appId = null, packageName = RECOMMENDATION_PACKAGE_NAME).asSource()
  )

  BackHandler {
    apkfyAnalytics.sendApkfyScreenBackClicked()
    navigateBack()
  }

  val baselineState = apkfyState as? ApkfyUiState.BaselineWithRecommendation ?: return@withAnalytics
  val videoUrl = baselineState.recommendationVideoUrl

  OverrideAnalyticsAPKFY(navigate) {
    baselineState.data.let { apkfyData ->
      WithUTM(
        source = apkfyData.utmSource,
        medium = apkfyData.utmMedium,
        campaign = apkfyData.utmCampaign,
        content = apkfyData.utmContent,
        term = apkfyData.utmTerm,
        navigate = navigate,
      ) { navigateWithUtm ->
        Column(modifier = Modifier.fillMaxSize()) {
          AppGamesTopBar(
            navigateBack = {
              apkfyAnalytics.sendApkfyScreenBackClicked()
              navigateBack()
            },
            title = ""
          )
          Column(modifier = Modifier.fillMaxSize()) {
            when (recommendationState) {
              is AppUiState.Idle -> RobloxApkfyRecommendationView(
                apkfyApp = apkfyData.app,
                recommendationApp = recommendationState.app,
                videoUrl = videoUrl,
                navigate = navigateWithUtm,
              )

              AppUiState.Error,
              AppUiState.NoConnection -> GenericErrorView(navigateBack)

              AppUiState.Loading -> Unit
            }
          }
        }
      }
    } ?: GenericErrorView(navigateBack)
  }
}

@Composable
fun RobloxApkfyRecommendationView(
  apkfyApp: App,
  recommendationApp: App,
  videoUrl: String,
  navigate: (String) -> Unit,
) {
  Column(modifier = Modifier.fillMaxSize()) {
    ApkfyRecommendationApkfyAppItem(
      app = apkfyApp,
      onClick = { navigate(buildAppViewRoute(apkfyApp)) },
    )
    WithUTM(medium = "store-placement-rec", navigate = navigate) { navigate ->
      ApkfyRecommendationCard(
        app = recommendationApp,
        videoUrl = videoUrl,
        onClick = { navigate(buildAppViewRoute(recommendationApp)) },
        modifier = Modifier.weight(1f),
      )
    }
  }
}

@Composable
private fun ApkfyRecommendationApkfyAppItem(
  app: App,
  onClick: () -> Unit,
) {
  val apkfyAnalytics = rememberApkfyAnalytics()
  AppItem(
    modifier = Modifier
      .padding(horizontal = 24.dp)
      .padding(top = 10.dp, bottom = 32.dp),
    app = app,
    onClick = onClick,
  ) {
    InstallViewShort(
      app = app,
      onOpen = { apkfyAnalytics.sendExp83OpenRobloxClick() },
    )
  }
}

@Composable
private fun ApkfyRecommendationCard(
  app: App,
  videoUrl: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .semantics(mergeDescendants = true) {}
      .clickable(onClick = onClick)
  ) {
    ApkfyVideoBackground(
      videoUrl = videoUrl,
      modifier = Modifier.fillMaxSize(),
    )
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            colors = listOf(
              Color.Transparent,
              Color.Black.copy(alpha = 0.9f),
            )
          )
        )
    )
    TopPlayedCompanionBanner(
      modifier = Modifier
        .align(Alignment.TopCenter)
    )
    Column(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(horizontal = 16.dp, vertical = 80.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      ApkfyRecommendationInstallItem(app = app)
    }
  }
}

@Composable
private fun TopPlayedCompanionBanner(modifier: Modifier = Modifier) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    modifier = modifier
      .background(color = Palette.Secondary.copy(alpha = 0.9f))
      .padding(horizontal = 8.dp, vertical = 8.dp)
  ) {
    Image(
      imageVector = getCrownIcon(color = Palette.Yellow),
      contentDescription = null,
      modifier = Modifier.size(18.dp)
    )
    Text(
      text = stringResource(R.string.apkfy_multi_install_highlighted_game_short),
      color = Palette.Yellow,
      style = AGTypography.InputsM
    )
  }
}

@OptIn(UnstableApi::class)
@Composable
private fun ApkfyVideoBackground(
  videoUrl: String,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  val exoPlayer = remember {
    ExoPlayer.Builder(context).build().apply {
      setMediaItem(MediaItem.fromUri(Uri.parse(videoUrl)))
      repeatMode = Player.REPEAT_MODE_ALL
      volume = 0f
      playWhenReady = true
      prepare()
    }
  }

  DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      when (event) {
        Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
        Lifecycle.Event.ON_RESUME -> exoPlayer.play()
        else -> Unit
      }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose {
      lifecycleOwner.lifecycle.removeObserver(observer)
      exoPlayer.release()
    }
  }

  AndroidView(
    factory = { ctx ->
      PlayerView(ctx).apply {
        player = exoPlayer
        useController = false
        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
      }
    },
    modifier = modifier,
  )
}

@Composable
private fun ApkfyRecommendationInstallItem(app: App) {
  val apkfyAnalytics = rememberApkfyAnalytics()
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Box(contentAlignment = Alignment.TopEnd) {
      AppIconWProgress(
        app = app,
        contentDescription = null,
        modifier = Modifier.size(92.dp),
      )
      if (app.isAppCoins) {
        Image(
          imageVector = getBonusIconRight(
            iconColor = Palette.Primary,
            outlineColor = Color(0xFF1E1E26),
            backgroundColor = Palette.Secondary
          ),
          contentDescription = null,
          modifier = Modifier.size(32.dp),
        )
      }
    }
    Text(
      text = app.name,
      color = Palette.White,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      style = AGTypography.SubHeadingM
    )
    InstallViewApkfyRecommendation(
      app = app,
      onInstallStarted = { apkfyAnalytics.sendExp83RecommendationInstallClick() },
    )
  }
}
