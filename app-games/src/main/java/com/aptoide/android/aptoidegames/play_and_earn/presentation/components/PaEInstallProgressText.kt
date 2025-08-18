package com.aptoide.android.aptoidegames.play_and_earn.presentation.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import cm.aptoide.pt.campaigns.domain.PaEApp
import cm.aptoide.pt.campaigns.domain.asNormalApp
import cm.aptoide.pt.campaigns.domain.randomPaEApp
import cm.aptoide.pt.download_view.presentation.DownloadUiState
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Downloading
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Error
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Install
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Installed
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Installing
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Migrate
import cm.aptoide.pt.download_view.presentation.DownloadUiState.MigrateAlias
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Outdated
import cm.aptoide.pt.download_view.presentation.DownloadUiState.ReadyToInstall
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Uninstalling
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Waiting
import cm.aptoide.pt.download_view.presentation.randomInstallPackageInfo
import cm.aptoide.pt.download_view.presentation.rememberDownloadState
import cm.aptoide.pt.extensions.PreviewDark
import com.aptoide.android.aptoidegames.R.string
import com.aptoide.android.aptoidegames.installer.presentation.GenericErrorLabel
import com.aptoide.android.aptoidegames.installer.presentation.getProgressString
import com.aptoide.android.aptoidegames.installer.presentation.getStateDescription
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette
import kotlin.random.Random

@Composable
fun PaEInstallProgressText(
  modifier: Modifier = Modifier,
  app: PaEApp,
) {
  val state = rememberDownloadState(app = app.asNormalApp())

  PaEInstallProgressTextContent(
    modifier = modifier,
    app = app,
    state = state,
  )
}

@Composable
private fun PaEInstallProgressTextContent(
  modifier: Modifier = Modifier,
  app: PaEApp,
  state: DownloadUiState?,
) {
  val text = when (state) {
    is Install,
    is Outdated,
    is Installed,
    is Migrate,
      -> ""

    is Waiting -> state.getStateDescription()
    is Downloading -> state.getProgressString()
    is ReadyToInstall -> stringResource(string.install_waiting_installation_message)
    is Installing -> stringResource(string.install_installing_message)
    is Uninstalling -> stringResource(string.uninstalling)

    else -> ""
  }

  when (state) {
    null -> Unit
    is Install,
    is Outdated,
    is Installed,
    is Migrate,
    is MigrateAlias,
      -> PaEAppXPText(app.progress?.current ?: 0, app.progress?.target ?: 0)

    is Waiting,
    is Downloading,
    is ReadyToInstall,
    is Installing,
    is Uninstalling,
      -> Text(
      modifier = modifier,
      text = text,
      style = AGTypography.InputsS,
      color = Palette.Yellow50,
      overflow = TextOverflow.Ellipsis,
      maxLines = 1
    )

    is DownloadUiState.Error,
      -> GenericErrorLabel(modifier = modifier)
  }
}

@PreviewDark
@Composable
fun ProgressTextContentInstalledPreview() {
  AptoideTheme {
    PaEInstallProgressTextContent(
      app = randomPaEApp,
      state = Installed({}, {}),
    )
  }
}

@PreviewDark
@Composable
fun ProgressTextContentInstallingPreview() {
  AptoideTheme {
    PaEInstallProgressTextContent(
      app = randomPaEApp,
      state = Downloading(randomInstallPackageInfo, Random.nextInt(0, 100), {}),
    )
  }
}

@PreviewDark
@Composable
fun ProgressTextContentErrorPreview() {
  AptoideTheme {
    PaEInstallProgressTextContent(
      app = randomPaEApp,
      state = Error(retryWith = {}),
    )
  }
}
