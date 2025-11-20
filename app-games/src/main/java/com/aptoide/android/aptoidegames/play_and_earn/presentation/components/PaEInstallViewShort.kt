package com.aptoide.android.aptoidegames.play_and_earn.presentation.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.campaigns.domain.PaEApp
import cm.aptoide.pt.campaigns.domain.asNormalApp
import cm.aptoide.pt.download_view.presentation.DownloadUiState
import cm.aptoide.pt.download_view.presentation.ExecutionBlocker.UNMETERED
import cm.aptoide.pt.download_view.presentation.downloadUiStates
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.feature_apps.data.randomApp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.design_system.SecondarySmallOutlinedButton
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewState
import com.aptoide.android.aptoidegames.installer.presentation.installViewStates
import com.aptoide.android.aptoidegames.installer.presentation.toInstallViewState
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rememberPlayAndEarnSetupRoute
import com.aptoide.android.aptoidegames.play_and_earn.rememberPlayAndEarnReady
import com.aptoide.android.aptoidegames.theme.AptoideTheme

@PreviewDark
@Composable
private fun PaEInstallViewShortPreview() {
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
        PaEInstallViewShortContent(installViewState = it.toInstallViewState(randomApp))
      }
      divider()
    }
  }
}

@Composable
fun PaEInstallViewShort(
  app: PaEApp,
  onInstallStarted: () -> Unit = {},
  onCancel: () -> Unit = {},
  cancelable: Boolean = true,
  navigate: ((String) -> Unit)? = null,
) {
  val installViewState = installViewStates(
    app = app.asNormalApp(),
    onInstallStarted = onInstallStarted,
    onCancel = onCancel,
  )

  PaEInstallViewShortContent(
    installViewState = installViewState,
    cancelable = cancelable,
    navigate = navigate,
  )
}

@Composable
private fun PaEInstallViewShortContent(
  installViewState: InstallViewState,
  navigate: ((String) -> Unit)? = null,
  cancelable: Boolean = true,
) {
  when (val state = installViewState.uiState) {
    is DownloadUiState.Install -> PaESmallCoinButton(
      onClick = state.install,
      title = installViewState.actionLabel ?: "",
    )

    is DownloadUiState.Migrate -> PaESmallCoinButton(
      onClick = state.migrate,
      title = installViewState.actionLabel ?: "",
    )

    is DownloadUiState.MigrateAlias -> PaESmallCoinButton(
      onClick = state.migrateAlias,
      title = installViewState.actionLabel ?: "",
    )

    is DownloadUiState.Outdated -> PaESmallCoinButton(
      onClick = state.update,
      title = installViewState.actionLabel ?: "",
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

    is DownloadUiState.Installed -> PaESmallPlayButton(
      onClick = state.open,
      navigate = navigate
    )

    is DownloadUiState.Error -> PaESmallTextButton(
      onClick = state.retry,
      title = installViewState.actionLabel ?: "",
    )

    null,
    is DownloadUiState.Installing,
    is DownloadUiState.Uninstalling,
      -> Unit
  }
}

@Composable
private fun PaESmallPlayButton(
  onClick: () -> Unit,
  navigate: ((String) -> Unit)?,
) {
  val isPaEReady = rememberPlayAndEarnReady()
  val paeSetupRoute = rememberPlayAndEarnSetupRoute()

  PaESmallCoinButton(
    onClick = {
      if (isPaEReady || navigate == null) {
        onClick()
      } else {
        navigate(paeSetupRoute)
      }
    },
    title = stringResource(R.string.play_and_earn_play_button),
  )
}
