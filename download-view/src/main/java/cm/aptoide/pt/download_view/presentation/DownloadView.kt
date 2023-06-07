package cm.aptoide.pt.download_view.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.aptoide_ui.theme.AppTheme
import cm.aptoide.pt.aptoide_ui.theme.AptoideTheme
import cm.aptoide.pt.download_view.platform.checkIfInstallationsAllowed
import cm.aptoide.pt.download_view.platform.requestAllowInstallations
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.emptyApp
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import java.util.Locale

@Preview
@Composable
fun DownloadPreview() {
  val apps = listOf(
    emptyApp,
    emptyApp.copy(isAppCoins = true)
  )
  val states = listOf(
    DownloadUiState.Install,
    DownloadUiState.Outdated,
    DownloadUiState.Processing,
    DownloadUiState.Downloading(33),
    DownloadUiState.ReadyToInstall,
    DownloadUiState.Installing(66),
    DownloadUiState.Installed,
    DownloadUiState.Error
  )
  AptoideTheme {
    LazyColumn {
      apps.forEach { app ->
        states.forEach { uiState ->
          item {
            MainDownloadView(app) {
              DownloadState(
                downloadUiState = uiState,
                tintColor = if (app.isAppCoins) {
                  AppTheme.colors.appCoinsColor
                } else {
                  AppTheme.colors.primary
                },
                appSize = app.appSize,
                onInstallClick = {},
                onCancelClick = {},
                onOpenClick = {}
              )
            }
          }
          item {
            Spacer(modifier = Modifier.height(16.dp))
          }
        }
        item {
          Spacer(modifier = Modifier.height(16.dp))
        }
      }
    }
  }
}

@Composable
fun DownloadViewScreen(app: App = emptyApp) {

  val downloadViewViewModel = perAppViewModel(app = app)
  val uiState by downloadViewViewModel.uiState.collectAsState()
  val openPermissionsDialog = remember { mutableStateOf(false) }
  val localContext = LocalContext.current

  AptoideTheme {
    MainDownloadView(app) {
      DownloadState(
        downloadUiState = uiState,
        tintColor = if (app.isAppCoins) {
          AppTheme.colors.appCoinsColor
        } else {
          AppTheme.colors.primary
        },
        appSize = app.appSize,
        onInstallClick = {
          if (localContext.checkIfInstallationsAllowed()) {
            downloadViewViewModel.downloadApp(app)
          } else {
            openPermissionsDialog.value = true
          }
        },
        onCancelClick = downloadViewViewModel::cancelDownload,
        onOpenClick = downloadViewViewModel::openApp
      )
    }
    if (openPermissionsDialog.value) {
      InstallSourcesDialog(
        onSettings = {
          openPermissionsDialog.value = false
          localContext.requestAllowInstallations()
        },
        onCancel = {
          openPermissionsDialog.value = false
        }
      )
    }
  }
}

@Composable
fun MainDownloadView(
  app: App,
  installButton: @Composable () -> Unit,
) {
  if (app.isAppCoins) {
    AppCoinsDownloadView {
      installButton()
    }
  } else {
    NoAppCoinsDownloadView {
      installButton()
    }
  }
  @Suppress("ConstantConditionIf")
  if (false) {
    ESkillsDownloadView {
      installButton()
    }
  }
}

@Composable
fun ESkillsDownloadView(installButton: @Composable () -> Unit) {
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
      installButton()
      ESkillsBanner()
    }
  }
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
        data = cm.aptoide.pt.aptoide_ui.R.drawable.ic_eskills_logo,
        builder = {
          placeholder(cm.aptoide.pt.aptoide_ui.R.drawable.ic_placeholder)
          transformations(RoundedCornersTransformation())
        }
      ),
      contentDescription = "Eskills icon",
      modifier = Modifier
        .padding(end = 8.dp)
        .size(24.dp),
      contentScale = ContentScale.Inside
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
fun AppCoinsDownloadView(installButton: @Composable () -> Unit) {
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
      installButton()
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
        data = cm.aptoide.pt.aptoide_ui.R.drawable.ic_appcoins_logo,
        builder = {
          placeholder(cm.aptoide.pt.aptoide_ui.R.drawable.ic_placeholder)
          transformations(RoundedCornersTransformation())
        }
      ),
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
fun NoAppCoinsDownloadView(installButton: @Composable () -> Unit) {
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
  downloadUiState: DownloadUiState,
  tintColor: Color,
  appSize: Long,
  onInstallClick: () -> Unit,
  onCancelClick: () -> Unit,
  onOpenClick: () -> Unit,
) {
  when (downloadUiState) {
    DownloadUiState.Install,
    DownloadUiState.Outdated,
    -> InstallButton(onInstallClick)

    DownloadUiState.Processing -> IndeterminateDownloadView(
      label = "Downloading",
      labelColor = tintColor,
      progressColor = tintColor
    )

    is DownloadUiState.Downloading -> DownloadingDownloadView(
      tintColor = tintColor,
      progress = downloadUiState.downloadProgress.toFloat(),
      appSize = appSize,
      onCloseClick = onCancelClick
    )

    is DownloadUiState.Installing -> IndeterminateDownloadView(
      label = "Installing",
      labelColor = tintColor,
      progressColor = tintColor
    )

    DownloadUiState.Installed -> OpenButton(onOpenClick)
    DownloadUiState.Error -> ErrorDownloadView()
    DownloadUiState.ReadyToInstall -> ReadyToInstallView()
  }
  if (
    downloadUiState !in listOf(
      DownloadUiState.Install,
      DownloadUiState.Installed,
      DownloadUiState.Outdated
    )
  ) {
    Divider(
      color = AppTheme.colors.dividerColor,
      thickness = 1.dp
    )
  }
}

@Composable
fun ReadyToInstallView() {
  Button(
    onClick = { TODO("Handle install app only needed for the backgorund install flow") },
    shape = RoundedCornerShape(16.dp),
    modifier = Modifier
      .height(56.dp)
      .fillMaxWidth()
  ) {
    Text(
      text = "READY TO INSTALL",
      maxLines = 1,
      style = AppTheme.typography.button_L,
      color = Color.White
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
      contentScale = ContentScale.Inside,
      painter = rememberImagePainter(
        data = cm.aptoide.pt.aptoide_ui.R.drawable.ic_icon_error,
        builder = {
          placeholder(cm.aptoide.pt.aptoide_ui.R.drawable.ic_placeholder)
          transformations(RoundedCornersTransformation())
        }
      ),
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
      text = "${progress.toInt()}% of " + formatBytes(appSize),
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
