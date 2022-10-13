package cm.aptoide.pt.download_view.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.aptoide_ui.theme.AppTheme
import cm.aptoide.pt.aptoide_ui.theme.AptoideTheme
import cm.aptoide.pt.feature_apps.data.DetailedApp
import cm.aptoide.pt.feature_apps.data.emptyDetailedApp
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import java.util.*

@Preview
@Composable
fun DownloadViewScreen(
  downloadViewViewModel: DownloadViewViewModel = hiltViewModel(),
  app: DetailedApp = emptyDetailedApp
) {

  val uiState by downloadViewViewModel.uiState.collectAsState()

  downloadViewViewModel.loadDownloadState(app)
  AptoideTheme {
    MainDownloadView(uiState, onDownloadApp = {
      downloadViewViewModel.downloadApp(it)
    }, onCancelDownload = { downloadViewViewModel.cancelDownload(it) }, openApp = {
      downloadViewViewModel.openApp(it)
    })
  }
}

@Composable
fun MainDownloadView(
  uiState: DownloadViewUiState,
  onDownloadApp: (DetailedApp) -> Unit,
  onCancelDownload: (DetailedApp) -> Unit,
  openApp: (DetailedApp) -> Unit
) {
  when (uiState.downloadViewType) {
    DownloadViewType.NO_APPCOINS -> {
      NoAppCoinsDownloadView(
        uiState.app,
        uiState.downloadViewState,
        uiState.downloadProgress,
        onDownloadApp,
        onCancelDownload, openApp
      )
    }
    DownloadViewType.APPCOINS -> {
      AppCoinsDownloadView(
        uiState.app,
        uiState.downloadViewState,
        uiState.downloadProgress,
        onDownloadApp, onCancelDownload, openApp
      )
    }
    DownloadViewType.ESKILLS -> {
      ESkillsDownloadView(
        uiState.app,
        uiState.downloadViewState,
        uiState.downloadProgress,
        onDownloadApp, onCancelDownload, openApp
      )
    }
  }
}

@Composable
fun ESkillsDownloadView(
  app: DetailedApp?,
  downloadViewState: DownloadViewState,
  downloadProgress: Int,
  onDownloadApp: (DetailedApp) -> Unit,
  onCancelDownload: (DetailedApp) -> Unit, openApp: (DetailedApp) -> Unit
) {
  Card(
    modifier = Modifier
      .padding(start = 16.dp, end = 16.dp)
      .fillMaxWidth()
      .height(96.dp)
      .clip(RoundedCornerShape(16.dp)),
    elevation = 6.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .background(color = AppTheme.colors.downloadBannerBackgroundColor)
    ) {
      DownloadState(
        downloadViewState,
        app,
        downloadProgress,
        onDownloadApp,
        onCancelDownload,
        openApp
      )
      if (shouldShowInstallDivider(downloadViewState)) {
        Divider(color = AppTheme.colors.dividerColor, thickness = 1.dp)
      }
      ESkillsBanner()
    }
  }
}

private fun shouldShowInstallDivider(downloadViewState: DownloadViewState): Boolean {
  return !(downloadViewState.equals(DownloadViewState.INSTALL) || downloadViewState == DownloadViewState.INSTALLED)
}

@Composable
fun ESkillsBanner() {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .padding(bottom = 8.dp, top = 8.dp, start = 16.dp)
      .wrapContentHeight()
  ) {
    Image(
      painter = rememberImagePainter(
        cm.aptoide.pt.download_view.R.drawable.ic_eskills_logo,
        builder = {
          placeholder(cm.aptoide.pt.download_view.R.drawable.ic_placeholder)
          transformations(RoundedCornersTransformation())
        }),
      contentDescription = "Eskills icon",
      modifier = Modifier
        .padding(end = 8.dp)
        .size(24.dp), contentScale = ContentScale.Inside
    )
    Text(
      text = "Earn Money", style = AppTheme.typography.medium_XS,
      color = AppTheme.colors.onBackground,
      modifier = Modifier.padding(end = 12.dp)
    )
    Text(
      text = "Beat other players with e-Skills",
      style = AppTheme.typography.regular_XXS,
      color = AppTheme.colors.greyText
    )
  }
}

