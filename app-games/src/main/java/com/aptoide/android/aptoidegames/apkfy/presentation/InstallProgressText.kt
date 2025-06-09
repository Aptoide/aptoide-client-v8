package com.aptoide.android.aptoidegames.apkfy.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.download_view.presentation.DownloadUiState
import cm.aptoide.pt.feature_apps.data.App
import com.aptoide.android.aptoidegames.R.string
import com.aptoide.android.aptoidegames.drawables.icons.getError
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewState
import com.aptoide.android.aptoidegames.installer.presentation.getProgressString
import com.aptoide.android.aptoidegames.installer.presentation.installViewStates
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun InstallProgressText(
  app: App,
  modifier: Modifier = Modifier,
  textStyle: TextStyle = AGTypography.InputsS,
  onInstallStarted: () -> Unit = {},
  onCancel: () -> Unit = {},
) {
  val installViewState = installViewStates(
    app = app,
    onInstallStarted = onInstallStarted,
    onCancel = onCancel
  )

  InstallProgressViewContent(
    installViewState = installViewState,
    modifier = modifier,
    textStyle = textStyle
  )
}

@Composable
private fun InstallProgressViewContent(
  installViewState: InstallViewState,
  modifier: Modifier = Modifier,
  textStyle: TextStyle
) = Box(modifier = modifier) {
  when (val state = installViewState.uiState) {
    null -> Unit
    is DownloadUiState.Install,
    is DownloadUiState.Migrate,
    is DownloadUiState.MigrateAlias,
    is DownloadUiState.Outdated,
    is DownloadUiState.Installed -> Unit

    is DownloadUiState.Downloading -> ProgressText(
      title = state.getProgressString(),
      textStyle
    )

    is DownloadUiState.Waiting,
    is DownloadUiState.ReadyToInstall,
    is DownloadUiState.Installing,
    is DownloadUiState.Uninstalling -> ProgressText(
      title = installViewState.stateDescription,
      textStyle
    )

    is DownloadUiState.Error -> InstallErrorText()
  }
}

@Composable
private fun ProgressText(
  title: String,
  textStyle: TextStyle = AGTypography.InputsS
) {
  val tintColor = Palette.Primary
  Text(
    text = title,
    style = textStyle,
    color = tintColor,
    maxLines = 1,
    overflow = TextOverflow.Ellipsis,
  )
}

@Composable
private fun InstallErrorText(modifier: Modifier = Modifier) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Image(
      contentScale = ContentScale.Inside,
      imageVector = getError(Palette.Error),
      contentDescription = null,
      modifier = Modifier
        .padding(end = 4.dp)
        .size(16.dp)
    )
    Text(
      text = stringResource(string.install_error_short_message),
      style = AGTypography.InputsM,
      color = Palette.Error
    )
  }
}
