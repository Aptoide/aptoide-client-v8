package com.aptoide.android.aptoidegames.apkfy.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import cm.aptoide.pt.download_view.presentation.ExecutionBlocker.UNMETERED
import cm.aptoide.pt.download_view.presentation.downloadUiStates
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import com.aptoide.android.aptoidegames.R.string
import com.aptoide.android.aptoidegames.design_system.AccentButton
import com.aptoide.android.aptoidegames.design_system.PrimaryButton
import com.aptoide.android.aptoidegames.design_system.PrimaryOutlinedButton
import com.aptoide.android.aptoidegames.design_system.SecondaryOutlinedButton
import com.aptoide.android.aptoidegames.drawables.icons.getError
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewState
import com.aptoide.android.aptoidegames.installer.presentation.getProgressString
import com.aptoide.android.aptoidegames.installer.presentation.installViewStates
import com.aptoide.android.aptoidegames.installer.presentation.toInstallViewState
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

@PreviewDark
@Composable
private fun ApkfyInstallViewProcessingPreview() {
  // A contrast divider to highlight items boundaries
  val divider = @Composable {
    Divider(
      color = Color.Green.copy(alpha = 0.2f),
      thickness = 8.dp
    )
  }
  val states = remember { downloadUiStates }
  AptoideTheme(darkTheme = isSystemInDarkTheme()) {
    Column(
      modifier = Modifier.verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.Center,
    ) {
      states.forEach {
        divider()
        ApkfyInstallViewContent(installViewState = it.toInstallViewState(randomApp))
      }
      divider()
    }
  }
}

@Composable
fun ApkfyInstallView(
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

  ApkfyInstallViewContent(
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
private fun ApkfyInstallViewContent(
  installViewState: InstallViewState,
  modifier: Modifier = Modifier,
  verticalSpacing: Dp = 24.dp,
) = Box(
  modifier = modifier
    .fillMaxHeight()
    .wrapContentWidth(),
  contentAlignment = Alignment.BottomCenter
) {
  when (val state = installViewState.uiState) {
    null -> Unit
    is DownloadUiState.Install -> PrimaryButton(
      modifier = Modifier.fillMaxWidth(),
      title = installViewState.actionLabel,
      onClick = state.install,
    )

    is DownloadUiState.Migrate -> AccentButton(
      modifier = Modifier.fillMaxWidth(),
      title = installViewState.actionLabel,
      onClick = state.migrate,
    )

    is DownloadUiState.MigrateAlias -> AccentButton(
      modifier = Modifier.fillMaxWidth(),
      title = installViewState.actionLabel,
      onClick = state.migrateAlias,
    )

    is DownloadUiState.Outdated -> PrimaryButton(
      modifier = Modifier.fillMaxWidth(),
      title = installViewState.actionLabel,
      onClick = state.update,
    )

    is DownloadUiState.Waiting -> ProgressText(
      title = installViewState.stateDescription,
      verticalSpacing = verticalSpacing,
    ) {
      state.action?.let {
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
    }

    is DownloadUiState.Downloading -> ProgressText(
      title = state.getProgressString(),
      verticalSpacing = verticalSpacing,
    ) {
      SecondaryOutlinedButton(
        modifier = Modifier.fillMaxWidth(),
        title = installViewState.actionLabel,
        onClick = state.cancel,
      )
    }

    is DownloadUiState.ReadyToInstall -> ProgressText(
      title = installViewState.stateDescription,
      verticalSpacing = verticalSpacing,
    ) {
      SecondaryOutlinedButton(
        modifier = Modifier.fillMaxWidth(),
        title = installViewState.actionLabel,
        onClick = state.cancel,
      )
    }

    is DownloadUiState.Installing -> ProgressText(
      title = installViewState.stateDescription,
      verticalSpacing = verticalSpacing,
    )

    is DownloadUiState.Uninstalling -> ProgressText(
      title = installViewState.stateDescription,
      verticalSpacing = verticalSpacing,
    )

    is DownloadUiState.Installed -> PrimaryOutlinedButton(
      modifier = Modifier.fillMaxWidth(),
      title = installViewState.actionLabel,
      onClick = state.open,
    )

    is DownloadUiState.Error -> Column(
      modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(verticalSpacing),
    ) {
      ApkfyInstallViewError()
      Spacer(modifier = Modifier.weight(1f))
      PrimaryButton(
        modifier = Modifier.width(136.dp),
        title = installViewState.actionLabel,
        onClick = state.retry,
      )
    }
  }
}

@Composable
private fun ApkfyInstallViewError(modifier: Modifier = Modifier) {
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
private fun ProgressText(
  title: String,
  verticalSpacing: Dp,
  action: @Composable ColumnScope.() -> Unit = {},
) {
  val tintColor = Palette.Primary
  Column(
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = title,
      style = AGTypography.InputsS,
      color = tintColor,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
    Spacer(
      modifier = Modifier
        .height(verticalSpacing)
        .weight(1f)
    )
    action()
  }
}
