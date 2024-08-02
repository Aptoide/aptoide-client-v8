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
import com.aptoide.android.aptoidegames.design_system.PrimarySmallButton
import com.aptoide.android.aptoidegames.design_system.PrimarySmallOutlinedButton
import com.aptoide.android.aptoidegames.design_system.SecondarySmallOutlinedButton
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
  onCancel: () -> Unit = {},
  cancelable: Boolean = true,
) {
  val installViewState = installViewStates(
    app = app,
    onInstallStarted = onInstallStarted,
    onCancel = onCancel,
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
    is DownloadUiState.Install -> PrimarySmallButton(
      onClick = state.install,
      title = installViewState.actionLabel,
    )

    is DownloadUiState.Outdated -> PrimarySmallButton(
      onClick = state.update,
      title = installViewState.actionLabel,
    )

    is DownloadUiState.Waiting -> {
      state.action?.let {
        if (state.blocker != UNMETERED && cancelable) {
          SecondarySmallOutlinedButton(
            onClick = it,
            title = installViewState.actionLabel,
          )
        }
      }
    }

    is DownloadUiState.Downloading -> if (cancelable) {
      SecondarySmallOutlinedButton(
        onClick = state.cancel,
        title = installViewState.actionLabel,
      )
    }

    is DownloadUiState.ReadyToInstall -> if (cancelable) {
      SecondarySmallOutlinedButton(
        onClick = state.cancel,
        title = installViewState.actionLabel,
      )
    }

    is DownloadUiState.Installed -> PrimarySmallOutlinedButton(
      onClick = state.open,
      title = installViewState.actionLabel,
    )

    is DownloadUiState.Error -> PrimarySmallButton(
      onClick = state.retry,
      title = installViewState.actionLabel,
    )

    null,
    is DownloadUiState.Installing,
    DownloadUiState.Uninstalling,
    -> Unit
  }
}
