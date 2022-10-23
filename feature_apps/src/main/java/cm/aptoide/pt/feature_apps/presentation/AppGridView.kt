package cm.aptoide.pt.feature_apps.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.aptoide_ui.textformatter.TextFormatter
import cm.aptoide.pt.aptoide_ui.theme.AppTheme
import cm.aptoide.pt.feature_apps.R
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.File
import cm.aptoide.pt.feature_apps.domain.Rating
import cm.aptoide.pt.feature_apps.domain.Store
import cm.aptoide.pt.feature_apps.domain.Votes
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation

@Preview
@Composable
internal fun AppGridView(@PreviewParameter(AppProvider::class) app: App) {
  Column(
    modifier = Modifier
      .width(80.dp)
      .height(132.dp)
      .wrapContentSize(Alignment.Center)
  ) {
    Box(contentAlignment = Alignment.TopEnd, modifier = Modifier.padding(bottom = 4.dp)) {
      Image(
        painter = rememberImagePainter(app.icon,
          builder = {
            placeholder(cm.aptoide.pt.aptoide_ui.R.drawable.ic_placeholder)
            //transformations(RoundedCornersTransformation(16f))
          }),
        contentDescription = "App Icon",
        modifier = Modifier
          .size(80.dp)
          .clip(RoundedCornerShape(16.dp)),
      )
      if (app.isAppCoins) {
        Image(
          painter = rememberImagePainter(R.drawable.ic_appcoins_logo,
            builder = {
              placeholder(R.drawable.ic_appcoins_logo)
              //transformations(RoundedCornersTransformation())
            }),
          contentDescription = "AppCoins Icon",
          modifier = Modifier.size(24.dp)
        )
      }
    }
    Text(
      app.name, maxLines = 2, modifier = Modifier
        .height(32.dp), style = AppTheme.typography.medium_XS
    )
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .height(16.dp)
        .fillMaxWidth()
    ) {
      Image(
        painter = rememberImagePainter(
          R.drawable.ic_icon_star,
          builder = {
            placeholder(R.drawable.ic_icon_star)
            transformations(RoundedCornersTransformation())
          }),
        contentDescription = "App Stats rating",
        modifier = Modifier
          .padding(end = 4.dp)
          .width(12.dp)
          .height(12.dp)
      )
      Text(
        text = TextFormatter.formatDecimal(app.rating.avgRating),
        style = AppTheme.typography.medium_XS, textAlign = TextAlign.Center
      )
    }
  }
}

class AppProvider : PreviewParameterProvider<App> {
  override val values =
    listOf(
      App(
        "teste",
        "teste",
        "md5",
        123,
        "teste",
        "trusted",
        Rating(
          2.3,
          12321,
          listOf(Votes(1, 3), Votes(2, 8), Votes(3, 123), Votes(4, 100), Votes(5, 1994))
        ),
        123,
        "teste",
        123,
        "teste",
        true,
        listOf("dasdsa", "dsadas"),
        "app description",
        Store("rmota", "rmota url", 123, 12313, 123123123123),
        "13123",
        "12313",
        "aptoide.com",
        "aptoide@aptoide.com",
        "none",
        listOf("permission 1", "permission 2"),
        File("asdas", 123, "md5", 123), null
      )
    ).asSequence()
}