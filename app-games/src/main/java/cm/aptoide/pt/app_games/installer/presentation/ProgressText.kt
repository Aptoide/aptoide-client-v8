package cm.aptoide.pt.app_games.installer.presentation

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
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

// TODO further implement show error label
