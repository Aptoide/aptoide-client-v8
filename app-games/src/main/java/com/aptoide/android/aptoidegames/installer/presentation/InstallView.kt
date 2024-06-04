package com.aptoide.android.aptoidegames.installer.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.download_view.presentation.DownloadUiState
import cm.aptoide.pt.download_view.presentation.ExecutionBlocker.CONNECTION
import cm.aptoide.pt.download_view.presentation.ExecutionBlocker.QUEUE
import cm.aptoide.pt.download_view.presentation.ExecutionBlocker.UNMETERED
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import com.aptoide.android.aptoidegames.R.string
import com.aptoide.android.aptoidegames.design_system.PrimaryButton
import com.aptoide.android.aptoidegames.design_system.PrimaryOutlinedButton
import com.aptoide.android.aptoidegames.design_system.PrimarySmallButton
import com.aptoide.android.aptoidegames.design_system.SecondarySmallOutlinedButton
import com.aptoide.android.aptoidegames.drawables.icons.getError
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

@PreviewDark
@Composable
private fun InstallViewProcessingPreview() {
  // A contrast divider to highlight items boundaries
  val divider = @Composable {
    Divider(
      color = Color.Green.copy(alpha = 0.2f),
      thickness = 8.dp
    )
  }
  AptoideTheme(darkTheme = isSystemInDarkTheme()) {
    Column(
      modifier = Modifier.verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.Center,
    ) {
      divider()
      InstallViewContent(installViewState = null.toInstallViewState(randomApp))
      divider()
      InstallViewContent(
        installViewState = DownloadUiState.Install(installWith = {}).toInstallViewState(randomApp)
      )
      divider()
      InstallViewContent(
        installViewState = DownloadUiState.Outdated({}, updateWith = {}, uninstall = {})
          .toInstallViewState(randomApp)
      )
      divider()
      InstallViewContent(
        installViewState = DownloadUiState.Waiting(action = {}, blocker = QUEUE)
          .toInstallViewState(randomApp)
      )
      divider()
      InstallViewContent(
        installViewState = DownloadUiState.Waiting(action = {}, blocker = CONNECTION)
          .toInstallViewState(randomApp)
      )
      divider()
      InstallViewContent(
        installViewState = DownloadUiState.Waiting(action = {}, blocker = UNMETERED)
          .toInstallViewState(randomApp)
      )
      divider()
      InstallViewContent(
        installViewState = DownloadUiState.Downloading(
          size = 830282380,
          downloadProgress = -1,
          cancel = {}
        ).toInstallViewState(randomApp)
      )
      divider()
      InstallViewContent(
        installViewState = DownloadUiState.Downloading(
          size = 830282380,
          downloadProgress = 33,
          cancel = {}
        ).toInstallViewState(randomApp)
      )
      divider()
      InstallViewContent(
        installViewState = DownloadUiState.ReadyToInstall(cancel = {}).toInstallViewState(randomApp)
      )
      divider()
      InstallViewContent(
        installViewState = DownloadUiState.Installing(
          size = 830282302,
          installProgress = -1
        ).toInstallViewState(randomApp)
      )
      divider()
      InstallViewContent(
        installViewState = DownloadUiState.Installing(
          size = 830282302,
          installProgress = 66
        ).toInstallViewState(randomApp)
      )
      divider()
      InstallViewContent(
        installViewState = DownloadUiState.Uninstalling.toInstallViewState(randomApp)
      )
      divider()
      InstallViewContent(
        installViewState = DownloadUiState.Installed({}, {}).toInstallViewState(randomApp)
      )
      divider()
      InstallViewContent(
        installViewState = DownloadUiState.Error(retryWith = {}).toInstallViewState(randomApp)
      )
      divider()
    }
  }
}

@Composable
fun InstallView(
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

  InstallViewContent(
    installViewState = installViewState,
    modifier = modifier.clearAndSetSemantics {
      installViewState.actionLabel?.let {
        onClick(label = it) {
          when (val uiState = installViewState.uiState) {
            is DownloadUiState.Install -> uiState.install
            is DownloadUiState.Outdated -> uiState.update
            is DownloadUiState.Waiting -> uiState.action
            is DownloadUiState.Downloading -> uiState.cancel
            is DownloadUiState.Installed -> uiState.open
            is DownloadUiState.Error -> uiState.retry
            else -> null
          }?.invoke()
          true
        }
      }
      this.contentDescription = installViewState.contentDescription
      stateDescription = installViewState.stateDescription
      liveRegion = LiveRegionMode.Assertive
    },
  )
}

