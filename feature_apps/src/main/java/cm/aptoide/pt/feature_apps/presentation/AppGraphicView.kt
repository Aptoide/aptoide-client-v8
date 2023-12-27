package cm.aptoide.pt.feature_apps.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.aptoide.pt.aptoide_ui.AptoideAsyncImage
import cm.aptoide.pt.aptoide_ui.textformatter.TextFormatter
import cm.aptoide.pt.aptoide_ui.theme.AppTheme
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp

@Preview(name = "Feature Graphic Item")
@Composable
fun AppGraphicView(
  @PreviewParameter(AppGraphicProvider::class) app: App,
  bonusBanner: Boolean = false,
  onAppClick: (String) -> Unit = {},
) {
  Column(
    modifier = Modifier
      .width(280.dp)
      .height(184.dp)
      .wrapContentSize(Alignment.Center)
      .clickable { onAppClick(app.packageName) }
  ) {
    Box {
      AptoideAsyncImage(
        data = app.featureGraphic,
        contentDescription = "App Graphic",
        placeholder = ColorPainter(AppTheme.colors.placeholderColor),
        modifier = Modifier
          .padding(bottom = 8.dp)
          .width(280.dp)
          .height(136.dp)
          .clip(RoundedCornerShape(16.dp))
      )
      if (bonusBanner) {
        Text(
          text = "up to\n20%\nBONUS",
          textAlign = TextAlign.Center,
          fontSize = 12.sp,
          color = MaterialTheme.colors.primary,
          modifier = Modifier
            .background(
              Color.White,
              RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp)
            )
            .size(64.dp)
            .padding(8.dp)
        )
      }
    }

    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceBetween) {
      AptoideAsyncImage(
        data = app.icon,
        contentDescription = "App Icon",
        placeholder = ColorPainter(AppTheme.colors.placeholderColor),
        modifier = Modifier
          .padding(end = 8.dp)
          .size(40.dp)
          .clip(RoundedCornerShape(16.dp))
      )
      Column(
        modifier = Modifier
          .padding(end = 8.dp)
          .weight(1f)
          .height(42.dp),
        verticalArrangement = Arrangement.Center
      ) {
        Text(
          text = app.name,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
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
            text = TextFormatter.formatDecimal(app.rating.avgRating),
            style = AppTheme.typography.medium_XS,
            textAlign = TextAlign.Center
          )
        }
      }
      Button(
        onClick = { TODO() },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .height(40.dp)
          .width(88.dp)
      ) {
        Text(
          text = "INSTALL",
          maxLines = 1,
          style = AppTheme.typography.button_M,
          color = Color.White
        )
      }
    }
  }
}

class AppGraphicProvider : PreviewParameterProvider<App> {
  override val values = listOf(randomApp).asSequence()
}
