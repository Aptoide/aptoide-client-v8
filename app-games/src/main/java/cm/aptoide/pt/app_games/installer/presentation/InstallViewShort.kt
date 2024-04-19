package cm.aptoide.pt.app_games.installer.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.app_games.R.string
import cm.aptoide.pt.app_games.theme.AppGamesButton
import cm.aptoide.pt.app_games.theme.AptoideTheme
import cm.aptoide.pt.app_games.theme.ButtonStyle.Default
import cm.aptoide.pt.extensions.PreviewAll

@PreviewAll
@Composable
fun InstallViewShortPreview() {
  // A contrast divider to highlight items boundaries
  val divider = @Composable {
    Divider(
      color = Color.Green.copy(alpha = 0.2f),
      thickness = 8.dp
    )
  }
  //TODO Implement with all ui states
  AptoideTheme(darkTheme = isSystemInDarkTheme()) {
    Column(verticalArrangement = Arrangement.Center) {
      InstallViewShortContent()
      divider()
    }
  }
}

@Composable
fun InstallViewShort() {
  //TODO Make it actually install
  InstallViewShortContent()
}

@Composable
private fun InstallViewShortContent() {
  AppGamesButton(
    title = stringResource(string.button_install_title),
    onClick = {},
    style = Default(fillWidth = false),
  )
}
