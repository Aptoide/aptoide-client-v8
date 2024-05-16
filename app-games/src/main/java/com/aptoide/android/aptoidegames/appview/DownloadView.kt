package com.aptoide.android.aptoidegames.appview

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Outlined
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.AppTheme
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import cm.aptoide.pt.aptoide_ui.textformatter.TextFormatter
import cm.aptoide.pt.download_view.presentation.DownloadUiState
import cm.aptoide.pt.download_view.presentation.rememberDownloadState
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp

@PreviewDark
@Composable
fun DownloadPreview() {
  // A contrast divider to highlight items boundaries
  val divider = @Composable {
    Divider(
      color = Color.Green.copy(alpha = 0.2f),
      thickness = 8.dp
    )
  }
  AptoideTheme(darkTheme = isSystemInDarkTheme()) {
    val app = randomApp.copy(isAppCoins = false)
    val states = listOf(
      DownloadUiState.Install(installWith = {}),
      DownloadUiState.Outdated({}, updateWith = {}, uninstall = {}),
      DownloadUiState.Waiting(action = null),
      DownloadUiState.Downloading(33, cancel = {}),
      DownloadUiState.ReadyToInstall(cancel = {}),
      DownloadUiState.Installing(66),
      DownloadUiState.Uninstalling,
      DownloadUiState.Installed({}, {}),
      DownloadUiState.Error(retryWith = {})
    )
    LazyColumn {
      states.forEach { uiState ->
        item {
          MainDownloadView {
            DownloadState(
              uiState = uiState,
              tintColor = if (app.isAppCoins) {
                AppTheme.colors.appCoinsColor
              } else {
                AppTheme.colors.primary
              },
              appSize = app.appSize,
            )
          }
          divider()
        }
      }
      item { divider() }
    }
  }
}

@PreviewDark
@Composable
fun DownloadAppcPreview() {
  // A contrast divider to highlight items boundaries
  val divider = @Composable {
    Divider(
      color = Color.Green.copy(alpha = 0.2f),
      thickness = 8.dp
    )
  }
  AptoideTheme(darkTheme = isSystemInDarkTheme()) {
    val app = randomApp.copy(isAppCoins = true)
    val states = listOf(
      null,
      DownloadUiState.Install(installWith = {}),
      DownloadUiState.Outdated(open = {}, updateWith = {}, uninstall = {}),
      DownloadUiState.Waiting(action = null),
      DownloadUiState.Downloading(size = 33, cancel = {}),
      DownloadUiState.ReadyToInstall(cancel = {}),
      DownloadUiState.Installing(size = 66),
      DownloadUiState.Uninstalling,
      DownloadUiState.Installed({}, {}),
      DownloadUiState.Error(retryWith = {}),
    )
    LazyColumn {
      states.forEach { uiState ->
        item {
          MainDownloadView {
            DownloadState(
              uiState = uiState,
              tintColor = if (app.isAppCoins) {
                AppTheme.colors.appCoinsColor
              } else {
                AppTheme.colors.primary
              },
              appSize = app.appSize,
            )
          }
          divider()
        }
      }
      item { divider() }
    }
  }
}

@Composable
fun DownloadViewScreen(app: App) {

  val uiState = rememberDownloadState(app = app)

  MainDownloadView {
    DownloadState(
      uiState = uiState,
      tintColor = if (app.isAppCoins) {
        AppTheme.colors.appCoinsColor
      } else {
        AppTheme.colors.primary
      },
      appSize = app.appSize,
    )
  }
}

@Composable
fun MainDownloadView(
  installButton: @Composable () -> Unit,
) {
  Card(
    modifier = Modifier
      .padding(start = 16.dp, end = 16.dp)
      .fillMaxWidth()
      .height(56.dp)
      .clip(RoundedCornerShape(16.dp)),
    elevation = 6.dp
  ) {
    Column {
      installButton()
    }
  }
}

@Composable
fun DownloadState(
  uiState: DownloadUiState?,
  tintColor: Color,
  appSize: Long,
) {
  when (uiState) {
    null -> Unit
    is DownloadUiState.Install -> InstallButton(uiState.install)
    is DownloadUiState.Outdated -> InstallButton(uiState.update)

    is DownloadUiState.Waiting -> IndeterminateDownloadView(
      label = "Downloading",
      labelColor = tintColor,
      progressColor = tintColor
    )

    is DownloadUiState.Downloading -> DownloadingDownloadView(
      tintColor = tintColor,
      progress = uiState.downloadProgress.toFloat(),
      appSize = appSize,
      onCloseClick = uiState.cancel
    )

    is DownloadUiState.Installing -> IndeterminateDownloadView(
      label = "Installing",
      labelColor = tintColor,
      progressColor = tintColor
    )

    is DownloadUiState.Uninstalling -> IndeterminateDownloadView(
      label = "Uninstalling",
      labelColor = tintColor,
      progressColor = tintColor
    )

    is DownloadUiState.Installed -> OpenButton(uiState.open)
    is DownloadUiState.Error -> ErrorDownloadView(uiState.retry)
    is DownloadUiState.ReadyToInstall -> IndeterminateDownloadView(
      label = "Waiting for install",
      labelColor = tintColor,
      progressColor = tintColor
    )
  }
  if (
    uiState !is DownloadUiState.Install &&
    uiState !is DownloadUiState.Installed &&
    uiState !is DownloadUiState.Outdated
  ) {
    Divider(
      color = AppTheme.colors.dividerColor,
      thickness = 1.dp
    )
  }
}

