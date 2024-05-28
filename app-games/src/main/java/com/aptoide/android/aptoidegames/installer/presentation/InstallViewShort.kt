package com.aptoide.android.aptoidegames.installer.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.download_view.presentation.DownloadUiState
import cm.aptoide.pt.download_view.presentation.ExecutionBlocker.CONNECTION
import cm.aptoide.pt.download_view.presentation.ExecutionBlocker.QUEUE
import cm.aptoide.pt.download_view.presentation.ExecutionBlocker.UNMETERED
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import com.aptoide.android.aptoidegames.design_system.AppGamesButton
import com.aptoide.android.aptoidegames.design_system.AppGamesOutlinedButton
import com.aptoide.android.aptoidegames.design_system.ButtonStyle.Default
import com.aptoide.android.aptoidegames.design_system.ButtonStyle.Gray
import com.aptoide.android.aptoidegames.theme.AptoideTheme

@PreviewDark
@Composable
fun InstallViewShortPreview() {
  // A contrast divider to highlight items boundaries
  val divider = @Composable {
    Divider(
      color = Color.Green.copy(alpha = 0.2f),
      thickness = 8.dp
    )
  }
  AptoideTheme(darkTheme = isSystemInDarkTheme()) {
    Column(verticalArrangement = Arrangement.Center) {
      divider()
      InstallViewShortContent(installViewState = null.toInstallViewState(randomApp))
      divider()
      InstallViewShortContent(
        installViewState = DownloadUiState.Install(installWith = {}).toInstallViewState(randomApp)
      )
      divider()
      InstallViewShortContent(
        installViewState = DownloadUiState.Outdated({}, updateWith = {}, uninstall = {})
          .toInstallViewState(randomApp)
      )
      divider()
      InstallViewShortContent(
        installViewState = DownloadUiState.Waiting(action = {}, blocker = QUEUE)
          .toInstallViewState(randomApp)
      )
      divider()
      InstallViewShortContent(
        installViewState = DownloadUiState.Waiting(action = {}, blocker = QUEUE)
          .toInstallViewState(randomApp),
        cancelable = false
      )
      divider()
      InstallViewShortContent(
        installViewState = DownloadUiState.Waiting(action = {}, blocker = CONNECTION)
          .toInstallViewState(randomApp)
      )
      divider()
      InstallViewShortContent(
        installViewState = DownloadUiState.Waiting(action = {}, blocker = UNMETERED)
          .toInstallViewState(randomApp)
      )
      divider()
      InstallViewShortContent(
        installViewState = DownloadUiState.Downloading(
          size = 830282380,
          downloadProgress = -1,
          cancel = {}
        ).toInstallViewState(randomApp)
      )
      divider()
      InstallViewShortContent(
        installViewState = DownloadUiState.Downloading(
          size = 830282380,
          downloadProgress = 33,
          cancel = {}
        ).toInstallViewState(randomApp)
      )
      divider()
      InstallViewShortContent(
        installViewState = DownloadUiState.Downloading(
          size = 830282380,
          downloadProgress = -1,
          cancel = {}
        ).toInstallViewState(randomApp),
        cancelable = false
      )
      divider()
      InstallViewShortContent(
        installViewState = DownloadUiState.ReadyToInstall(cancel = {}).toInstallViewState(randomApp)
      )
      divider()
      InstallViewShortContent(
        installViewState = DownloadUiState.ReadyToInstall(cancel = {})
          .toInstallViewState(randomApp),
        cancelable = false
      )
      divider()
      InstallViewShortContent(
        installViewState = DownloadUiState.Installing(
          size = 830282302,
          installProgress = -1
        ).toInstallViewState(randomApp)
      )
      divider()
      InstallViewShortContent(
        installViewState = DownloadUiState.Installing(
          size = 830282302,
          installProgress = 66
        ).toInstallViewState(randomApp)
      )
      divider()
      InstallViewShortContent(
        installViewState = DownloadUiState.Uninstalling.toInstallViewState(randomApp)
      )
      divider()
      InstallViewShortContent(
        installViewState = DownloadUiState.Installed({}, {}).toInstallViewState(randomApp)
      )
      divider()
      InstallViewShortContent(
        installViewState = DownloadUiState.Error(retryWith = {}).toInstallViewState(randomApp)
      )
      divider()
    }
  }
}

@Composable
fun InstallViewShort(
  app: App,
  onInstallStarted: () -> Unit = {},
  cancelable: Boolean = true,
) {
  val installViewState = installViewStates(
    app = app,
    onInstallStarted = onInstallStarted,
  )

  InstallViewShortContent(
    installViewState = installViewState,
    cancelable = cancelable,
  )
}

@Composable
private fun InstallViewShortContent(
  installViewState: InstallViewState,
  cancelable: Boolean = true,
) {
  when (val state = installViewState.uiState) {
    is DownloadUiState.Install -> AppGamesButton(
      title = installViewState.actionLabel,
      onClick = state.install,
      style = Default(fillWidth = false),
    )

    is DownloadUiState.Outdated -> AppGamesButton(
      title = installViewState.actionLabel,
      onClick = state.update,
      style = Default(fillWidth = false),
    )

    is DownloadUiState.Waiting -> {
      state.action?.let {
        if (state.blocker != UNMETERED && cancelable) {
          AppGamesOutlinedButton(
            title = installViewState.actionLabel,
            onClick = it,
            style = Gray(fillWidth = false),
          )
        }
      }
    }

    is DownloadUiState.Downloading -> if (cancelable) {
      AppGamesOutlinedButton(
        title = installViewState.actionLabel,
        onClick = state.cancel,
        style = Gray(fillWidth = false),
      )
    }

    is DownloadUiState.ReadyToInstall -> if (cancelable) {
      AppGamesOutlinedButton(
        title = installViewState.actionLabel,
        onClick = state.cancel,
        style = Gray(fillWidth = false),
      )
    }

    is DownloadUiState.Installed -> AppGamesButton(
      title = installViewState.actionLabel,
      onClick = state.open,
      style = Default(fillWidth = false)
    )

    is DownloadUiState.Error -> AppGamesButton(
      title = installViewState.actionLabel,
      onClick = state.retry,
      style = Default(fillWidth = false),
    )

    null,
    is DownloadUiState.Installing,
    DownloadUiState.Uninstalling,
    -> Unit
  }
}
