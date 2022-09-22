package cm.aptoide.pt.feature_apps.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
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
      .height(128.dp)
      .wrapContentSize(Alignment.Center)
  ) {
    Box(contentAlignment = Alignment.TopEnd) {
      Image(
        painter = rememberImagePainter(app.icon,
          builder = {
            placeholder(R.drawable.ic_placeholder)
            transformations(RoundedCornersTransformation(16f))
          }),
        contentDescription = "App Icon",
        modifier = Modifier.size(80.dp),

        )
      if (app.isAppCoins) {
        Image(
          painter = rememberImagePainter("https://s2.coinmarketcap.com/static/img/coins/64x64/2344.png"),
          contentDescription = "AppCoins Icon",
          modifier = Modifier.size(21.dp)
        )
      }
    }
    Text(
      app.name, maxLines = 2, modifier = Modifier
        .height(42.dp)
    )
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