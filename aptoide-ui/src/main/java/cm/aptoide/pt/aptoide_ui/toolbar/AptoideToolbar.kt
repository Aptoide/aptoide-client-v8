package cm.aptoide.pt.aptoide_ui.toolbar

import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import cm.aptoide.pt.aptoide_ui.BuildConfig
import cm.aptoide.pt.aptoide_ui.theme.AppTheme
import cm.aptoide.pt.aptoide_ui.theme.AptoideTheme

@Preview
@Composable
fun AptoideActionBar() {
  AptoideTheme {
    TopAppBar(
      backgroundColor = AppTheme.colors.background, elevation = Dp(0f)
    ) {
      Icon(imageVector = Icons.Outlined.SportsEsports, contentDescription = null)
      Text(BuildConfig.MARKET_NAME)
    }
  }
}