@Composable
private fun InstallViewContent(
  installViewState: InstallViewState,
  modifier: Modifier = Modifier,
  verticalSpacing: Dp = 8.dp,
  horizontalSpacing: Dp = 24.dp,
) = Box(
  modifier = modifier
    .fillMaxWidth()
    .wrapContentHeight(),
) {
  when (val state = installViewState.uiState) {
    null -> Unit
    is DownloadUiState.Install -> PrimaryButton(
      title = installViewState.actionLabel,
      onClick = state.install,
      modifier = Modifier.fillMaxWidth(),
    )

    is DownloadUiState.Outdated -> PrimaryButton(
      title = installViewState.actionLabel,
      onClick = state.update,
      modifier = Modifier.fillMaxWidth(),
    )

    is DownloadUiState.Waiting -> ProgressView(
      title = installViewState.stateDescription,
      verticalSpacing = verticalSpacing,
      horizontalSpacing = horizontalSpacing,
    ) {
      state.action?.let {
        if (state.blocker == UNMETERED) {
          PrimarySmallButton(
            title = installViewState.actionLabel,
            onClick = it,
          )
        } else {
          SecondarySmallOutlinedButton(
            title = installViewState.actionLabel,
            onClick = it,
          )
        }
      }
    }

    is DownloadUiState.Downloading -> ProgressView(
      title = state.getProgressString(),
      progressValue = state.downloadProgress,
      verticalSpacing = verticalSpacing,
      horizontalSpacing = horizontalSpacing,
    ) {
      SecondarySmallOutlinedButton(
        title = installViewState.actionLabel,
        onClick = state.cancel,
      )
    }

    is DownloadUiState.ReadyToInstall -> ProgressView(
      title = installViewState.stateDescription,
      verticalSpacing = verticalSpacing,
      horizontalSpacing = horizontalSpacing,
    ) {
      SecondarySmallOutlinedButton(
        title = installViewState.actionLabel,
        onClick = state.cancel,
      )
    }

    is DownloadUiState.Installing -> ProgressView(
      title = installViewState.stateDescription,
      verticalSpacing = verticalSpacing,
      horizontalSpacing = horizontalSpacing,
    )

    DownloadUiState.Uninstalling -> ProgressView(
      title = installViewState.stateDescription,
      verticalSpacing = verticalSpacing,
      horizontalSpacing = horizontalSpacing,
    )

    is DownloadUiState.Installed -> PrimaryOutlinedButton(
      title = installViewState.actionLabel,
      onClick = state.open,
      modifier = Modifier.fillMaxWidth(),
    )

    is DownloadUiState.Error -> Row(
      modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
    ) {
      InstallViewError(
        modifier = Modifier
          .padding(end = 16.dp)
          .weight(1f)
      )
      PrimaryButton(
        modifier = Modifier.width(136.dp),
        title = installViewState.actionLabel,
        onClick = state.retry,
      )
    }
  }
}

@Composable
private fun InstallViewError(modifier: Modifier = Modifier) {
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

@Composable
private fun ProgressView(
  title: String,
  progressValue: Int? = null,
  verticalSpacing: Dp,
  horizontalSpacing: Dp,
  content: @Composable RowScope.() -> Unit = {},
) {
  val tintColor = Palette.Primary
  Row(
    horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
    verticalAlignment = Alignment.Bottom,
  ) {
    Column(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(verticalSpacing),
    ) {
      Text(
        text = title,
        style = AGTypography.InputsS,
        color = tintColor,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      LinearProgress(
        progress = progressValue?.takeIf { it >= 0 },
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 12.dp)
          .height(8.dp),
        backgroundColor = Palette.Grey,
        color = tintColor
      )
    }
    content()
  }
}

@Composable
private fun LinearProgress(
  progress: Int?,
  backgroundColor: Color,
  color: Color,
  modifier: Modifier = Modifier,
) = progress?.let {
  LinearProgressIndicator(
    progress = it / 100f,
    modifier = modifier,
    backgroundColor = backgroundColor,
    color = color
  )
} ?: LinearProgressIndicator(
  modifier = modifier,
  backgroundColor = backgroundColor,
  color = color
)
