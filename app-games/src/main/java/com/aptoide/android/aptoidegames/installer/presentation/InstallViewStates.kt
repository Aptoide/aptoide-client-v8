package com.aptoide.android.aptoidegames.installer.presentation

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.aptoide_ui.textformatter.TextFormatter
import cm.aptoide.pt.download_view.presentation.ConstraintsResolver
import cm.aptoide.pt.download_view.presentation.DownloadUiState
import cm.aptoide.pt.download_view.presentation.ExecutionBlocker.CONNECTION
import cm.aptoide.pt.download_view.presentation.ExecutionBlocker.QUEUE
import cm.aptoide.pt.download_view.presentation.ExecutionBlocker.UNMETERED
import cm.aptoide.pt.download_view.presentation.rememberDownloadState
import cm.aptoide.pt.extensions.hidable
import cm.aptoide.pt.extensions.isActiveNetworkMetered
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.extensions.toMb
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_campaigns.toMMPLinkerCampaign
import cm.aptoide.pt.install_manager.dto.Constraints
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.getNetworkType
import com.aptoide.android.aptoidegames.analytics.presentation.AnalyticsContext
import com.aptoide.android.aptoidegames.analytics.presentation.rememberGenericAnalytics
import com.aptoide.android.aptoidegames.feature_oos.OutOfSpaceDialog
import com.aptoide.android.aptoidegames.installer.analytics.AnalyticsInstallPackageInfoMapper
import com.aptoide.android.aptoidegames.installer.analytics.rememberInstallAnalytics
import com.aptoide.android.aptoidegames.installer.analytics.rememberScheduledInstalls
import com.aptoide.android.aptoidegames.installer.installConstraints
import com.aptoide.android.aptoidegames.installer.notifications.rememberInstallerNotifications
import com.aptoide.android.aptoidegames.installer.wifiInstallConstraints
import com.aptoide.android.aptoidegames.network.presentation.NetworkPreferencesViewModel
import com.aptoide.android.aptoidegames.network.presentation.WifiPromptDialog
import com.aptoide.android.aptoidegames.network.presentation.WifiPromptType
import com.aptoide.android.aptoidegames.network.rememberDownloadOverWifi

const val MAX_APP_SIZE_METERED_DOWNLOAD = 500

data class InstallViewState(
  val uiState: DownloadUiState?,
  val contentDescription: String,
  val stateDescription: String,
  val actionLabel: String?,
)

