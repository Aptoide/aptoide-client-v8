package com.aptoide.android.aptoidegames.apkfy.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.presentation.rememberAppsByTag
import cm.aptoide.pt.feature_campaigns.UTMInfo
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.dto.AnalyticsUIContext
import com.aptoide.android.aptoidegames.analytics.presentation.AnalyticsContext
import com.aptoide.android.aptoidegames.analytics.presentation.OverrideAnalyticsAPKFY
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.design_system.PrimaryButton
import com.aptoide.android.aptoidegames.drawables.icons.getBonusIconRight
import com.aptoide.android.aptoidegames.drawables.icons.getCrownIcon
import com.aptoide.android.aptoidegames.drawables.icons.getFireIcon
import com.aptoide.android.aptoidegames.error_views.GenericErrorView
import com.aptoide.android.aptoidegames.installer.analytics.getNetworkType
import com.aptoide.android.aptoidegames.installer.presentation.AppIconWProgress
import com.aptoide.android.aptoidegames.installer.presentation.ProgressText
import com.aptoide.android.aptoidegames.mmp.UTMContext
import com.aptoide.android.aptoidegames.mmp.WithUTM
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar

const val robloxMultiInstallApkfyRoute = "robloxMultiInstallApkfyRoute"

fun RobloxApkfyMultiInstallScreen() = ScreenData.withAnalytics(
  route = robloxMultiInstallApkfyRoute,
  screenAnalyticsName = "RobloxApkfyMultiInstall"
) { _, navigate, navigateBack ->
  val apkfyState = rememberApkfyState()
  val apkfyAnalytics = rememberApkfyAnalytics()
  val (apkfyCompanionAppsState, _) = rememberAppsByTag("ab-test-companion-app-bundle")

  val apkfyCompanionAppsList = when (apkfyCompanionAppsState) {
    cm.aptoide.pt.feature_apps.presentation.AppsListUiState.Empty,
    cm.aptoide.pt.feature_apps.presentation.AppsListUiState.Error,
    cm.aptoide.pt.feature_apps.presentation.AppsListUiState.Loading,
    cm.aptoide.pt.feature_apps.presentation.AppsListUiState.NoConnection -> emptyList()

    is cm.aptoide.pt.feature_apps.presentation.AppsListUiState.Idle -> apkfyCompanionAppsState.apps.sortedBy { !it.isAppCoins }
      .take(4)
  }

  BackHandler {
    apkfyAnalytics.sendApkfyScreenBackClicked()
    navigateBack()
  }

  OverrideAnalyticsAPKFY(navigate) {
    Column(modifier = Modifier.fillMaxSize()) {
      AppGamesTopBar(
        navigateBack = {
          apkfyAnalytics.sendApkfyScreenBackClicked()
          navigateBack()
        },
        title = ""
      )
      Column(
        modifier = Modifier
          .fillMaxSize()
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
          ) {
            RobloxApkfyMultiInstallView(
              apkfyApp = apkfyData.app,
              companionAppsList = apkfyCompanionAppsList
            )
          }
        } ?: GenericErrorView(navigateBack)
      }
    }
  }
}

@Composable
fun RobloxApkfyMultiInstallView(
  apkfyApp: App,
  companionAppsList: List<App>,
) {
  val apkfyAnalytics = rememberApkfyAnalytics()
  LaunchedEffect(Unit) {
    apkfyAnalytics.sendRobloxExp81ApkfyShown()
    apkfyAnalytics.sendApkfyShown()
  }
  var hasSelectedApps by rememberSaveable { mutableStateOf(false) }
  val onSelectApps: () -> Unit = { hasSelectedApps = true }

  val (selectedApps, onToggleApp, install) = rememberCompanionAppsSelection(
    apkfyApp = apkfyApp,
    appList = companionAppsList
  )

  val scrollState = rememberScrollState()
  Box(modifier = Modifier.fillMaxSize()) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .height(IntrinsicSize.Max)
    ) {
      RobloxAppCard(apkfyApp)
      CompanionAppsSection(
        companionAppsList,
        selectedApps,
        onToggleApp,
        hasSelectedApps,
      )
    }

    if (!hasSelectedApps) {
      MultiInstallButton(
        selectedApps.size,
        install,
        onSelectApps,
        Modifier.align(Alignment.BottomCenter)
      )
    }
  }
}

@Composable
private fun MultiInstallButton(
  numberOfSelectedApps: Int,
  install: (AnalyticsUIContext, UTMInfo, String) -> Unit,
  onInstallClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val analyticsUIContext = AnalyticsContext.current
  val utmContext = UTMContext.current
  val networkType = context.getNetworkType()
  val totalAppsToInstall = numberOfSelectedApps + 1
  PrimaryButton(
    title = stringResource(R.string.apkfy_multi_install_install_button, totalAppsToInstall),
    onClick = {
      install(analyticsUIContext, utmContext, networkType)
      onInstallClick()
    },
    modifier = modifier
      .fillMaxWidth()
      .padding(start = 16.dp, end = 16.dp, bottom = 40.dp),
  )
}

