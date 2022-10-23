package cm.aptoide.pt.feature_apps.presentation

import android.util.DisplayMetrics
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
import androidx.compose.ui.platform.LocalContext
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
import kotlin.math.roundToInt

@Preview(name = "Feature Graphic Item")
@Composable
internal fun AppGraphicView(
  @PreviewParameter(AppGraphicProvider::class) app: App,
  bonusBanner: Boolean = false,
) {

  val imageCornersPx =
    (16 * (LocalContext.current.resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()

  Column(
    modifier = Modifier
      .width(280.dp)
      .height(184.dp)
      .wrapContentSize(Alignment.Center)
  ) {
    Box {
      Image(
        painter = rememberImagePainter(app.featureGraphic,
          builder = {
            placeholder(cm.aptoide.pt.aptoide_ui.R.drawable.ic_placeholder)
          }),
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
        painter = rememberImagePainter(app.icon, builder = {
          placeholder(R.drawable.ic_placeholder)
        }),
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
          .height(42.dp), verticalArrangement = Arrangement.Center
      ) {
        Text(
          app.name,
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
              R.drawable.ic_icon_star,
              builder = {
                placeholder(R.drawable.ic_icon_star)
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


      Button(
        onClick = { TODO() },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .height(40.dp)
          .width(88.dp)
      ) {
        Text(
          "INSTALL", maxLines = 1, style = AppTheme.typography.button_M,
          color = Color.White
        )
      }
    }
  }
}

class AppGraphicProvider : PreviewParameterProvider<App> {
  override val values = listOf(
    App(
      "Best App In the World",
      "teste",
      "md5",
      123,
      "teste",
      "tusted",
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
      listOf("", ""),
      "description",
      Store("rmota", "rmota url", 123, 12313, 123131241),
      "18 of may",
      "18 of may",
      "www.aptoide.com",
      "aptoide@aptoide.com",
      "none",
      listOf("Permission 1", "permission 2"),
      File("asdas", 123, "md5", 123), null
    )
  ).asSequence()
}