@Composable
fun installViewStates(
  app: App,
  onInstallStarted: () -> Unit = {},
  onCancel: () -> Unit = {},
): InstallViewState {
  val context = LocalContext.current
  val analyticsContext = AnalyticsContext.current
  val installAnalytics = rememberInstallAnalytics()
  val downloadUiState = rememberDownloadState(app = app)
  val installerNotifications = rememberInstallerNotifications()
  val (saveAppDetails) = rememberSaveAppDetails()
  val downloadOnlyOverWifi = rememberDownloadOverWifi()
  val scheduledInstallListener = rememberScheduledInstalls()

  var canceled by remember { mutableStateOf(false) }
  LaunchedEffect(key1 = downloadUiState) {
    if (downloadUiState is DownloadUiState.Install && canceled) {
      onCancel()
    }
  }

  val resolver: ConstraintsResolver = installWithChecksResolver(app)

  val uiState: DownloadUiState? by remember(key1 = downloadUiState) {
    derivedStateOf {
      when (downloadUiState) {
        null -> null
        is DownloadUiState.Install -> DownloadUiState.Install(
          resolver = resolver.onResolvedNotNull {
            installAnalytics.sendInstallClickEvent(
              app = app,
              networkType = context.getNetworkType(),
              analyticsContext = analyticsContext,
            )
            app.campaigns?.toMMPLinkerCampaign()?.sendDownloadEvent(BuildConfig.OEMID)
            onInstallStarted()
            scheduledInstallListener.listenToWifiStart(app.packageName)
            saveAppDetails(app) {
              installerNotifications.onInstallationQueued(app.packageName)
            }
          },
          installWith = {
            AnalyticsInstallPackageInfoMapper.currentAnalyticsUIContext = analyticsContext
            downloadUiState.installWith(it)
          }
        )

        is DownloadUiState.Outdated -> DownloadUiState.Outdated(
          open = downloadUiState.open,
          resolver = resolver.onResolvedNotNull {
            installAnalytics.sendUpdateClickEvent(
              app = app,
              networkType = context.getNetworkType(),
              analyticsContext = analyticsContext,
            )
            app.campaigns?.toMMPLinkerCampaign()?.sendDownloadEvent(BuildConfig.OEMID)
            onInstallStarted()
            scheduledInstallListener.listenToWifiStart(app.packageName)
            saveAppDetails(app) {
              installerNotifications.onInstallationQueued(app.packageName)
            }
          },
          updateWith = {
            AnalyticsInstallPackageInfoMapper.currentAnalyticsUIContext = analyticsContext
            downloadUiState.updateWith(it)
          },
          uninstall = {
            AnalyticsInstallPackageInfoMapper.currentAnalyticsUIContext = analyticsContext
            installAnalytics.sendUninstallClick(app.packageName, app.appSize)
            downloadUiState.uninstall()
          }
        )

        is DownloadUiState.Waiting -> DownloadUiState.Waiting(
          blocker = downloadUiState.blocker,
          action = downloadUiState.action?.let {
            when (downloadUiState.blocker) {
              UNMETERED -> { ->
                installAnalytics.sendResumeDownloadClick(
                  packageName = app.packageName,
                  downloadOnlyOverWifiSetting = downloadOnlyOverWifi,
                  appSize = app.appSize
                )
                it()
              }

              else -> { ->
                canceled = true
                installAnalytics.sendDownloadCancel(app.packageName, analyticsContext)
                it()
              }
            }
          }
        )

        is DownloadUiState.Downloading -> DownloadUiState.Downloading(
          size = downloadUiState.size,
          downloadProgress = downloadUiState.downloadProgress,
          cancel = {
            canceled = true
            installAnalytics.sendDownloadCancel(app.packageName, analyticsContext)
            downloadUiState.cancel()
          }
        )

        is DownloadUiState.ReadyToInstall -> DownloadUiState.ReadyToInstall(
          cancel = {
            canceled = true
            installAnalytics.sendDownloadCancel(app.packageName, analyticsContext)
            downloadUiState.cancel()
          }
        )

        is DownloadUiState.Installing,
        DownloadUiState.Uninstalling,
        -> downloadUiState

        is DownloadUiState.Installed -> DownloadUiState.Installed(
          open = {
            installAnalytics.sendOpenClick(
              packageName = app.packageName,
              hasAPPCBilling = app.isAppCoins,
              analyticsContext = analyticsContext,
            )
            downloadUiState.open()
          },
          uninstall = {
            installAnalytics.sendUninstallClick(app.packageName, app.appSize)
            downloadUiState.uninstall()
          }
        )

        is DownloadUiState.Error -> DownloadUiState.Error(
          resolver = resolver.onResolvedNotNull {
            installAnalytics.sendRetryClick(
              app = app,
              networkType = context.getNetworkType(),
              analyticsContext = analyticsContext,
            )
            onInstallStarted()
            scheduledInstallListener.listenToWifiStart(app.packageName)
            saveAppDetails(app) {
              installerNotifications.onInstallationQueued(app.packageName)
            }
          },
          retryWith = {
            AnalyticsInstallPackageInfoMapper.currentAnalyticsUIContext = analyticsContext
            downloadUiState.retryWith(it)
          },
        )
      }
    }
  }

  return uiState.toInstallViewState(app)
}

@Composable
fun DownloadUiState?.toInstallViewState(app: App): InstallViewState {
  val stateDescription: String = when (this) {
    null -> ""
    is DownloadUiState.Install -> stringResource(R.string.appview_status_not_installed_talkback)
    is DownloadUiState.Outdated -> stringResource(R.string.appview_status_outdated_talkback)
    is DownloadUiState.Waiting -> getStateDescription()
    is DownloadUiState.Downloading -> getProgressString(R.string.notification_downloading_body)
    is DownloadUiState.ReadyToInstall -> stringResource(
      R.string.install_waiting_installation_message
    )

    is DownloadUiState.Installing -> stringResource(R.string.install_installing_message)
    DownloadUiState.Uninstalling -> stringResource(R.string.uninstalling)
    is DownloadUiState.Installed -> stringResource(R.string.appview_status_installed_talkback)
    is DownloadUiState.Error,
    -> stringResource(R.string.appview_status_failed_talkback)
  }

  val actionDescription: String? = when (this) {
    is DownloadUiState.Install -> stringResource(R.string.button_install_title)
    is DownloadUiState.Outdated -> stringResource(R.string.button_update_title)
    is DownloadUiState.Waiting -> getActionDescription()
      .takeUnless { action == null }

    is DownloadUiState.Downloading,
    is DownloadUiState.ReadyToInstall,
    -> stringResource(R.string.appview_action_cancel_talkback)

    is DownloadUiState.Installing,
    DownloadUiState.Uninstalling,
    -> null

    is DownloadUiState.Installed -> stringResource(R.string.button_open_app_title)
    is DownloadUiState.Error -> stringResource(R.string.retry_button)
    null -> null
  }

  val contentDescription: String = stringResource(R.string.appview_installer_talkback, app.name)
    .plus(
      actionDescription
        ?.let { stringResource(R.string.appview_action_double_tap_talkback, it) }
        ?: ""
    )

  return InstallViewState(
    uiState = this,
    contentDescription = contentDescription,
    stateDescription = stateDescription,
    actionLabel = actionDescription
  )
}

