package com.aptoide.android.aptoidegames.installer.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.R.string
import com.aptoide.android.aptoidegames.theme.AppTheme
import com.aptoide.android.aptoidegames.theme.richOrange
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Downloading
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Error
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Install
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Installed
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Installing
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Outdated
import cm.aptoide.pt.download_view.presentation.DownloadUiState.ReadyToInstall
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Uninstalling
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Waiting
import cm.aptoide.pt.download_view.presentation.rememberDownloadState
import cm.aptoide.pt.feature_apps.data.App

@Composable
fun ProgressText(
  app: App,
  showVersionName: Boolean = true,
) {
  val state = rememberDownloadState(app = app)

  val text = when (state) {
    is Install,
    is Outdated,
    is Installed,
    -> app.versionName

    is Waiting -> state.getStateDescription()
    is Downloading -> state.getProgressString()
    is ReadyToInstall -> stringResource(string.install_waiting_installation_message)
    is Installing -> stringResource(string.install_installing_message)
    Uninstalling -> stringResource(string.uninstalling)

    else -> ""
  }

  when (state) {
    null -> Unit
    is Install,
    is Outdated,
    is Installed,
    -> if (showVersionName) {
      Text(
        text = text,
        style = AppTheme.typography.gameTitleTextCondensed,
        color = AppTheme.colors.secondary,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1
      )
    }

    is Waiting,
    is Downloading,
    is ReadyToInstall,
    is Installing,
    Uninstalling,
    -> Text(
      text = text,
      style = AppTheme.typography.gameTitleTextCondensed,
      color = richOrange,
      overflow = TextOverflow.Ellipsis,
      maxLines = 1
    )

    is Error,
    -> GenericErrorLabel()
  }
}

@Composable
fun GenericErrorLabel(modifier: Modifier = Modifier) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Image(
      contentScale = ContentScale.Inside,
      imageVector = AppTheme.icons.ErrorOutlined,
      contentDescription = null,
      modifier = Modifier
        .padding(end = 4.dp)
        .size(16.dp)
    )
    Text(
      text = stringResource(string.install_error_short_message),
      style = AppTheme.typography.bodyCopyXS,
      color = AppTheme.colors.error
    )
  }
}
