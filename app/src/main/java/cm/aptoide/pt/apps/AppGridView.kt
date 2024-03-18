package cm.aptoide.pt.apps

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.aptoide_ui.AptoideAsyncImage
import cm.aptoide.pt.aptoide_ui.textformatter.TextFormatter
import cm.aptoide.pt.aptoide_ui.theme.AppTheme
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp

@Preview
@Composable
internal fun AppGridView(
  @PreviewParameter(AppProvider::class) app: App,
  onAppClick: (String) -> Unit = {},
) {
  Column(
    modifier = Modifier
      .width(80.dp)
      .height(132.dp)
      .wrapContentSize(Alignment.Center)
      .clickable { onAppClick(app.packageName) }
  ) {
    Box(
      contentAlignment = Alignment.TopEnd,
      modifier = Modifier.padding(bottom = 4.dp)
    ) {
      AptoideAsyncImage(
        data = app.icon,
        contentDescription = "App Icon",
        placeholder = ColorPainter(AppTheme.colors.placeholderColor),
        modifier = Modifier
          .size(80.dp)
          .clip(RoundedCornerShape(16.dp)),
      )
      if (app.isAppCoins) {
        Image(
          imageVector = AppTheme.icons.AppCoinsLogo,
          contentDescription = "AppCoins Icon",
          modifier = Modifier.size(24.dp),
        )
      }
    }
    Text(
      text = app.name,
      maxLines = 2,
      modifier = Modifier.height(32.dp),
      style = AppTheme.typography.medium_XS
    )
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .height(16.dp)
        .fillMaxWidth()
    ) {
      Image(
        imageVector = Icons.Filled.Star,
        colorFilter = ColorFilter.tint(AppTheme.colors.iconColor),
        contentDescription = "App Stats rating",
        modifier = Modifier
          .padding(end = 4.dp)
          .width(12.dp)
          .height(12.dp)
      )
      Text(
        text = if (app.pRating.avgRating == 0.0) "--"
        else TextFormatter.formatDecimal(app.pRating.avgRating),
        style = AppTheme.typography.medium_XS,
        textAlign = TextAlign.Center
      )
    }
  }
}

class AppProvider : PreviewParameterProvider<App> {
  override val values = listOf(randomApp).asSequence()
}
