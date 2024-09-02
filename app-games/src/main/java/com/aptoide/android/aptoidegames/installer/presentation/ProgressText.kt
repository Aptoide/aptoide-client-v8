package com.aptoide.android.aptoidegames.installer.presentation

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import cm.aptoide.pt.download_view.presentation.DownloadUiState
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
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import com.aptoide.android.aptoidegames.R.string
import com.aptoide.android.aptoidegames.appview.AppRating
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette
import kotlin.random.Random

@Composable
fun ProgressText(
  modifier: Modifier = Modifier,
  app: App,
  showVersionName: Boolean = false,
) {
  val state = rememberDownloadState(app = app)

  ProgressTextContent(
    modifier = modifier,
    app = app,
    state = state,
    showVersionName = showVersionName
  )
}

@Composable
private fun ProgressTextContent(
  modifier: Modifier = Modifier,
  app: App,
  state: DownloadUiState?,
  showVersionName: Boolean,
) {
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
        modifier = modifier,
        text = text,
        style = AGTypography.InputsS,
        color = Palette.GreyLight,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1
      )
    } else {
      AppRating(rating = app.pRating)
    }

    is Waiting,
    is Downloading,
    is ReadyToInstall,
    is Installing,
    Uninstalling,
    -> Text(
      modifier = modifier,
      text = text,
      style = AGTypography.InputsS,
      color = Palette.Primary,
      overflow = TextOverflow.Ellipsis,
      maxLines = 1
    )

    is Error,
    -> GenericErrorLabel(modifier = modifier)
  }
}

@Composable
fun GenericErrorLabel(modifier: Modifier = Modifier) {
  Text(
    modifier = modifier,
    text = stringResource(string.install_error_short_message),
    style = AGTypography.InputsS,
    color = Palette.Error
  )
}

@PreviewDark
@Composable
fun ProgressTextContentInstalledPreview() {
  AptoideTheme {
    ProgressTextContent(
      app = randomApp,
      state = Installed({}, {}),
      showVersionName = true
    )
  }
}

@PreviewDark
@Composable
fun ProgressTextContentInstallingPreview() {
  AptoideTheme {
    ProgressTextContent(
      app = randomApp,
      state = Downloading(Random.nextLong(500000, 1000000), Random.nextInt(0, 100), {}),
      showVersionName = Random.nextBoolean()
    )
  }
}

@PreviewDark
@Composable
fun ProgressTextContentErrorPreview() {
  AptoideTheme {
    ProgressTextContent(
      app = randomApp,
      state = Error(retryWith = {}),
      showVersionName = Random.nextBoolean()
    )
  }
}
