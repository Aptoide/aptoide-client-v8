package cm.aptoide.pt.app_games.installer.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.app_games.R.string
import cm.aptoide.pt.app_games.theme.AppTheme
import cm.aptoide.pt.feature_apps.data.App

@Composable
fun ProgressText(
  app: App,
) {
  // TODO Add bool showVersionName and every ui state
  val text = app.versionName
  Text(
    text = text,
    style = AppTheme.typography.gameTitleTextCondensedSmall,
    color = AppTheme.colors.standardSecondaryTextColor,
    overflow = TextOverflow.Ellipsis,
    maxLines = 1
  )
}

@Composable
fun GenericErrorLabel(modifier: Modifier = Modifier) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Image(
      contentScale = ContentScale.Inside,
      imageVector = AppTheme.icons.ErrorOutlined,
      contentDescription = null,
      modifier = Modifier
        .padding(end = 4.dp)
        .size(16.dp)
    )
    Text(
      text = stringResource(string.install_error_short_message),
      style = AppTheme.typography.bodyCopyXS,
      color = AppTheme.colors.error
    )
  }
}
