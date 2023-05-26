package cm.aptoide.pt.feature_apps.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.aptoide.pt.aptoide_ui.textformatter.TextFormatter
import cm.aptoide.pt.aptoide_ui.theme.AppTheme
import cm.aptoide.pt.feature_apps.R
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.File
import cm.aptoide.pt.feature_apps.domain.Rating
import cm.aptoide.pt.feature_apps.domain.Store
import cm.aptoide.pt.feature_apps.domain.Votes
import coil.compose.rememberImagePainter

@Preview(name = "Feature Graphic Item")
@Composable
fun AppGraphicView(
  @PreviewParameter(AppGraphicProvider::class) app: App,
  bonusBanner: Boolean = false,
) {
  Column(
    modifier = Modifier
      .width(280.dp)
      .height(184.dp)
      .wrapContentSize(Alignment.Center)
  ) {
    Box {
      Image(
        painter = rememberImagePainter(
          data = app.featureGraphic,
          builder = { placeholder(cm.aptoide.pt.aptoide_ui.R.drawable.ic_placeholder) }
        ),
        contentDescription = "App Graphic",
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
      Image(
        painter = rememberImagePainter(
          data = app.icon,
          builder = { placeholder(R.drawable.ic_placeholder) }
        ),
        contentDescription = "App Icon",
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
            painter = rememberImagePainter(
              data = R.drawable.ic_icon_star,
              builder = { placeholder(R.drawable.ic_icon_star) }
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
  override val values = listOf(
    App(
      name = "Best App In the World",
      packageName = "teste",
      md5 = "md5",
      appSize = 123,
      icon = "teste",
      malware = "tusted",
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
      pRating = Rating(
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
      screenshots = listOf("", ""),
      description = "description",
      videos = listOf("", ""),
      store = Store(
        storeName = "rmota",
        icon = "rmota url",
        apps = 123,
        subscribers = 12313,
        downloads = 123131241
      ),
      releaseDate = "18 of may",
      updateDate = "18 of may",
      website = "www.aptoide.com",
      email = "aptoide@aptoide.com",
      privacyPolicy = "none",
      permissions = listOf("Permission 1", "permission 2"),
      file = File(
        vername = "asdas",
        vercode = 123,
        md5 = "md5",
        filesize = 123,
        path = null,
        path_alt = null
      ),
      obb = null,
      developerName = null
    )
  ).asSequence()
}
