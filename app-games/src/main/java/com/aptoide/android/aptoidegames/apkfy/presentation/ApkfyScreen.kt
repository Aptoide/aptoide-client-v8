package com.aptoide.android.aptoidegames.apkfy.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.OverrideAnalyticsAPKFY
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.apkfy.isRoblox
import com.aptoide.android.aptoidegames.appview.AppRatingAndDownloads
import com.aptoide.android.aptoidegames.drawables.backgrounds.myiconpack.getApkfyAppIconBackground
import com.aptoide.android.aptoidegames.error_views.GenericErrorView
import com.aptoide.android.aptoidegames.installer.presentation.AppIconWProgress
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar

const val apkfyScreenRoute = "apkfy"

fun apkfyScreen() = ScreenData.withAnalytics(
  route = apkfyScreenRoute,
  screenAnalyticsName = "Apkfy",
) { _, navigate, navigateBack ->
  val apkfyState = rememberApkfyState()
  val apkfyAnalytics = rememberApkfyAnalytics()

  val scrollState = rememberScrollState()

  BackHandler {
    apkfyAnalytics.sendApkfyScreenBackClicked()
    navigateBack()
  }

  Column {
    AppGamesTopBar(
      navigateBack = {
        apkfyAnalytics.sendApkfyScreenBackClicked()
        navigateBack()
      },
      title = ""
    )
    Column(
      modifier = Modifier
        .fillMaxHeight()
        .verticalScroll(scrollState)
        .height(IntrinsicSize.Max)
    ) {
      apkfyState?.let {
        ApkfyScreen(
          apkfyState = it,
          navigate = navigate,
        )
      } ?: GenericErrorView(navigateBack)
    }
  }
}

@Composable
fun ApkfyScreen(
  apkfyState: ApkfyUiState,
  navigate: (String) -> Unit,
) {
  val apkfyAnalytics = rememberApkfyAnalytics()

  LaunchedEffect(Unit) {
    if (apkfyState is ApkfyUiState.Default) {
      apkfyAnalytics.sendApkfyTimeout()
    } else {
      apkfyAnalytics.sendApkfyShown()
      if (apkfyState.app.isRoblox()) {
        apkfyAnalytics.sendRobloxExp81ApkfyShown()
      }
    }
  }

  OverrideAnalyticsAPKFY(navigate) { navigateTo ->
    Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        modifier = Modifier
          .padding(horizontal = 40.dp)
          .padding(top = 44.dp),
        text = stringResource(id = R.string.apkfy_install_title),
        style = AGTypography.Title,
        color = Palette.White,
        textAlign = TextAlign.Center
      )
      Spacer(modifier = Modifier.height(55.dp))
      ApkfyAppInfo(app = apkfyState.app)
      Spacer(modifier = Modifier.height(17.dp))
      ApkfyInstallView(
        modifier = Modifier
          .fillMaxHeight()
          .padding(horizontal = 16.dp)
          .padding(bottom = 32.dp),
        app = apkfyState.app,
        onInstallStarted = {}
      )
    }
  }
}

@Composable
private fun ApkfyAppInfo(
  modifier: Modifier = Modifier,
  app: App
) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Box(contentAlignment = Alignment.Center) {
      Image(
        imageVector = getApkfyAppIconBackground(),
        contentDescription = null
      )
      AppIconWProgress(
        app = app,
        modifier = Modifier.size(88.dp),
        contentDescription = null,
      )
    }
    Column(
      modifier = Modifier.padding(horizontal = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        modifier = Modifier.padding(top = 30.dp),
        text = app.name,
        style = AGTypography.TitleGames,
        color = Palette.White,
        textAlign = TextAlign.Center,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
      )
      app.developerName?.let {
        Text(
          text = it,
          style = AGTypography.SmallGames,
          color = Palette.White,
          textAlign = TextAlign.Center,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
        )
      }
      AppRatingAndDownloads(
        rating = app.pRating,
        downloads = app.pDownloads
      )
    }
  }
}

@PreviewDark
@Composable
fun ApkfyScreenPreview() {
  AptoideTheme {
    ApkfyScreen(
      apkfyState = ApkfyUiState.Default(randomApp),
      navigate = {},
    )
  }
}
