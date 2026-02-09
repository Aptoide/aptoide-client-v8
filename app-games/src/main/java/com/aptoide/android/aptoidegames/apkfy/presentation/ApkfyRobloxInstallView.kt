package com.aptoide.android.aptoidegames.apkfy.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.download_view.presentation.DownloadUiState
import cm.aptoide.pt.download_view.presentation.ExecutionBlocker.UNMETERED
import cm.aptoide.pt.download_view.presentation.downloadUiStates
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import com.aptoide.android.aptoidegames.design_system.PrimarySmallButton
import com.aptoide.android.aptoidegames.design_system.PrimarySmallOutlinedButton
import com.aptoide.android.aptoidegames.design_system.SecondarySmallOutlinedButton
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewState
import com.aptoide.android.aptoidegames.installer.presentation.installViewStates
import com.aptoide.android.aptoidegames.installer.presentation.toInstallViewState
import com.aptoide.android.aptoidegames.theme.AptoideTheme

@PreviewDark
@Composable
fun ApkfyRobloxInstallViewPreview(
  app: App,
  onInstallStarted: () -> Unit = {},
  onCancel: () -> Unit = {},
  cancelable: Boolean = false
) {
  // A contrast divider to highlight items boundaries
  val divider = @Composable {
    Divider(
      color = Color.Green.copy(alpha = 0.2f),
      thickness = 8.dp
    )
  }
  val states = remember { downloadUiStates }
  AptoideTheme(darkTheme = isSystemInDarkTheme()) {
    Column(verticalArrangement = Arrangement.Center) {
      states.forEach {
        divider()
        ApkfyMultiInstallViewContent(installViewState = it.toInstallViewState(randomApp))
      }
      divider()
    }
  }
}

@Composable
fun ApkfyRobloxInstallView(
  app: App,
  onInstallStarted: () -> Unit = {},
  onCancel: () -> Unit = {},
  cancelable: Boolean = false,
  shouldShowTrusted: Boolean = false,
) {
  val installViewState = installViewStates(
    app = app,
    onInstallStarted = onInstallStarted,
    onCancel = onCancel,
  )

  ApkfyMultiInstallViewContent(
    installViewState = installViewState,
    cancelable = cancelable,
    shouldShowTrusted = shouldShowTrusted
  )
}

@Composable
private fun ApkfyMultiInstallViewContent(
  installViewState: InstallViewState,
  cancelable: Boolean = false,
  shouldShowTrusted: Boolean = false
) {
  when (val state = installViewState.uiState) {
    is DownloadUiState.Install,
    is DownloadUiState.Migrate,
    is DownloadUiState.MigrateAlias,
    is DownloadUiState.Outdated -> {
      if (shouldShowTrusted) {
        TrustedBadge()
      }
    }

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
    is DownloadUiState.Uninstalling,
      -> Unit
  }
}
