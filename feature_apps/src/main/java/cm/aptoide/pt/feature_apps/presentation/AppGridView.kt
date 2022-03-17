package cm.aptoide.pt.feature_apps.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.feature_apps.data.App
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation

@Composable
internal fun AppGridView(app: App) {
  Box(contentAlignment = Alignment.TopEnd) {
    Image(
      painter = rememberImagePainter(app.icon,
        builder = {
          transformations(RoundedCornersTransformation(16f))
        }),
      contentDescription = "App Icon",
      modifier = Modifier.size(80.dp),

      )
    if (app.isAppCoins) {
      Image(painter = rememberImagePainter("https://s2.coinmarketcap.com/static/img/coins/64x64/2344.png"),
        contentDescription = "AppCoins Icon",
        modifier = Modifier.size(21.dp))
    }
  }
  Text(app.name, maxLines = 2, modifier = Modifier
    .height(42.dp))
}