@Composable
private fun installWithChecksResolver(app: App): ConstraintsResolver = runPreviewable(
  preview = {
    { _, _ -> }
  },
  real = {
    installWithRealChecks(app = app)
  }
)

@Composable
private fun installWithRealChecks(app: App): ConstraintsResolver {
  val context = LocalContext.current
  val genericAnalytics = rememberGenericAnalytics()

  val networkPreferencesViewModel = hiltViewModel<NetworkPreferencesViewModel>()
  val downloadOnlyOverWifi by networkPreferencesViewModel.downloadOnlyOverWifi.collectAsState()

  val (showOutOfSpace) = hidable<Unit> { hide, _ ->
    OutOfSpaceDialog(
      app = app,
      onDismiss = hide
    )
  }

  val (showWifiPromptDialog) = hidable<(constraints: Constraints) -> Unit> { hide, installWith ->
    LaunchedEffect(Unit) {
      genericAnalytics.sendWifiPromptShown(
        app = app,
        downloadOnlyOverWifiSetting = downloadOnlyOverWifi
      )
    }
    WifiPromptDialog(
      type = if (downloadOnlyOverWifi) {
        WifiPromptType.UNMETERED_WIFI_ONLY
      } else {
        WifiPromptType.UNMETERED_LARGE_FILE
      },
      size = app.appSize,
      onWaitForWifi = {
        hide()
        genericAnalytics.sendWaitForWifiClicked(
          app = app,
          downloadOnlyOverWifi = downloadOnlyOverWifi
        )
        installWith(wifiInstallConstraints)
      },
      onDownloadNow = {
        hide()
        genericAnalytics.sendDownloadNowClicked(
          downloadOnlyOverWifi = downloadOnlyOverWifi,
          promptType = "overlay",
          packageName = app.packageName,
          appSize = app.appSize
        )
        installWith(installConstraints)
      },
      onDismiss = hide
    )
  }

  return { missingSpace, resolve ->
    if (missingSpace > 0) {
      genericAnalytics.sendNotEnoughSpaceDialogShow(app.packageName, app.appSize)
      showOutOfSpace(Unit)
    } else {
      if (
        context.isActiveNetworkMetered &&
        (downloadOnlyOverWifi || app.appSize.toMb() > MAX_APP_SIZE_METERED_DOWNLOAD)
      ) {
        showWifiPromptDialog(resolve)
      } else if (downloadOnlyOverWifi) {
        resolve(wifiInstallConstraints)
      } else {
        resolve(installConstraints)
      }
    }
  }
}

@Composable
fun DownloadUiState.Waiting.getActionDescription() = stringResource(
  when (blocker) {
    UNMETERED -> R.string.resume_button
    else -> R.string.appview_action_cancel_talkback
  }
)

@Composable
fun DownloadUiState.Waiting.getStateDescription() = stringResource(
  when (blocker) {
    QUEUE -> R.string.install_waiting_download_message
    CONNECTION -> R.string.waiting_for_connection
    UNMETERED -> R.string.notification_waiting_for_wifi_title
  }
)

@Composable
fun DownloadUiState.Downloading.getProgressString(
  @StringRes resourceId: Int = R.string.downloading_percentage_body,
) = if (downloadProgress < 0) {
  stringResource(R.string.install_downloading_message)
} else {
  stringResource(
    resourceId,
    downloadProgress.toString(),
    TextFormatter.formatBytes(size)
  )
}

fun ConstraintsResolver.onResolvedNotNull(onResolved: () -> Unit): ConstraintsResolver =
  { canInstall, resolve ->
    invoke(canInstall) {
      resolve(it)
      onResolved()
    }
  }
