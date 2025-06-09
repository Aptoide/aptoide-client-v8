package com.aptoide.android.aptoidegames.apkfy.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.download_view.presentation.DownloadUiState
import cm.aptoide.pt.download_view.presentation.ExecutionBlocker.UNMETERED
import cm.aptoide.pt.feature_apps.data.App
import com.aptoide.android.aptoidegames.design_system.AccentButton
import com.aptoide.android.aptoidegames.design_system.PrimaryButton
import com.aptoide.android.aptoidegames.design_system.PrimaryContentButton
import com.aptoide.android.aptoidegames.design_system.PrimaryOutlinedButton
import com.aptoide.android.aptoidegames.design_system.SecondaryOutlinedButton
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewState
import com.aptoide.android.aptoidegames.installer.presentation.installViewStates
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun ApkfyInstallButton(
  app: App,
  modifier: Modifier = Modifier,
  onInstallStarted: () -> Unit = {},
  onCancel: () -> Unit = {},
) {
  val installViewState = installViewStates(
    app = app,
    onInstallStarted = onInstallStarted,
    onCancel = onCancel
  )

  ApkfyInstallButtonContent(
    app = app,
    installViewState = installViewState,
    modifier = modifier,
  )
}

@Composable
private fun ApkfyInstallButtonContent(
  app: App,
  installViewState: InstallViewState,
  modifier: Modifier = Modifier,
) = Box(
  modifier = modifier,
) {
  when (val state = installViewState.uiState) {
    null -> Unit
    is DownloadUiState.Install -> PrimaryContentButton(
      onClick = state.install,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          modifier = Modifier.alignByBaseline(),
          text = installViewState.actionLabel?.uppercase() ?: "",
          style = AGTypography.InputsL,
          color = Palette.Black,
          textAlign = TextAlign.Center,
          fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          modifier = Modifier.alignByBaseline(),
          text = app.name,
          style = AGTypography.InputsM,
          color = Palette.Black,
          textAlign = TextAlign.Center,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }

    is DownloadUiState.Migrate -> AccentButton(
      title = installViewState.actionLabel,
      onClick = state.migrate,
      modifier = Modifier.fillMaxWidth(),
    )

    is DownloadUiState.MigrateAlias -> AccentButton(
      title = installViewState.actionLabel,
      onClick = state.migrateAlias,
      modifier = Modifier.fillMaxWidth(),
    )

    is DownloadUiState.Outdated -> PrimaryContentButton(
      onClick = state.update,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          modifier = Modifier.alignByBaseline(),
          text = installViewState.actionLabel?.uppercase() ?: "",
          style = AGTypography.InputsL,
          color = Palette.Black,
          textAlign = TextAlign.Center,
          fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          modifier = Modifier.alignByBaseline(),
          text = app.name,
          style = AGTypography.InputsM,
          color = Palette.Black,
          textAlign = TextAlign.Center,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }

    is DownloadUiState.Waiting -> state.action?.let {
      if (state.blocker == UNMETERED) {
        PrimaryButton(
          modifier = Modifier.fillMaxWidth(),
          title = installViewState.actionLabel,
          onClick = it,
        )
      } else {
        SecondaryOutlinedButton(
          modifier = Modifier.fillMaxWidth(),
          title = installViewState.actionLabel,
          onClick = it,
        )
      }
    }

    is DownloadUiState.Downloading -> SecondaryOutlinedButton(
      modifier = Modifier
        .fillMaxWidth()
        .requiredHeight(32.dp),
      title = installViewState.actionLabel,
      textStyle = AGTypography.InputsS.copy(color = Palette.White),
      onClick = state.cancel,
    )

    is DownloadUiState.ReadyToInstall -> SecondaryOutlinedButton(
      modifier = Modifier.fillMaxWidth(),
      title = installViewState.actionLabel,
      onClick = state.cancel,
    )

    is DownloadUiState.Installing -> Unit

    is DownloadUiState.Uninstalling -> Unit

    is DownloadUiState.Installed -> PrimaryOutlinedButton(
      modifier = Modifier.fillMaxWidth(),
      title = installViewState.actionLabel,
      onClick = state.open,
    )

    is DownloadUiState.Error -> PrimaryButton(
      modifier = Modifier.fillMaxWidth(),
      title = installViewState.actionLabel,
      onClick = state.retry,
    )
  }
}