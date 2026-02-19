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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.presentation.rememberAppsByTag
import cm.aptoide.pt.feature_campaigns.UTMInfo
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.dto.InstallAction
import com.aptoide.android.aptoidegames.analytics.presentation.AnalyticsContext
import com.aptoide.android.aptoidegames.analytics.presentation.OverrideAnalyticsAPKFY
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.design_system.AptoideGamesSwitch
import com.aptoide.android.aptoidegames.design_system.PrimaryButton
import com.aptoide.android.aptoidegames.drawables.icons.getBonusIconRight
import com.aptoide.android.aptoidegames.drawables.icons.getCrownIcon
import com.aptoide.android.aptoidegames.drawables.icons.getFireIcon
import com.aptoide.android.aptoidegames.drawables.icons.getTrustedIcon
import com.aptoide.android.aptoidegames.error_views.GenericErrorView
import com.aptoide.android.aptoidegames.installer.analytics.AnalyticsInstallPackageInfoMapper
import com.aptoide.android.aptoidegames.installer.analytics.InstallAnalytics
import com.aptoide.android.aptoidegames.installer.analytics.getNetworkType
import com.aptoide.android.aptoidegames.installer.analytics.rememberInstallAnalytics
import com.aptoide.android.aptoidegames.installer.presentation.AppIconWProgress
import com.aptoide.android.aptoidegames.installer.presentation.ProgressText
import com.aptoide.android.aptoidegames.mmp.LocalUTMInfo
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
        title = apkfyState?.data?.app?.name?.let {
          stringResource(
            R.string.apkfy_multi_install_install_title,
            it
          )
        } ?: ""
      )
      Column(
        modifier = Modifier
          .fillMaxSize()
          .height(IntrinsicSize.Max)
      ) {
        apkfyState?.data?.let { apkfyData ->
          val autoOpenDefault =
            (apkfyState as? ApkfyUiState.RobloxCompanionAppsVariant)?.autoOpenDefault ?: false
          val utmMedium = apkfyData.utmMedium?.takeIf { it.isNotEmpty() }
            ?: LocalUTMInfo.current.utmMedium
          val autoOpenUTMMedium = "$utmMedium-${if (autoOpenDefault) "open-on" else "open-off"}"

          WithUTM(
            source = apkfyData.utmSource,
            medium = autoOpenUTMMedium,
            campaign = apkfyData.utmCampaign,
            content = apkfyData.utmContent,
            term = apkfyData.utmTerm,
            navigate = navigate,
          ) {
            RobloxApkfyMultiInstallView(
              apkfyApp = apkfyData.app,
              companionAppsList = apkfyCompanionAppsList,
              autoOpenDefault = autoOpenDefault
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
  autoOpenDefault: Boolean = false,
) {
  val apkfyAnalytics = rememberApkfyAnalytics()
  val installAnalytics = rememberInstallAnalytics()
  LaunchedEffect(Unit) {
    apkfyAnalytics.sendRobloxExp82ApkfyShown()
    apkfyAnalytics.sendApkfyShown()
  }
  var hasSelectedApps by rememberSaveable { mutableStateOf(false) }
  val onSelectApps: () -> Unit = { hasSelectedApps = true }
  var autoOpenEnabled by rememberSaveable { mutableStateOf(autoOpenDefault) }
  var checkDiffCounter by rememberSaveable { mutableIntStateOf(0) }
  var switchCounter by rememberSaveable { mutableIntStateOf(0) }

  val (selectedApps, onToggleApp, install) = rememberCompanionAppsSelection(
    apkfyApp = apkfyApp,
    appList = companionAppsList,
  )

  val onToggleAppWithCounter: (String) -> Unit = { packageName ->
    checkDiffCounter += if (selectedApps.contains(packageName)) -1 else 1
    onToggleApp(packageName)
  }

  val scrollState = rememberScrollState()
  Box(modifier = Modifier.fillMaxSize()) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .height(IntrinsicSize.Max)
    ) {
      CompanionAppItem(
        app = apkfyApp,
        isSelected = selectedApps.contains(apkfyApp.packageName),
        onToggleApp = onToggleAppWithCounter,
        hasSelectedApps = hasSelectedApps,
        shouldShowTrusted = true,
        paddingBottom = 6.dp
      )
      Divider(
        color = Palette.Grey,
        thickness = 1.dp,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
      )
      CompanionAppsSection(
        companionAppsList,
        selectedApps,
        onToggleApp = onToggleAppWithCounter,
        hasSelectedApps,
      )
    }

    if (!hasSelectedApps) {
      Column(
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .fillMaxWidth()
      ) {
        AutoOpenToggleRow(
          enabled = autoOpenEnabled,
          onToggle = {
            autoOpenEnabled = it
            switchCounter += 1
          }
        )
        MultiInstallButton(
          apkfyApp = apkfyApp,
          numberOfSelectedApps = selectedApps.size,
          install = install,
          installAnalytics = installAnalytics,
          onInstallClick = onSelectApps,
          autoOpenDefault = autoOpenDefault,
          autoOpenFinal = autoOpenEnabled,
          checkDiffCounter = checkDiffCounter,
          switchCounter = switchCounter,
          modifier = Modifier
        )
      }
    }
  }
}

@Composable
private fun MultiInstallButton(
  apkfyApp: App,
  numberOfSelectedApps: Int,
  install: (UTMInfo, Boolean) -> Unit,
  installAnalytics: InstallAnalytics,
  onInstallClick: () -> Unit,
  autoOpenDefault: Boolean,
  autoOpenFinal: Boolean,
  checkDiffCounter: Int,
  switchCounter: Int,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val analyticsUIContext = AnalyticsContext.current
  val utmContext = UTMContext.current
  val networkType = context.getNetworkType()
  val totalAppsToInstall = numberOfSelectedApps
  PrimaryButton(
    title = stringResource(R.string.apkfy_multi_install_install_button, totalAppsToInstall),
    onClick = {
      AnalyticsInstallPackageInfoMapper.currentAnalyticsUIContext =
        analyticsUIContext.copy(installAction = InstallAction.INSTALL)
      installAnalytics.sendApkfyRobloxExp82InstallClickEvent(
        numberOfCheckPresses = checkDiffCounter,
        autoOpenDefault = autoOpenDefault,
        autoOpenFinal = autoOpenFinal,
        switchCheckDiff = switchCounter,
        apkfyVariant = if (autoOpenDefault) "multiinstall_auto_on" else "multiinstall_auto_off"
      )
      installAnalytics.sendClickEvent(apkfyApp, analyticsUIContext, networkType)
      install(utmContext, autoOpenFinal)
      onInstallClick()
    },
    modifier = modifier
      .fillMaxWidth()
      .padding(start = 16.dp, end = 16.dp, bottom = 40.dp),
  )
}

@Composable
private fun AutoOpenToggleRow(
  enabled: Boolean,
  onToggle: (Boolean) -> Unit
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .padding(horizontal = 16.dp)
      .height(56.dp)
      .fillMaxWidth()
      .background(Palette.GreyDark)
      .padding(horizontal = 16.dp)
  ) {
    AptoideGamesSwitch(
      checked = enabled,
      onCheckedChanged = onToggle
    )
    Text(
      text = stringResource(R.string.apkfy_multi_install_auto_open),
      style = AGTypography.InputsS,
      color = Palette.White,
      modifier = Modifier
        .weight(1f)
        .padding(start = 12.dp)
    )
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
      .padding(bottom = 160.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .padding(bottom = 16.dp)
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
      CompanionAppItem(
        app = item,
        isSelected = selectedApps.contains(item.packageName),
        onToggleApp = onToggleApp,
        hasSelectedApps = hasSelectedApps,
        isTopPlayAppRecommendation = index == 0
      )
    }
  }
}