@Composable
fun RobloxAppCard(apkfyApp: App) {
  Column {
    Text(
      text = stringResource(R.string.apkfy_multi_install_intro_1),
      style = AGTypography.Title,
      modifier = Modifier
        .padding(horizontal = 24.dp)
        .padding(bottom = 16.dp)
        .fillMaxWidth(),
    )
    CompanionAppItem(app = apkfyApp, isSelectable = false)
  }
}

@Composable
fun CompanionAppsSection(
  companionAppsList: List<App>,
  selectedApps: Set<String>,
  onToggleApp: (String) -> Unit,
  hasSelectedApps: Boolean,
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Palette.GreyDark)
      .padding(bottom = 160.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .padding(top = 24.dp, bottom = 16.dp)
    ) {
      Image(
        imageVector = getFireIcon(color = Palette.White),
        contentDescription = null,
        modifier = Modifier.size(32.dp),
      )
      Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        Text(
          text = stringResource(R.string.apkfy_multi_install_intro_2),
          style = AGTypography.InputsL,
        )
        Text(
          text = stringResource(R.string.apkfy_multi_install_intro_3),
          style = AGTypography.BodyBold,
        )
      }
    }

    companionAppsList.forEachIndexed { index, item ->
      if (index == 0) {
        TopPlayedCompanionItem(
          app = item,
          isSelected = selectedApps.contains(item.packageName),
          onToggleApp = onToggleApp,
          hasSelectedApps = hasSelectedApps,
        )
      } else {
        CompanionAppItem(
          app = item,
          isSelected = selectedApps.contains(item.packageName),
          onToggleApp = onToggleApp,
          hasSelectedApps = hasSelectedApps,
        )
      }
    }

  }
}

@Composable fun TopPlayedCompanionItem(
  app: App,
  isSelected: Boolean,
  onToggleApp: (String) -> Unit,
  hasSelectedApps: Boolean
) {
  Column {
    TopPlayedCompanionBanner()
    Box(
      modifier = Modifier
        .padding(bottom = 16.dp)
        .background(color = Palette.Secondary.copy(alpha = 0.2f))
        .height(80.dp)
        .fillMaxWidth(),
      contentAlignment = Alignment.CenterStart
    ) {
      CompanionAppItem(
        app = app,
        isSelected = isSelected,
        onToggleApp = onToggleApp,
        hasSelectedApps = hasSelectedApps,
        paddingBottom = 0.dp
      )
    }
  }
}

@Composable fun TopPlayedCompanionBanner() {
  Row(
    modifier = Modifier
      .height(24.dp)
      .wrapContentWidth()
      .background(Palette.Secondary.copy(alpha = 0.4f))
      .padding(horizontal = 16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Image(
      imageVector = getCrownIcon(color = Palette.Yellow),
      contentDescription = null,
      modifier = Modifier
        .padding(end = 4.dp)
        .size(16.dp),
    )
    Text(
      text = stringResource(R.string.apkfy_multi_install_highlighted_game),
      style = AGTypography.InputsXS,
      color = Palette.Yellow,
    )
  }
}

@Composable
private fun CompanionAppItem(
  app: App,
  isSelected: Boolean = false,
  onToggleApp: (String) -> Unit = {},
  hasSelectedApps: Boolean = false,
  isSelectable: Boolean = true,
  paddingBottom: Dp = 24.dp
) {
  val checked = remember { mutableStateOf(isSelected) }

  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .padding(bottom = paddingBottom)
      .padding(horizontal = 24.dp)
      .height(64.dp)
      .fillMaxWidth()
  ) {
    if (isSelectable && !hasSelectedApps) {
      Checkbox(
        modifier = Modifier
          .padding(end = 16.dp)
          .size(24.dp),
        checked = isSelected,
        onCheckedChange = { isChecked ->
          checked.value = isChecked
          onToggleApp(app.packageName)
        },
        colors = CheckboxDefaults.colors(
          checkedColor = Palette.Primary,
          uncheckedColor = Palette.Primary,
          checkmarkColor = Palette.GreyDark
        )
      )
    } else {
      Spacer(modifier = Modifier.padding(start = if (!isSelectable) 0.dp else 24.dp))
    }
    Box(
      contentAlignment = Alignment.TopEnd
    ) {
      AppIconWProgress(
        app = app,
        contentDescription = null,
        modifier = Modifier.size(64.dp),
      )
      if (app.isAppCoins) {
        Image(
          imageVector = getBonusIconRight(
            iconColor = Palette.Primary,
            outlineColor = Palette.Black,
            backgroundColor = Palette.Secondary
          ),
          contentDescription = null,
          modifier = Modifier.size(32.dp),
        )
      }
    }

    Column(
      modifier = Modifier
        .padding(start = 8.dp, end = 8.dp)
        .weight(1f),
      verticalArrangement = Arrangement.Center
    ) {
      Text(
        modifier = Modifier.wrapContentHeight(unbounded = true),
        text = app.name,
        color = Palette.White,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = AGTypography.DescriptionGames
      )
      ProgressText(
        modifier = Modifier.wrapContentHeight(unbounded = true, align = Alignment.Top),
        app = app,
        showVersionName = false
      )
    }
    ApkfyRobloxInstallView(app)
  }
}
