package cm.aptoide.pt.download_view.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.feature_apps.R
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.theme.AppTheme
import cm.aptoide.pt.theme.AptoideTheme
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation

@Preview
@Composable
fun DownloadViewScreen(downloadViewViewModel: DownloadViewViewModel, app: App) {

  val uiState by downloadViewViewModel.uiState.collectAsState()

  downloadViewViewModel.loadDownloadState(app)
  AptoideTheme {
    MainDownloadView(uiState, onDownloadApp = {
      downloadViewViewModel.downloadApp(it)
    })
  }
}

@Composable
fun MainDownloadView(uiState: DownloadViewUiState, onDownloadApp: (App) -> Unit) {
  when (uiState.downloadViewType) {
    DownloadViewType.NO_APPCOINS -> {
      NoAppCoinsDownloadView(
        uiState.app,
        uiState.downloadViewState,
        uiState.downloadProgress,
        onDownloadApp
      )
    }
    DownloadViewType.APPCOINS -> {
      AppCoinsDownloadView(
        uiState.app,
        uiState.downloadViewState,
        uiState.downloadProgress,
        onDownloadApp
      )
    }
    DownloadViewType.ESKILLS -> {
      ESkillsDownloadView(
        uiState.app,
        uiState.downloadViewState,
        uiState.downloadProgress,
        onDownloadApp
      )
    }
  }
}

@Composable
fun ESkillsDownloadView(
  app: App?,
  downloadViewState: DownloadViewState,
  downloadProgress: Int,
  onDownloadApp: (App) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .height(96.dp)
  ) {
    DownloadState(downloadViewState, app, downloadProgress, onDownloadApp)
    ESkillsBanner()
  }
}

@Composable
fun ESkillsBanner() {
  Row {
    // TODO: add eskills icon
    Image(
      painter = rememberImagePainter(R.drawable.ic_placeholder,
        builder = {
          placeholder(R.drawable.ic_placeholder)
          transformations(RoundedCornersTransformation())
        }),
      contentDescription = "Eskills icon",
      modifier = Modifier
        .width(24.dp)
        .height(24.dp)
        .padding(bottom = 8.dp, top = 8.dp, start = 16.dp, end = 8.dp)
    )
    Text(text = "Earn Money")
    Text(text = "Beat other players with e-Skills")
  }
}

@Composable
fun AppCoinsDownloadView(
  app: App?,
  downloadViewState: DownloadViewState,
  downloadProgress: Int,
  onDownloadApp: (App) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .height(96.dp)
  ) {
    DownloadState(downloadViewState, app, downloadProgress, onDownloadApp)
    AppCoinsBanner()
  }
}

@Composable
fun AppCoinsBanner() {
  Row {
    // TODO: add appcoins icon
    Image(
      painter = rememberImagePainter(R.drawable.ic_placeholder,
        builder = {
          placeholder(R.drawable.ic_placeholder)
          transformations(RoundedCornersTransformation())
        }),
      contentDescription = "AppCoins icon",
      modifier = Modifier
        .width(24.dp)
        .height(24.dp)
        .padding(bottom = 8.dp, top = 8.dp, start = 16.dp, end = 8.dp)
    )
    Text(text = "Up to 25% bonus")
    Text(text = "In-app purchases with AppCoins")
  }
}

@Composable
fun NoAppCoinsDownloadView(
  app: App?,
  downloadViewState: DownloadViewState,
  downloadProgress: Int,
  onDownloadApp: (App) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .height(56.dp)
  ) {
    DownloadState(downloadViewState, app, downloadProgress, onDownloadApp)
  }
}

@Composable
fun DownloadState(
  downloadViewState: DownloadViewState,
  app: App?,
  downloadProgress: Int,
  onDownloadApp: (App) -> Unit
) {
  when (downloadViewState) {
    DownloadViewState.INSTALL -> {
      app?.let { InstallButton(onDownloadApp, it) }
    }
    DownloadViewState.PROCESSING -> {
      app?.let { ProcessingDownloadView(it.isAppCoins) }
    }
    DownloadViewState.DOWNLOADING -> {
      app?.let { DownloadingDownloadView(it.isAppCoins, downloadProgress.toFloat(), app.appSize) }
    }
    DownloadViewState.INSTALLING -> {
      app?.let { InstallingDownloadView(it.isAppCoins) }
    }
    DownloadViewState.INSTALLED -> {
      OpenButton()
    }
    DownloadViewState.ERROR -> {
      ErrorDownloadView()
    }
    DownloadViewState.READY_TO_INSTALL -> {
      ReadyToInstallView()
    }
  }
}

