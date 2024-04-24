package cm.aptoide.pt.app_games.installer.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.app_games.theme.AppGamesButton
import cm.aptoide.pt.app_games.theme.AppGamesOutlinedButton
import cm.aptoide.pt.app_games.theme.AppTheme
import cm.aptoide.pt.app_games.theme.AptoideTheme
import cm.aptoide.pt.app_games.theme.ButtonStyle
import cm.aptoide.pt.app_games.theme.ButtonStyle.Gray
import cm.aptoide.pt.download_view.presentation.DownloadUiState
import cm.aptoide.pt.download_view.presentation.ExecutionBlocker.CONNECTION
import cm.aptoide.pt.download_view.presentation.ExecutionBlocker.QUEUE
import cm.aptoide.pt.download_view.presentation.ExecutionBlocker.UNMETERED
import cm.aptoide.pt.extensions.PreviewAll
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp

@PreviewAll
@Composable
fun InstallViewProcessingPreview() {
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
fun InstallViewContent(
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
    is DownloadUiState.Install -> AppGamesButton(
      title = installViewState.actionLabel,
      onClick = state.install,
      style = ButtonStyle.Default(fillWidth = true),
    )

    is DownloadUiState.Outdated -> AppGamesButton(
      title = installViewState.actionLabel,
      onClick = state.update,
      style = ButtonStyle.Default(fillWidth = true),
    )

    is DownloadUiState.Waiting -> ProgressView(
      title = installViewState.stateDescription,
      verticalSpacing = verticalSpacing,
      horizontalSpacing = horizontalSpacing,
    ) {
      state.action?.let {
        if (state.blocker == UNMETERED) {
          AppGamesButton(
            title = installViewState.actionLabel,
            onClick = it,
          )
        } else {
          AppGamesOutlinedButton(
            title = installViewState.actionLabel,
            onClick = it,
            style = Gray(fillWidth = false),
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
      AppGamesOutlinedButton(
        title = installViewState.actionLabel,
        onClick = state.cancel,
        style = Gray(fillWidth = false),
      )
    }

    is DownloadUiState.ReadyToInstall -> ProgressView(
      title = installViewState.stateDescription,
      verticalSpacing = verticalSpacing,
      horizontalSpacing = horizontalSpacing,
    ) {
      AppGamesOutlinedButton(
        title = installViewState.actionLabel,
        onClick = state.cancel,
        style = Gray(fillWidth = false),
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

    is DownloadUiState.Installed -> AppGamesButton(
      title = installViewState.actionLabel,
      onClick = state.open,
      style = ButtonStyle.Red(fillWidth = true)
    )

    is DownloadUiState.Error -> Column(
      modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(space = verticalSpacing)
    ) {
      AppGamesButton(
        title = installViewState.actionLabel,
        onClick = state.retry,
        style = ButtonStyle.Default(fillWidth = true),
      )
      GenericErrorLabel()
    }
  }
}

@Composable
fun ProgressView(
  title: String,
  progressValue: Int? = null,
  verticalSpacing: Dp,
  horizontalSpacing: Dp,
  content: @Composable RowScope.() -> Unit = {},
) {
  val tintColor = AppTheme.colors.primary
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
        style = AppTheme.typography.gameTitleTextCondensed,
        color = tintColor,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      LinearProgress(
        progress = progressValue?.takeIf { it >= 0 },
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 12.dp)
          .height(8.dp)
          .clip(RoundedCornerShape(8.dp)),
        backgroundColor = AppTheme.colors.downloadProgressBarBackgroundColor,
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