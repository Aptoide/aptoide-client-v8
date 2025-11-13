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
import cm.aptoide.pt.feature_campaigns.toAptoideMMPCampaign
import cm.aptoide.pt.feature_campaigns.toMMPLinkerCampaign
import cm.aptoide.pt.install_manager.dto.Constraints
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.dto.InstallAction
import com.aptoide.android.aptoidegames.analytics.presentation.AnalyticsContext
import com.aptoide.android.aptoidegames.feature_oos.OutOfSpaceDialog
import com.aptoide.android.aptoidegames.installer.analytics.AnalyticsInstallPackageInfoMapper
import com.aptoide.android.aptoidegames.installer.analytics.getNetworkType
import com.aptoide.android.aptoidegames.installer.analytics.rememberInstallAnalytics
import com.aptoide.android.aptoidegames.installer.analytics.rememberScheduledInstalls
import com.aptoide.android.aptoidegames.installer.installConstraints
import com.aptoide.android.aptoidegames.installer.notifications.rememberInstallerNotifications
import com.aptoide.android.aptoidegames.installer.rememberInstallerQueueHandler
import com.aptoide.android.aptoidegames.installer.wifiInstallConstraints
import com.aptoide.android.aptoidegames.network.presentation.NetworkPreferencesViewModel
import com.aptoide.android.aptoidegames.network.presentation.WifiPromptDialog
import com.aptoide.android.aptoidegames.network.presentation.WifiPromptType
import com.aptoide.android.aptoidegames.network.rememberDownloadOverWifi
import com.aptoide.android.aptoidegames.play_and_earn.presentation.analytics.rememberPaEAnalytics
import com.aptoide.android.aptoidegames.play_and_earn.presentation.permissions.hasOverlayPermission
import com.aptoide.android.aptoidegames.play_and_earn.presentation.permissions.hasUsageStatsPermissionStatus

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
  val installerQueueHandler = rememberInstallerQueueHandler()
  val paeAnalytics = rememberPaEAnalytics()

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
            installAnalytics.sendClickEvent(
              app = app,
              networkType = context.getNetworkType(),
              analyticsContext = analyticsContext.copy(installAction = InstallAction.INSTALL),
            )
            if (analyticsContext.currentScreen != "AppView") {
              app.campaigns?.toAptoideMMPCampaign()
                ?.sendClickEvent(bundleTag = analyticsContext.bundleMeta?.tag, isCta = true)
              app.campaigns?.toAptoideMMPCampaign()
                ?.sendDownloadEvent(
                  bundleTag = analyticsContext.bundleMeta?.tag,
                  searchKeyword = analyticsContext.searchMeta?.searchKeyword,
                  currentScreen = analyticsContext.currentScreen,
                  isCta = true
                )
            } else if (!app.campaigns?.deepLinkUtms?.get("utm_source").isNullOrEmpty()) {
              app.campaigns?.placementType =
                app.campaigns?.deepLinkUtms?.get("utm_content") ?: "appview"

              app.campaigns?.toAptoideMMPCampaign()
                ?.sendDownloadEvent(
                  bundleTag = null,
                  searchKeyword = null,
                  utmCampaign = app.campaigns?.deepLinkUtms?.get("utm_campaign"),
                  currentScreen = app.campaigns?.deepLinkUtms?.get("utm_medium")
                    ?: analyticsContext.currentScreen,
                  utmSourceExterior = app.campaigns?.deepLinkUtms?.get("utm_source")
                )
            } else {
              app.campaigns?.placementType = "appview"

              val campaignId = app.campaigns?.deepLinkUtms?.get("utm_campaign")
              app.campaigns?.toAptoideMMPCampaign()
                ?.sendDownloadEvent(
                  bundleTag = analyticsContext.bundleMeta?.tag,
                  utmCampaign = campaignId,
                  searchKeyword = analyticsContext.searchMeta?.searchKeyword,
                  currentScreen = analyticsContext.currentScreen
                )
            }
            app.campaigns?.toMMPLinkerCampaign()?.sendDownloadEvent()
            onInstallStarted()
            scheduledInstallListener.listenToWifiStart(app.packageName)
            saveAppDetails(app) {
              installerNotifications.onInstallationQueued(app.packageName)
            }
          },
          installWith = {
            AnalyticsInstallPackageInfoMapper.currentAnalyticsUIContext =
              analyticsContext.copy(installAction = InstallAction.INSTALL)
            downloadUiState.installWith(it)
          }
        )

        is DownloadUiState.Outdated -> DownloadUiState.Outdated(
          open = downloadUiState.open,
          resolver = resolver.onResolvedNotNull {
            installAnalytics.sendClickEvent(
              app = app,
              networkType = context.getNetworkType(),
              analyticsContext = analyticsContext.copy(installAction = InstallAction.UPDATE),
            )
            if (analyticsContext.currentScreen != "AppView") {
              app.campaigns?.toAptoideMMPCampaign()
                ?.sendClickEvent(bundleTag = analyticsContext.bundleMeta?.tag, isCta = true)
              app.campaigns?.toAptoideMMPCampaign()
                ?.sendDownloadEvent(
                  bundleTag = analyticsContext.bundleMeta?.tag,
                  searchKeyword = analyticsContext.searchMeta?.searchKeyword,
                  currentScreen = analyticsContext.currentScreen,
                  isCta = true
                )
            } else if (!app.campaigns?.deepLinkUtms?.get("utm_source").isNullOrEmpty()) {
              app.campaigns?.placementType =
                app.campaigns?.deepLinkUtms?.get("utm_content") ?: "appview"

              app.campaigns?.toAptoideMMPCampaign()
                ?.sendDownloadEvent(
                  bundleTag = null,
                  searchKeyword = null,
                  utmCampaign = app.campaigns?.deepLinkUtms?.get("utm_campaign"),
                  currentScreen = app.campaigns?.deepLinkUtms?.get("utm_medium")
                    ?: analyticsContext.currentScreen,
                  utmSourceExterior = app.campaigns?.deepLinkUtms?.get("utm_source")
                )
            } else {
              app.campaigns?.placementType = "appview"

              val campaignId = app.campaigns?.deepLinkUtms?.get("utm_campaign")
              app.campaigns?.toAptoideMMPCampaign()
                ?.sendDownloadEvent(
                  bundleTag = analyticsContext.bundleMeta?.tag,
                  utmCampaign = campaignId,
                  searchKeyword = analyticsContext.searchMeta?.searchKeyword,
                  currentScreen = analyticsContext.currentScreen
                )
            }
            app.campaigns?.toMMPLinkerCampaign()?.sendDownloadEvent()
            onInstallStarted()
            scheduledInstallListener.listenToWifiStart(app.packageName)
            saveAppDetails(app) {
              installerNotifications.onInstallationQueued(app.packageName)
            }
          },
          updateWith = {
            AnalyticsInstallPackageInfoMapper.currentAnalyticsUIContext = analyticsContext
              .copy(installAction = InstallAction.UPDATE)
            downloadUiState.updateWith(it)
          },
          uninstall = {
            installAnalytics.sendClickEvent(
              app = app,
              networkType = context.getNetworkType(),
              analyticsContext = analyticsContext.copy(installAction = InstallAction.UNINSTALL),
            )
            AnalyticsInstallPackageInfoMapper.currentAnalyticsUIContext = analyticsContext
              .copy(installAction = InstallAction.UNINSTALL)
            downloadUiState.uninstall()
          }
        )

        is DownloadUiState.Migrate -> DownloadUiState.Migrate(
          open = downloadUiState.open,
          resolver = resolver.onResolvedNotNull {
            installAnalytics.sendClickEvent(
              app = app,
              networkType = context.getNetworkType(),
              analyticsContext = analyticsContext.copy(installAction = InstallAction.MIGRATE),
            )
            if (analyticsContext.currentScreen != "AppView") {
              app.campaigns?.toAptoideMMPCampaign()
                ?.sendClickEvent(bundleTag = analyticsContext.bundleMeta?.tag, isCta = true)
              app.campaigns?.toAptoideMMPCampaign()
                ?.sendDownloadEvent(
                  bundleTag = analyticsContext.bundleMeta?.tag,
                  searchKeyword = analyticsContext.searchMeta?.searchKeyword,
                  currentScreen = analyticsContext.currentScreen,
                  isCta = true
                )
            } else if (!app.campaigns?.deepLinkUtms?.get("utm_source").isNullOrEmpty()) {
              app.campaigns?.placementType =
                app.campaigns?.deepLinkUtms?.get("utm_content") ?: "appview"

              app.campaigns?.toAptoideMMPCampaign()
                ?.sendDownloadEvent(
                  bundleTag = null,
                  searchKeyword = null,
                  utmCampaign = app.campaigns?.deepLinkUtms?.get("utm_campaign"),
                  currentScreen = app.campaigns?.deepLinkUtms?.get("utm_medium")
                    ?: analyticsContext.currentScreen,
                  utmSourceExterior = app.campaigns?.deepLinkUtms?.get("utm_source")
                )
            } else {
              app.campaigns?.placementType = "appview"

              val campaignId = app.campaigns?.deepLinkUtms?.get("utm_campaign")
              app.campaigns?.toAptoideMMPCampaign()
                ?.sendDownloadEvent(
                  bundleTag = analyticsContext.bundleMeta?.tag,
                  utmCampaign = campaignId,
                  searchKeyword = analyticsContext.searchMeta?.searchKeyword,
                  currentScreen = analyticsContext.currentScreen
                )
            }
            app.campaigns?.toMMPLinkerCampaign()?.sendDownloadEvent()
            onInstallStarted()
            scheduledInstallListener.listenToWifiStart(app.packageName)
            saveAppDetails(app) {
              installerNotifications.onInstallationQueued(app.packageName)
            }
          },
          migrateWith = {
            AnalyticsInstallPackageInfoMapper.currentAnalyticsUIContext = analyticsContext
              .copy(installAction = InstallAction.MIGRATE)
            downloadUiState.migrateWith(it)
          },
          uninstall = {
            installAnalytics.sendClickEvent(
              app = app,
              networkType = context.getNetworkType(),
              analyticsContext = analyticsContext.copy(installAction = InstallAction.UNINSTALL),
            )
            AnalyticsInstallPackageInfoMapper.currentAnalyticsUIContext = analyticsContext
              .copy(installAction = InstallAction.UNINSTALL)
            downloadUiState.uninstall()
          }
        )

        is DownloadUiState.MigrateAlias -> DownloadUiState.MigrateAlias(
          resolver = resolver.onResolvedNotNull {
            installAnalytics.sendClickEvent(
              app = app,
              networkType = context.getNetworkType(),
              analyticsContext = analyticsContext.copy(installAction = InstallAction.MIGRATE_ALIAS),
            )
            if (analyticsContext.currentScreen != "AppView") {
              app.campaigns?.toAptoideMMPCampaign()
                ?.sendClickEvent(bundleTag = analyticsContext.bundleMeta?.tag, isCta = true)
              app.campaigns?.toAptoideMMPCampaign()
                ?.sendDownloadEvent(
                  bundleTag = analyticsContext.bundleMeta?.tag,
                  searchKeyword = analyticsContext.searchMeta?.searchKeyword,
                  currentScreen = analyticsContext.currentScreen,
                  isCta = true
                )
            } else if (!app.campaigns?.deepLinkUtms?.get("utm_source").isNullOrEmpty()) {
              app.campaigns?.placementType =
                app.campaigns?.deepLinkUtms?.get("utm_content") ?: "appview"

              app.campaigns?.toAptoideMMPCampaign()
                ?.sendDownloadEvent(
                  bundleTag = null,
                  searchKeyword = null,
                  utmCampaign = app.campaigns?.deepLinkUtms?.get("utm_campaign"),
                  currentScreen = app.campaigns?.deepLinkUtms?.get("utm_medium")
                    ?: analyticsContext.currentScreen,
                  utmSourceExterior = app.campaigns?.deepLinkUtms?.get("utm_source")
                )
            } else {
              app.campaigns?.placementType = "appview"

              val campaignId = app.campaigns?.deepLinkUtms?.get("utm_campaign")
              app.campaigns?.toAptoideMMPCampaign()
                ?.sendDownloadEvent(
                  bundleTag = analyticsContext.bundleMeta?.tag,
                  utmCampaign = campaignId,
                  searchKeyword = analyticsContext.searchMeta?.searchKeyword,
                  currentScreen = analyticsContext.currentScreen
                )
            }
            app.campaigns?.toMMPLinkerCampaign()?.sendDownloadEvent()
            onInstallStarted()
            scheduledInstallListener.listenToWifiStart(app.packageName)
            saveAppDetails(app) {
              installerNotifications.onInstallationQueued(app.packageName)
            }
          },
          migrateAliasWith = {
            AnalyticsInstallPackageInfoMapper.currentAnalyticsUIContext = analyticsContext
              .copy(installAction = InstallAction.MIGRATE_ALIAS)
            downloadUiState.migrateAliasWith(it)
          }
        )

        is DownloadUiState.Waiting -> DownloadUiState.Waiting(
          installPackageInfo = downloadUiState.installPackageInfo,
          blocker = downloadUiState.blocker,
          action = downloadUiState.action?.let { cancel ->
            when (downloadUiState.blocker) {
              UNMETERED -> { ->
                installAnalytics.sendResumeDownloadClick(
                  app = app,
                  downloadOnlyOverWifiSetting = downloadOnlyOverWifi
                )
                cancel()
                installerQueueHandler.clearRemainingQueue(app.packageName)
              }

              else -> { ->
                canceled = true
                installAnalytics.sendOnInstallationRemovedFromQueue(
                  packageName = app.packageName,
                  installPackageInfo = downloadUiState.installPackageInfo
                )
                installAnalytics.sendDownloadCancel(app, analyticsContext)
                // While task is the queue the Package Downloader will not be called and cancellation
                // will not be caught by the  Package Downloader probe.
                // So we need to call this event here
                installAnalytics.sendDownloadCanceledInQueueEvent(
                  packageName = app.packageName,
                  installPackageInfo = downloadUiState.installPackageInfo
                )
                cancel()
                installerQueueHandler.clearRemainingQueue(app.packageName)
              }
            }
          }
        )

        is DownloadUiState.Downloading -> DownloadUiState.Downloading(
          installPackageInfo = downloadUiState.installPackageInfo,
          downloadProgress = downloadUiState.downloadProgress,
          cancel = {
            canceled = true
            installAnalytics.sendDownloadCancel(app, analyticsContext)
            downloadUiState.cancel()
          }
        )

        is DownloadUiState.ReadyToInstall -> DownloadUiState.ReadyToInstall(
          cancel = {
            canceled = true
            installAnalytics.sendDownloadCancel(app, analyticsContext)
            downloadUiState.cancel()
          }
        )

        is DownloadUiState.Installing,
        is DownloadUiState.Uninstalling,
          -> downloadUiState

        is DownloadUiState.Installed -> DownloadUiState.Installed(
          open = {
            installAnalytics.sendOpenClick(
              packageName = app.packageName,
              hasAPPCBilling = app.isAppCoins,
              analyticsContext = analyticsContext,
            )
            runCatching {
              if (analyticsContext.isPlayAndEarn) {
                paeAnalytics.sendPaEPlayClick(app.packageName, analyticsContext)

                if (context.hasUsageStatsPermissionStatus() && context.hasOverlayPermission()) {
                  paeAnalytics.sendPaEAppLaunched(app.packageName, analyticsContext)
                }
              }
            }

            downloadUiState.open()
          },
          uninstall = {
            installAnalytics.sendClickEvent(
              app = app,
              networkType = context.getNetworkType(),
              analyticsContext = analyticsContext.copy(installAction = InstallAction.UNINSTALL),
            )
            AnalyticsInstallPackageInfoMapper.currentAnalyticsUIContext = analyticsContext
              .copy(installAction = InstallAction.UNINSTALL)
            downloadUiState.uninstall()
          }
        )

        is DownloadUiState.Error -> DownloadUiState.Error(
          resolver = resolver.onResolvedNotNull {
            installAnalytics.sendClickEvent(
              app = app,
              networkType = context.getNetworkType(),
              analyticsContext = analyticsContext.copy(installAction = InstallAction.RETRY),
            )
            onInstallStarted()
            scheduledInstallListener.listenToWifiStart(app.packageName)
            saveAppDetails(app) {
              installerNotifications.onInstallationQueued(app.packageName)
            }
          },
          retryWith = {
            AnalyticsInstallPackageInfoMapper.currentAnalyticsUIContext =
              analyticsContext.copy(installAction = InstallAction.RETRY)
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
    is DownloadUiState.Uninstalling -> stringResource(R.string.uninstalling)
    is DownloadUiState.Installed -> stringResource(R.string.appview_status_installed_talkback)
    is DownloadUiState.Error,
      -> stringResource(R.string.appview_status_failed_talkback)

    is DownloadUiState.Migrate -> stringResource(R.string.appview_status_outdated_talkback)
    is DownloadUiState.MigrateAlias -> stringResource(R.string.appview_status_outdated_talkback)
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
    is DownloadUiState.Uninstalling,
      -> null

    is DownloadUiState.Installed -> stringResource(R.string.button_open_app_title)
    is DownloadUiState.Error -> stringResource(R.string.retry_button)
    is DownloadUiState.Migrate -> stringResource(R.string.button_update_title)
    is DownloadUiState.MigrateAlias -> stringResource(R.string.button_update_title)
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
  val installAnalytics = rememberInstallAnalytics()

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
      installAnalytics.sendWifiPromptShown(
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
        installAnalytics.sendWaitForWifiClicked(
          app = app,
          downloadOnlyOverWifi = downloadOnlyOverWifi
        )
        installWith(wifiInstallConstraints)
      },
      onDownloadNow = {
        hide()
        installAnalytics.sendDownloadNowClicked(
          packageName = app.packageName,
          appSize = app.appSize,
          promptType = "overlay",
          downloadOnlyOverWifi = downloadOnlyOverWifi
        )
        installWith(installConstraints)
      },
      onDismiss = hide
    )
  }

  return { missingSpace, resolve ->
    if (missingSpace > 0) {
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
    TextFormatter.formatBytes(installPackageInfo.filesSize)
  )
}

fun ConstraintsResolver.onResolvedNotNull(onResolved: () -> Unit): ConstraintsResolver =
  { canInstall, resolve ->
    invoke(canInstall) {
      resolve(it)
      onResolved()
    }
  }
