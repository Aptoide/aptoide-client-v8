package cm.aptoide.pt.app_games.feature_oos

import android.graphics.drawable.Drawable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.app_games.AptoideAsyncImage
import cm.aptoide.pt.app_games.R.string
import cm.aptoide.pt.app_games.installer.presentation.installViewStates
import cm.aptoide.pt.app_games.theme.AppTheme
import cm.aptoide.pt.aptoide_ui.textformatter.TextFormatter
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Installed
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Outdated
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Uninstalling
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Waiting
import cm.aptoide.pt.extensions.getAppIconDrawable
import cm.aptoide.pt.extensions.getAppName
import cm.aptoide.pt.extensions.getAppSize
import cm.aptoide.pt.extensions.getAppVersionName
import cm.aptoide.pt.feature_apps.data.emptyApp

@Composable
fun OutOfSpaceAppItem(packageName: String) {
  val context = LocalContext.current
  val app = emptyApp.copy(
    packageName = packageName,
    appSize = context.getAppSize(packageName),
    name = context.getAppName(packageName),
    versionName = context.getAppVersionName(packageName),
  )
  val appIcon = context.getAppIconDrawable(packageName)
  val (state) = installViewStates(
    app = app,
  )
  if ((state is Waiting
      || state is Outdated
      || state is Installed
      || state is Uninstalling)
  ) {
    AppItem(
      icon = appIcon,
      name = app.name,
      appSize = app.appSize,
      action = when (state) {
        is Installed -> state.uninstall
        is Outdated -> state.uninstall
        else -> { -> }
      }
    )
  }
}

@Composable
fun AppItem(
  icon: Drawable?,
  name: String,
  appSize: Long,
  action: () -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .defaultMinSize(minHeight = 80.dp)
      .padding(top = 8.dp, bottom = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    AptoideAsyncImage(
      modifier = Modifier
        .padding(end = 16.dp)
        .size(64.dp)
        .clip(RoundedCornerShape(16.dp)),
      data = icon,
      contentDescription = null
    )
    Column(
      modifier = Modifier
        .padding(end = 16.dp)
        .fillMaxHeight()
        .width(136.dp)
        .weight(1f)
    ) {
      Text(
        text = name,
        style = AppTheme.typography.gameTitleTextCondensed,
        modifier = Modifier.padding(bottom = 8.dp),
        overflow = TextOverflow.Ellipsis,
        maxLines = 2,
        color = AppTheme.colors.outOfSpaceDialogAppNameColor
      )
      Text(
        text = TextFormatter.formatBytes(appSize),
        style = AppTheme.typography.gameTitleTextCondensed,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        color = AppTheme.colors.outOfSpaceDialogAppSizeColor
      )
    }

    OutlinedButton(
      onClick = action,
      shape = RoundedCornerShape(32.dp),
      modifier = Modifier
        .wrapContentWidth()
        .height(32.dp),
      contentPadding = PaddingValues(start = 14.dp, end = 14.dp),
      elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
      colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
      border = BorderStroke(
        width = 1.dp,
        color = AppTheme.colors.outOfSpaceDialogUninstallButtonColor
      )
    ) {
      Text(
        text = stringResource(string.uninstall_button),
        maxLines = 1,
        style = AppTheme.typography.buttonTextMedium,
        color = AppTheme.colors.outOfSpaceDialogUninstallButtonColor
      )
    }

  }
}
