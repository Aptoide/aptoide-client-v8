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
    Box(
      contentAlignment = Alignment.TopEnd,
      modifier = Modifier.padding(bottom = 4.dp)
    ) {
      Image(
        painter = rememberImagePainter(
          data = app.icon,
          builder = {
            placeholder(cm.aptoide.pt.aptoide_ui.R.drawable.ic_placeholder)
            //transformations(RoundedCornersTransformation(16f))
          }
        ),
        contentDescription = "App Icon",
        modifier = Modifier
          .size(80.dp)
          .clip(RoundedCornerShape(16.dp)),
      )
      if (app.isAppCoins) {
        Image(
          painter = rememberImagePainter(
            data = R.drawable.ic_appcoins_logo,
            builder = {
              placeholder(R.drawable.ic_appcoins_logo)
              //transformations(RoundedCornersTransformation())
            }
          ),
          contentDescription = "AppCoins Icon",
          modifier = Modifier.size(24.dp)
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
        painter = rememberImagePainter(
          data = R.drawable.ic_icon_star,
          builder = {
            placeholder(R.drawable.ic_icon_star)
            transformations(RoundedCornersTransformation())
          }
        ),
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
}

class AppProvider : PreviewParameterProvider<App> {
  override val values =
    listOf(
      App(
        name = "teste",
        packageName = "teste",
        md5 = "md5",
        appSize = 123,
        icon = "teste",
        malware = "trusted",
        rating = Rating(
          avgRating = 2.3,
          totalVotes = 12321,
          votes = listOf(
            Votes(1, 3),
            Votes(2, 8),
            Votes(3, 123),
            Votes(4, 100),
            Votes(5, 1994)
          )
        ),
        downloads = 123,
        versionName = "teste",
        versionCode = 123,
        featureGraphic = "teste",
        isAppCoins = true,
        screenshots = listOf("dasdsa", "dsadas"),
        description = "app description",
        store = Store(
          storeName = "rmota",
          icon = "rmota url",
          apps = 123,
          subscribers = 12313,
          downloads = 123123123123
        ),
        releaseDate = "13123",
        updateDate = "12313",
        website = "aptoide.com",
        email = "aptoide@aptoide.com",
        privacyPolicy = "none",
        permissions = listOf("permission 1", "permission 2"),
        file = File(
          vername = "asdas",
          vercode = 123,
          md5 = "md5",
          filesize = 123
        ),
        obb = null
      )
    ).asSequence()
}
