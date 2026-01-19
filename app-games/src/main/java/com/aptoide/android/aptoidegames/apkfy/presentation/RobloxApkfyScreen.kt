package com.aptoide.android.aptoidegames.apkfy.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.extensions.toAnnotatedString
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.OverrideAnalyticsAPKFY
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.apkfy.isRoblox
import com.aptoide.android.aptoidegames.appview.AppRatingAndDownloads
import com.aptoide.android.aptoidegames.drawables.icons.getCrownIcon
import com.aptoide.android.aptoidegames.drawables.icons.getRocketIcon
import com.aptoide.android.aptoidegames.drawables.icons.getUpdateDisabledIcon
import com.aptoide.android.aptoidegames.error_views.GenericErrorView
import com.aptoide.android.aptoidegames.mmp.WithUTM
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar

const val robloxApkfyRoute = "robloxApkfyRoute"

fun robloxApkfyScreen() = ScreenData.withAnalytics(
  route = robloxApkfyRoute,
  screenAnalyticsName = "RobloxApkfy",
) { _, navigate, navigateBack ->
  val apkfyState = rememberApkfyState()
  val apkfyAnalytics = rememberApkfyAnalytics()

  val scrollState = rememberScrollState()

  BackHandler {
    apkfyAnalytics.sendApkfyScreenBackClicked()
    navigateBack()
  }

  Column(modifier = Modifier.fillMaxSize()) {
    AppGamesTopBar(
      navigateBack = {
        apkfyAnalytics.sendApkfyScreenBackClicked()
        navigateBack()
      },
      title = stringResource(R.string.apkfy_install_installing)
    )
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .height(IntrinsicSize.Max)
    ) {
      apkfyState?.data?.let { apkfyData ->
        WithUTM(
          source = apkfyData.utmSource,
          medium = apkfyData.utmMedium,
          campaign = apkfyData.utmCampaign,
          content = apkfyData.utmContent,
          term = apkfyData.utmTerm,
          navigate = navigate,
        ) { navigate ->
          RobloxApkfyScreen(
            app = apkfyData.app,
            navigate = navigate,
          )
        }
      } ?: GenericErrorView(navigateBack)
    }
  }
}

@Composable
fun RobloxApkfyScreen(
  app: App,
  navigate: (String) -> Unit,
) {
  val apkfyAnalytics = rememberApkfyAnalytics()
  var showAppRating by remember { mutableStateOf(true) }

  LaunchedEffect(Unit) {
    if (app.isRoblox()) {
      apkfyAnalytics.sendRobloxApkfyShown()
    }
  }

  OverrideAnalyticsAPKFY(navigate) { navigateTo ->
    Column(
      modifier = Modifier.fillMaxHeight(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Spacer(modifier = Modifier.height(16.dp))
      ApkfyAppInfo(app = app) {
        AnimatedVisibility(visible = showAppRating) {
          AppRatingAndDownloads(
            rating = app.pRating,
            downloads = app.pDownloads
          )
        }
        AnimatedVisibility(visible = !showAppRating) {
          InstallProgressText(app = app, textStyle = AGTypography.InputsM)
        }
      }
      Spacer(modifier = Modifier.height(39.dp))
      Column(
        modifier = Modifier.padding(bottom = 88.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
          modifier = Modifier.padding(horizontal = 36.dp),
          text = stringResource(R.string.apkfy_aptoide_games_intro, app.name),
          style = AGTypography.InputsL,
          color = Palette.White,
          textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          modifier = Modifier.padding(horizontal = 36.dp),
          text = stringResource(R.string.apkfy_aptoide_games_intro_2, app.name),
          style = AGTypography.InputsS,
          color = Palette.White,
          textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(24.dp))
        ApkfyInstallButton(
          app = app,
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
          onInstallStarted = { showAppRating = false },
          onCancel = { showAppRating = true }
        )
        Spacer(modifier = Modifier.height(40.dp))
        AptoideGamesBenefitsSection(appName = app.name)
        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}

@Composable
private fun AptoideGamesBenefitsSection(appName: String) {
  var titleWidth by remember { mutableIntStateOf(0) }
  val density = LocalDensity.current

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Text(
      modifier = Modifier.onGloballyPositioned { coordinates ->
        titleWidth = coordinates.size.width
      },
      text = stringResource(R.string.apkfy_aptoide_games_advantages_title),
      style = AGTypography.InputsM,
      color = Palette.Primary,
    )
    Column(
      modifier = if (titleWidth > 0) {
        with(density) {
          Modifier.requiredWidth(titleWidth.toDp())
        }
      } else {
        Modifier
      },
      verticalArrangement = Arrangement.spacedBy(8.dp),
      horizontalAlignment = Alignment.Start
    ) {
      AptoideGamesBenefitItem(icon = getUpdateDisabledIcon(Palette.Error)) {
        val originalString =
          stringResource(id = R.string.apkfy_aptoide_games_advantage_updates_roblox, appName)
        val annotatedString = originalString.toAnnotatedString(SpanStyle(color = Palette.Error))

        Text(
          text = annotatedString,
          style = AGTypography.BodyBold,
          color = Palette.White,
          textAlign = TextAlign.Start,
          maxLines = 2
        )
      }
      AptoideGamesBenefitItem(icon = getCrownIcon(Palette.Primary)) {
        Text(
          text = stringResource(R.string.apkfy_aptoide_games_advantage_faster_updates_roblox),
          style = AGTypography.BodyBold,
          color = Palette.White,
          textAlign = TextAlign.Start,
          maxLines = 2
        )
      }
      AptoideGamesBenefitItem(icon = getRocketIcon(Palette.Primary)) {
        Text(
          text = stringResource(R.string.apkfy_aptoide_games_advantage_space_roblox),
          style = AGTypography.BodyBold,
          color = Palette.White,
          textAlign = TextAlign.Start,
          maxLines = 2
        )
      }
    }
  }
}

@PreviewDark
@Composable
fun RobloxApkfyScreenPreview() {
  AptoideTheme {
    RobloxApkfyScreen(
      app = randomApp,
      navigate = {},
    )
  }
}
