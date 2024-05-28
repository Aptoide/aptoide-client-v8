package com.aptoide.android.aptoidegames.feature_oos

import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.aptoide_ui.textformatter.TextFormatter
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Installed
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Outdated
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Uninstalling
import cm.aptoide.pt.download_view.presentation.DownloadUiState.Waiting
import cm.aptoide.pt.extensions.getAppIconDrawable
import cm.aptoide.pt.extensions.getAppName
import cm.aptoide.pt.extensions.getAppSize
import cm.aptoide.pt.feature_apps.data.emptyApp
import com.aptoide.android.aptoidegames.AptoideAsyncImage
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.design_system.AppGamesOutlinedButton
import com.aptoide.android.aptoidegames.design_system.ButtonStyle.Default
import com.aptoide.android.aptoidegames.installer.presentation.installViewStates
import com.aptoide.android.aptoidegames.theme.AppTheme

@Composable
fun OutOfSpaceAppItem(packageName: String) {
  val context = LocalContext.current
  val app = emptyApp.copy(
    packageName = packageName,
    appSize = context.getAppSize(packageName),
    name = context.getAppName(packageName),
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
        .size(64.dp),
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
        style = AppTheme.typography.descriptionGames,
        modifier = Modifier.padding(bottom = 8.dp),
        overflow = TextOverflow.Ellipsis,
        maxLines = 2,
        color = AppTheme.colors.outOfSpaceDialogAppNameColor
      )
      Text(
        text = TextFormatter.formatBytes(appSize),
        style = AppTheme.typography.inputs_S,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        color = AppTheme.colors.outOfSpaceDialogAppSizeColor
      )
    }

    AppGamesOutlinedButton(
      title = stringResource(id = R.string.uninstall_button),
      onClick = action,
      enabled = true,
      style = Default(fillWidth = false),
      modifier = Modifier
        .wrapContentWidth()
        .height(32.dp)
    )

  }
}