@Composable
fun AppCoinsDownloadView(
  app: DetailedApp?,
  downloadViewState: DownloadViewState,
  downloadProgress: Int,
  onDownloadApp: (DetailedApp) -> Unit,
  onCancelDownload: (DetailedApp) -> Unit,
  openApp: (DetailedApp) -> Unit
) {
  Card(
    modifier = Modifier
      .padding(start = 16.dp, end = 16.dp)
      .fillMaxWidth()
      .height(96.dp)
      .clip(RoundedCornerShape(16.dp)),
    elevation = 6.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .background(color = AppTheme.colors.downloadBannerBackgroundColor)
    ) {
      DownloadState(
        downloadViewState,
        app,
        downloadProgress,
        onDownloadApp,
        onCancelDownload,
        openApp
      )
      if (shouldShowInstallDivider(downloadViewState)) {
        Divider(color = AppTheme.colors.dividerColor, thickness = 1.dp)
      }
      AppCoinsBanner()
    }

  }
}

@Composable
fun AppCoinsBanner() {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
      .wrapContentHeight()
  ) {
    Image(
      contentScale = ContentScale.Inside,
      painter = rememberImagePainter(
        cm.aptoide.pt.download_view.R.drawable.ic_appcoins_logo,
        builder = {
          placeholder(cm.aptoide.pt.download_view.R.drawable.ic_placeholder)
          transformations(RoundedCornersTransformation())
        }),
      contentDescription = "AppCoins icon",
      modifier = Modifier
        .padding(end = 8.dp)
        .size(24.dp)
    )
    Text(
      text = "Up to 25% bonus",
      style = AppTheme.typography.medium_XS,
      color = AppTheme.colors.appCoinsColor,
      modifier = Modifier.padding(end = 12.dp)
    )
    Text(
      text = "In-app purchases with AppCoins",
      style = AppTheme.typography.regular_XXS,
      color = AppTheme.colors.greyText
    )
  }
}

@Composable
fun NoAppCoinsDownloadView(
  app: DetailedApp?,
  downloadViewState: DownloadViewState,
  downloadProgress: Int,
  onDownloadApp: (DetailedApp) -> Unit,
  onCancelDownload: (DetailedApp) -> Unit,
  openApp: (DetailedApp) -> Unit
) {
  Card(
    modifier = Modifier
      .padding(start = 16.dp, end = 16.dp)
      .fillMaxWidth()
      .height(56.dp)
      .clip(RoundedCornerShape(16.dp)), elevation = 6.dp
  ) {
    DownloadState(
      downloadViewState,
      app,
      downloadProgress,
      onDownloadApp,
      onCancelDownload,
      openApp
    )
  }
}

@Composable
fun DownloadState(
  downloadViewState: DownloadViewState,
  app: DetailedApp?,
  downloadProgress: Int,
  onDownloadApp: (DetailedApp) -> Unit,
  onCancelDownload: (DetailedApp) -> Unit, openApp: (DetailedApp) -> Unit
) {
  when (downloadViewState) {
    DownloadViewState.INSTALL -> {
      app?.let { InstallButton(onDownloadApp, it) }
    }
    DownloadViewState.PROCESSING -> {
      app?.let { ProcessingDownloadView(it.isAppCoins) }
    }
    DownloadViewState.DOWNLOADING -> {
      app?.let {
        DownloadingDownloadView(
          it.isAppCoins,
          downloadProgress.toFloat(),
          app.appSize,
          onCancelDownload, app
        )
      }
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
      ReadyToInstallView(openApp)
    }
  }
}

@Composable
fun ReadyToInstallView(openApp: (DetailedApp) -> Unit) {
  Button(
    onClick = { TODO("Handle install app only needed for the backgorund install flow") },
    shape = RoundedCornerShape(16.dp),
    modifier = Modifier
      .height(56.dp)
      .fillMaxWidth()
  ) {
    Text(
      "READY TO INSTALL", maxLines = 1, style = AppTheme.typography.button_L,
      color = Color.White
    )
  }
}


@Composable
fun InstallButton(onDownloadApp: (DetailedApp) -> Unit, app: DetailedApp) {
  Button(
    onClick = { onDownloadApp(app) },
    shape = RoundedCornerShape(16.dp),
    modifier = Modifier
      .height(56.dp)
      .fillMaxWidth()
  ) {
    Text(
      "INSTALL", maxLines = 1, style = AppTheme.typography.button_L,
      color = Color.White
    )
  }
}