@Composable
fun InstallButton(onClick: () -> Unit) {
  Button(
    onClick = onClick,
    shape = RoundedCornerShape(16.dp),
    modifier = Modifier
      .height(56.dp)
      .fillMaxWidth()
  ) {
    Text(
      text = "INSTALL",
      maxLines = 1,
      style = AppTheme.typography.button_L,
      color = Color.White
    )
  }
}

@Composable
fun ErrorDownloadView(onClick: () -> Unit) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 16.dp, end = 16.dp)
      .height(56.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(modifier = Modifier.weight(1f)) {
      GeneralErrorLabel()
    }
    Button(
      onClick = onClick,
      shape = RoundedCornerShape(16.dp),
      modifier = Modifier.width(140.dp)
    ) {
      Text(
        text = "RETRY",
        maxLines = 1,
        style = AppTheme.typography.button_M,
        color = Color.White
      )
    }
  }
}

@Composable
fun GeneralErrorLabel() {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Image(
      imageVector = Outlined.ErrorOutline,
      colorFilter = ColorFilter.tint(AppTheme.colors.error),
      contentDescription = "Error icon",
      modifier = Modifier
        .padding(end = 8.dp)
        .size(16.dp)
    )
    Text(
      text = "Oops, an error occurred.",
      style = AppTheme.typography.medium_XS,
      color = AppTheme.colors.error
    )
  }
}

@Composable
fun OpenButton(onClick: () -> Unit) {
  Button(
    onClick = onClick,
    shape = RoundedCornerShape(16.dp),
    modifier = Modifier
      .height(56.dp)
      .fillMaxWidth()
  ) {
    Text(
      text = "OPEN",
      maxLines = 1,
      style = AppTheme.typography.button_L,
      color = Color.White
    )
  }
}

@Composable
fun DownloadingDownloadView(
  tintColor: Color,
  progress: Float,
  appSize: Long,
  onCloseClick: () -> Unit,
) {
  Column(
    modifier = Modifier.fillMaxWidth()
  ) {
    DownloadingProgressLabel(
      textColor = tintColor,
      progress = progress,
      appSize = appSize
    )
    DownloadingProgressBar(
      progressColor = tintColor,
      progress = progress,
      onCloseClick = onCloseClick
    )
  }
}

@Composable
fun DownloadingProgressBar(
  progressColor: Color,
  progress: Float,
  onCloseClick: () -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 16.dp, end = 16.dp, bottom = 15.dp)
      .height(15.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .weight(1f)
        .padding(end = 12.dp)
    ) {
      AptoideProgressBar(
        progressColor = progressColor,
        progress = progress
      )
    }
    Icon(
      imageVector = Icons.Default.Close,
      contentDescription = "Cancel download",
      modifier = Modifier
        .size(12.dp)
        .clickable(onClick = onCloseClick)
    )
  }
}

@Composable
fun DownloadingProgressLabel(
  textColor: Color,
  progress: Float,
  appSize: Long,
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 16.dp, end = 40.dp, bottom = 2.dp, top = 12.dp)
  ) {
    Text(
      text = "Downloading",
      style = AppTheme.typography.medium_XS,
      color = textColor,
      modifier = Modifier.align(Alignment.TopStart)
    )
    Text(
      text = "${progress.toInt()}% of " + TextFormatter.formatBytes(appSize),
      style = AppTheme.typography.medium_XS,
      color = textColor,
      modifier = Modifier.align(Alignment.TopEnd)
    )
  }
}

@Composable
fun IndeterminateDownloadView(
  label: String,
  labelColor: Color,
  progressColor: Color,
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 19.dp)
  ) {
    Text(
      text = label,
      style = AppTheme.typography.medium_XS,
      color = labelColor,
      modifier = Modifier.padding(bottom = 6.dp)
    )
    AptoideIndeterminateProgressBar(progressColor = progressColor)
  }
}

@Composable
fun AptoideIndeterminateProgressBar(progressColor: Color) {
  LinearProgressIndicator(
    modifier = Modifier
      .fillMaxWidth()
      .height(2.dp),
    backgroundColor = AppTheme.colors.downloadProgressBarBackgroundColor,
    color = progressColor
  )
}

@Composable
fun AptoideProgressBar(
  progressColor: Color,
  progress: Float,
) {
  LinearProgressIndicator(
    modifier = Modifier
      .fillMaxWidth()
      .height(8.dp)
      .clip(
        RoundedCornerShape(8.dp)
      ),
    backgroundColor = AppTheme.colors.downloadProgressBarBackgroundColor,
    color = progressColor,
    progress = progress / 100
  )
}