@Composable
fun TopPlayedCompanionBanner() {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp),
    modifier = Modifier
      .background(color = Palette.Secondary.copy(alpha = 0.4f))
      .padding(horizontal = 8.dp, vertical = 4.dp)
  ) {
    Image(
      imageVector = getCrownIcon(color = Palette.Yellow),
      contentDescription = null,
      modifier = Modifier.size(12.dp)
    )
    Text(
      text = stringResource(R.string.apkfy_multi_install_highlighted_game_short),
      color = Palette.Yellow,
      style = AGTypography.InputsXXS.copy(fontWeight = Bold)
    )
  }
}

@Composable
fun TrustedBadge() {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(2.dp),
    modifier = Modifier
      .padding(horizontal = 4.dp, vertical = 3.dp)
  ) {
    Image(
      imageVector = getTrustedIcon(Palette.Green),
      contentDescription = null,
      modifier = Modifier.size(16.dp)
    )
    Text(
      text = stringResource(R.string.trusted_badge),
      color = Palette.Green,
      style = AGTypography.InputsXS
    )
  }
}

@Composable
private fun CompanionAppItem(
  app: App,
  isSelected: Boolean = false,
  onToggleApp: (String) -> Unit = {},
  hasSelectedApps: Boolean = false,
  paddingBottom: Dp = 24.dp,
  shouldShowTrusted: Boolean = false,
  isTopPlayAppRecommendation: Boolean = false
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
    if (!hasSelectedApps) {
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
      Spacer(modifier = Modifier.padding(start = 40.dp))
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
        .padding(start = 8.dp)
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
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 8.dp)
      ) {
        if (isTopPlayAppRecommendation) {
          ProgressText(
            modifier = Modifier
              .wrapContentHeight(unbounded = true, align = Alignment.Top),
            app = app,
            showVersionName = false
          )
          TopPlayedCompanionBanner()
        } else {
          ProgressText(
            modifier = Modifier
              .wrapContentHeight(unbounded = true, align = Alignment.Top),
            app = app,
            showVersionName = false
          )
        }
      }
    }
    ApkfyRobloxInstallView(app = app, shouldShowTrusted = shouldShowTrusted)
  }
}
