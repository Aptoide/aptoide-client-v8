package com.aptoide.android.aptoidegames.installer.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.download_view.presentation.DownloadUiState
import cm.aptoide.pt.download_view.presentation.ExecutionBlocker.UNMETERED
import cm.aptoide.pt.download_view.presentation.downloadUiStates
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import com.aptoide.android.aptoidegames.design_system.AccentSmallButton
import com.aptoide.android.aptoidegames.design_system.PrimarySmallButton
import com.aptoide.android.aptoidegames.design_system.PrimarySmallOutlinedButton
import com.aptoide.android.aptoidegames.design_system.SecondarySmallOutlinedButton
import com.aptoide.android.aptoidegames.installer.FEED_INSTALL_DIVERTS_TO_APPVIEW
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
  val states = remember { downloadUiStates }
  AptoideTheme(darkTheme = isSystemInDarkTheme()) {
    Column(verticalArrangement = Arrangement.Center) {
      states.forEach {
        divider()
        InstallViewShortContent(installViewState = it.toInstallViewState(randomApp))
      }
      divider()
    }
  }
}

@Composable
fun InstallViewShort(
  app: App,
  onInstallStarted: () -> Unit = {},
  onCancel: () -> Unit = {},
  onOpen: () -> Unit = {},
  cancelable: Boolean = true,
  prefetchPlayCatalog: Boolean = false,
  onNavigateToAppView: (() -> Unit)? = null,
) {
  val installViewState = installViewStates(
    app = app,
    onInstallStarted = onInstallStarted,
    onCancel = onCancel,
    prefetchPlayCatalog = prefetchPlayCatalog,
  )

  InstallViewShortContent(
    installViewState = installViewState,
    onOpen = onOpen,
    cancelable = cancelable,
    onNavigateToAppView = onNavigateToAppView,
  )
}

@Composable
private fun InstallViewShortContent(
  installViewState: InstallViewState,
  modifier: Modifier = Modifier,
  onOpen: () -> Unit = {},
  cancelable: Boolean = true,
  onNavigateToAppView: (() -> Unit)? = null,
) = Column(horizontalAlignment = Alignment.CenterHorizontally) {
  // Non-null only where this card must hand the install over to AppView instead of starting
  // it here - see [appViewDiversion]
  val divert = installViewState.uiState.appViewDiversion(
    onNavigateToAppView = onNavigateToAppView,
    divertsToAppView = FEED_INSTALL_DIVERTS_TO_APPVIEW,
  )
  when (val state = installViewState.uiState) {
    is DownloadUiState.Install -> PrimarySmallButton(
      onClick = divert ?: state.install,
      modifier = modifier,
      title = installViewState.actionLabel,
    )

    is DownloadUiState.Migrate -> AccentSmallButton(
      onClick = divert ?: state.migrate,
      modifier = modifier,
      title = installViewState.actionLabel,
    )

    is DownloadUiState.MigrateAlias -> AccentSmallButton(
      onClick = divert ?: state.migrateAlias,
      modifier = modifier,
      title = installViewState.actionLabel,
    )

    is DownloadUiState.Outdated -> PrimarySmallButton(
      onClick = divert ?: state.update,
      modifier = modifier,
      title = installViewState.actionLabel,
    )

    is DownloadUiState.Waiting -> {
      state.action?.let {
        if (state.blocker != UNMETERED && cancelable) {
          SecondarySmallOutlinedButton(
            onClick = it,
            modifier = modifier,
            title = installViewState.actionLabel,
          )
        }
      }
    }

    is DownloadUiState.Downloading -> if (cancelable) {
      SecondarySmallOutlinedButton(
        onClick = state.cancel,
        modifier = modifier,
        title = installViewState.actionLabel,
      )
    }

    is DownloadUiState.ReadyToInstall -> if (cancelable) {
      SecondarySmallOutlinedButton(
        onClick = state.cancel,
        modifier = modifier,
        title = installViewState.actionLabel,
      )
    }

    is DownloadUiState.Installed -> PrimarySmallOutlinedButton(
      onClick = {
        onOpen()
        state.open()
      },
      modifier = modifier,
      title = installViewState.actionLabel,
    )

    is DownloadUiState.Error -> PrimarySmallButton(
      onClick = divert ?: state.retry,
      modifier = modifier,
      title = installViewState.actionLabel,
    )

    null,
    is DownloadUiState.Installing,
    is DownloadUiState.Uninstalling,
      -> Unit
  }
  if (installViewState.showPlayAttribution) {
    PlayAttributionLabel(modifier = Modifier.padding(top = 2.dp))
  }
}