@Composable
fun ReadyToInstallView() {
  Button(
    onClick = { TODO("Handle install app") },
    shape = CircleShape,
    modifier = Modifier
      .height(56.dp)
      .fillMaxWidth()
  ) {
    Text("READY TO INSTALL", maxLines = 1)
  }
}


@Composable
fun InstallButton(onDownloadApp: (App) -> Unit, app: App) {
  Button(
    onClick = { onDownloadApp(app) },
    shape = CircleShape,
    modifier = Modifier
      .height(56.dp)
      .fillMaxWidth()
  ) {
    Text("INSTALL", maxLines = 1)
  }
}

@Composable
fun ErrorDownloadView() {
  Row(modifier = Modifier.fillMaxWidth()) {
    Box(modifier = Modifier.weight(1f)) {
      GeneralErrorLabel()
    }
    Button(
      onClick = { TODO("Handle open app") },
      shape = CircleShape,
      modifier = Modifier
        .height(56.dp)
        .width(140.dp)
    ) {
      Text("RETRY", maxLines = 1)
    }
  }
}

@Composable
fun GeneralErrorLabel() {
  Text(text = "Oops, an error occurred")
}

@Composable
fun OpenButton() {
  Button(
    onClick = { TODO("Handle open app") },
    shape = CircleShape,
    modifier = Modifier
      .height(56.dp)
      .fillMaxWidth()
  ) {
    Text("OPEN", maxLines = 1)
  }
}

@Composable
fun ProcessingDownloadView(isAppCoins: Boolean) {
  if (isAppCoins) {
    IndeterminateDownloadView(
      label = "Downloading",
      AppTheme.colors.appCoinsColor,
      AppTheme.colors.appCoinsColor
    )
  } else {
    IndeterminateDownloadView(
      label = "Downloading",
      AppTheme.colors.primary,
      AppTheme.colors.primary
    )
  }
}

@Composable
fun InstallingDownloadView(isAppCoins: Boolean) {
  if (isAppCoins) {
    IndeterminateDownloadView(
      label = "Installing",
      AppTheme.colors.appCoinsColor,
      AppTheme.colors.appCoinsColor
    )
  } else {
    IndeterminateDownloadView(
      label = "Installing",
      AppTheme.colors.primary,
      AppTheme.colors.primary
    )
  }
}

@Composable
fun DownloadingDownloadView(isAppCoins: Boolean, progress: Float, appSize: Long) {
  if (isAppCoins) {
    AppCoinsDownloadingDownloadView(progress, appSize)
  } else {
    NoAppCoinsDownloadingDownloadView(progress, appSize)
  }
}

@Composable
fun NoAppCoinsDownloadingDownloadView(progress: Float, appSize: Long) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
  ) {
    DownloadingProgressLabel(AppTheme.colors.primary, progress, appSize)
    DownloadingProgressBar(AppTheme.colors.primary, progress)
  }
}

@Composable
fun AppCoinsDownloadingDownloadView(progress: Float, appSize: Long) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
  ) {
    DownloadingProgressLabel(AppTheme.colors.appCoinsColor, progress, appSize)
    DownloadingProgressBar(AppTheme.colors.appCoinsColor, progress)
  }
}

@Composable
fun DownloadingProgressBar(progressColor: Color, progress: Float) {
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
      AptoideProgressBar(progressColor = progressColor, progress = progress)
    }
    Icon(
      imageVector = Icons.Default.Close,
      contentDescription = "Cancel download",
      modifier = Modifier
        .size(12.dp)
        .clickable(onClick = {
          // TODO:
        })
    )

  }
}

@Composable
fun DownloadingProgressLabel(color: Color, progress: Float, appSize: Long) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 16.dp, end = 40.dp, bottom = 2.dp, top = 12.dp)
  ) {
    Text(
      text = "Downloading", fontSize = MaterialTheme.typography.caption.fontSize,
      color = color, modifier = Modifier.align(Alignment.TopStart)
    )
    Text(
      text = "$progress of $appSize", fontSize = MaterialTheme.typography.caption.fontSize,
      color = color, modifier = Modifier.align(Alignment.TopEnd)
    )
  }
}


@Composable
fun IndeterminateDownloadView(label: String, labelColor: Color, progressColor: Color) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 19.dp)
  ) {
    Text(
      text = label,
      fontSize = MaterialTheme.typography.caption.fontSize,
      color = labelColor, modifier = Modifier.padding(bottom = 6.dp)
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
    backgroundColor = Color(0xFF6F6F6F),
    color = progressColor
  )
}

@Composable
fun AptoideProgressBar(progressColor: Color, progress: Float) {
  LinearProgressIndicator(
    modifier = Modifier
      .fillMaxWidth()
      .height(8.dp),
    backgroundColor = Color(0xFF6F6F6F),
    color = progressColor,
    progress = progress / 100
  )
}