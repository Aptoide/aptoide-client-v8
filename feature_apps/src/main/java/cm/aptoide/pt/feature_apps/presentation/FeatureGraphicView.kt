package cm.aptoide.pt.feature_apps.presentation

import android.util.DisplayMetrics
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.aptoide.pt.feature_apps.R
import cm.aptoide.pt.feature_apps.data.App
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import kotlin.math.roundToInt

@Preview(name = "Feature Graphic Item")
@Composable
internal fun AppGraphicView(
  @PreviewParameter(AppGraphicProvider::class) app: App,
  bonusBanner: Boolean = false,
) {

  val imageCornersPx =
    (16 * (LocalContext.current.resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()

  Column(modifier = Modifier
    .width(280.dp)
    .height(184.dp)
    .wrapContentSize(Alignment.Center)) {
    Box {
      Image(
        painter = rememberImagePainter(app.featureGraphic,
          builder = {
            placeholder(R.drawable.ic_placeholder)
            transformations(RoundedCornersTransformation(imageCornersPx.toFloat()))
          }),
        contentDescription = "App Graphic",
        modifier = Modifier
          .width(280.dp)
          .height(136.dp)
          .padding(bottom = 8.dp)
      )
      if (bonusBanner) {
        Text(text = "up to\n20%\nBONUS",
          textAlign = TextAlign.Center,
          fontSize = 12.sp,
          color = MaterialTheme.colors.primary,
          modifier = Modifier
            .background(Color.White,
              RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp))
            .size(64.dp)
            .padding(8.dp))
      }
    }

    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceBetween) {
      Image(
        painter = rememberImagePainter(app.icon, builder = {
          placeholder(R.drawable.ic_placeholder)
          transformations(RoundedCornersTransformation(imageCornersPx.toFloat()))
        }),
        contentDescription = "App Icon",
        modifier = Modifier.size(40.dp)
      )
      Text(app.name, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier
        .height(42.dp))

      Button(onClick = { /*TODO*/ }, shape = CircleShape) {
        Text("INSTALL", maxLines = 1)
      }
    }
  }
}

class AppGraphicProvider : PreviewParameterProvider<App> {
  override val values = listOf(App("Best App In the World", "teste", "teste", true)).asSequence()
}