@Composable
fun ErrorDownloadView() {
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
      onClick = { },
      shape = RoundedCornerShape(16.dp),
      modifier = Modifier
        .width(140.dp)
    ) {
      Text(
        "RETRY", maxLines = 1, style = AppTheme.typography.button_M,
        color = Color.White
      )
    }
  }
}

@Composable
fun GeneralErrorLabel() {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Image(
      contentScale = ContentScale.Inside,
      painter = rememberImagePainter(
        cm.aptoide.pt.download_view.R.drawable.ic_icon_error,
        builder = {
          placeholder(cm.aptoide.pt.download_view.R.drawable.ic_placeholder)
          transformations(RoundedCornersTransformation())
        }),
      contentDescription = "Error icon",
      modifier = Modifier
        .padding(end = 8.dp)
        .size(16.dp)
    )
    Text(
      text = "Oops, an error occurred.", style = AppTheme.typography.medium_XS,
      color = AppTheme.colors.error
    )
  }
}

@Composable
fun OpenButton() {
  Button(
    onClick = { TODO("Handle open app") },
    shape = RoundedCornerShape(16.dp),
    modifier = Modifier
      .height(56.dp)
      .fillMaxWidth()
  ) {
    Text(
      "OPEN", maxLines = 1, style = AppTheme.typography.button_L,
      color = Color.White
    )
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
fun DownloadingDownloadView(
  isAppCoins: Boolean,
  progress: Float,
  appSize: Long,
  onCancelDownload: (DetailedApp) -> Unit,
  app: DetailedApp
) {
  if (isAppCoins) {
    AppCoinsDownloadingDownloadView(progress, appSize, onCancelDownload, app)
  } else {
    NoAppCoinsDownloadingDownloadView(progress, appSize, onCancelDownload, app)
  }
}

@Composable
fun NoAppCoinsDownloadingDownloadView(
  progress: Float,
  appSize: Long,
  onCancelDownload: (DetailedApp) -> Unit,
  app: DetailedApp
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
  ) {
    DownloadingProgressLabel(AppTheme.colors.primary, progress, appSize)
    DownloadingProgressBar(AppTheme.colors.primary, progress, onCancelDownload, app)
  }
}

@Composable
fun AppCoinsDownloadingDownloadView(
  progress: Float,
  appSize: Long,
  onCancelDownload: (DetailedApp) -> Unit,
  app: DetailedApp
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
  ) {
    DownloadingProgressLabel(AppTheme.colors.appCoinsColor, progress, appSize)
    DownloadingProgressBar(AppTheme.colors.appCoinsColor, progress, onCancelDownload, app)
  }
}

@Composable
fun DownloadingProgressBar(
  progressColor: Color,
  progress: Float,
  onCancelDownload: (DetailedApp) -> Unit,
  app: DetailedApp
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
      AptoideProgressBar(progressColor = progressColor, progress = progress)
    }
    Icon(
      imageVector = Icons.Default.Close,
      contentDescription = "Cancel download",
      modifier = Modifier
        .size(12.dp)
        .clickable(onClick = {
          onCancelDownload(app)
        })
    )

  }
}

@Composable
fun DownloadingProgressLabel(
  color: Color,
  progress: Float,
  appSize: Long
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 16.dp, end = 40.dp, bottom = 2.dp, top = 12.dp)
  ) {
    Text(
      text = "Downloading", style = AppTheme.typography.medium_XS,
      color = color, modifier = Modifier.align(Alignment.TopStart)
    )
    Text(
      text = "${progress.toInt()}% of " + formatBytes(appSize),
      style = AppTheme.typography.medium_XS,
      color = color,
      modifier = Modifier.align(Alignment.TopEnd)
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
      style = AppTheme.typography.medium_XS,
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
    backgroundColor = AppTheme.colors.downloadProgressBarBackgroundColor,
    color = progressColor
  )
}

@Composable
fun AptoideProgressBar(progressColor: Color, progress: Float) {
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

fun formatBytes(bytes: Long): String {
  val unit = 1024
  if (bytes < unit) {
    return "$bytes B"
  }
  val exp = (Math.log(bytes.toDouble()) / Math.log(unit.toDouble())).toInt()
  val pre = "KMGTPE"[exp - 1].toString() + ""
  val string = String.format(
    Locale.ENGLISH,
    "%.1f %sB",
    bytes / Math.pow(unit.toDouble(), exp.toDouble()),
    pre
  )
  return string
}