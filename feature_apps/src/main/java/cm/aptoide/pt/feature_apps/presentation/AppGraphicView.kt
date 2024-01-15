package cm.aptoide.pt.feature_apps.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.aptoide_ui.AptoideAsyncImage
import cm.aptoide.pt.aptoide_ui.buttons.GradientButton
import cm.aptoide.pt.aptoide_ui.textformatter.TextFormatter
import cm.aptoide.pt.aptoide_ui.theme.AppTheme
import cm.aptoide.pt.aptoide_ui.theme.appCoinsButtonGradient
import cm.aptoide.pt.aptoide_ui.theme.orangeGradient
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import cm.aptoide.pt.theme.shapes

@Preview(name = "Feature Graphic Item")
@Composable
fun AppGraphicView(
  @PreviewParameter(AppGraphicProvider::class) app: App,
  onAppClick: (String) -> Unit = {},
  bonusBanner: (@Composable BoxScope.() -> Unit)? = null,
) {
  Column(
    modifier = Modifier
      .width(280.dp)
      .height(184.dp)
      .clickable { onAppClick(app.packageName) },
    verticalArrangement = Arrangement.Center,
  ) {
    Box {
      AptoideAsyncImage(
        data = app.featureGraphic,
        contentDescription = "App Graphic",
        placeholder = ColorPainter(AppTheme.colors.placeholderColor),
        modifier = Modifier
          .width(280.dp)
          .height(136.dp)
          .clip(RoundedCornerShape(16.dp))
      )
      bonusBanner?.invoke(this)
    }
    Spacer(modifier = Modifier.height(8.dp))
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      AptoideAsyncImage(
        data = app.icon,
        contentDescription = "App Icon",
        placeholder = ColorPainter(AppTheme.colors.placeholderColor),
        modifier = Modifier
          .size(40.dp)
          .clip(shapes.large)
      )
      Column(
        modifier = Modifier
          .fillMaxHeight()
          .weight(1f),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Bottom),
      ) {
        Text(
          text = app.name,
          modifier = Modifier.fillMaxWidth(),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          style = AppTheme.typography.medium_XS
        )
        Row(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Image(
            imageVector = Icons.Filled.Star,
            colorFilter = ColorFilter.tint(AppTheme.colors.iconColor),
            contentDescription = "App Stats rating",
            modifier = Modifier.size(14.dp)
          )
          Text(
            text = TextFormatter.formatDecimal(app.rating.avgRating),
            style = AppTheme.typography.medium_XS,
            textAlign = TextAlign.Center
          )
        }
      }
      GradientButton(
        title = "INSTALL",
        modifier = Modifier
          .height(40.dp)
          .width(88.dp),
        gradient = if (bonusBanner != null) appCoinsButtonGradient else orangeGradient,
        style = AppTheme.typography.button_M,
        onClick = { TODO() },
      )
    }
  }
}

class AppGraphicProvider : PreviewParameterProvider<App> {
  override val values = sequenceOf(randomApp)
}
