package cm.aptoide.pt.feature_apps.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.feature_apps.data.App
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation


class AppProvider : PreviewParameterProvider<App> {
  override val values = listOf(App("teste", "teste", "teste", true)).asSequence()
}

@Preview(name = "Feature Graphic Item")
@Composable
internal fun AppGraphicView(
  @PreviewParameter(AppProvider::class) app: App,
  bonusBanner: Boolean = false,
) {
  Box() {
    Image(
      painter = rememberImagePainter(app.featureGraphic,
        builder = {
          transformations(RoundedCornersTransformation(16f))
        }),
      contentDescription = "App Graphic",
      modifier = Modifier
        .width(280.dp)
        .height(136.dp)
        .padding(bottom = 8.dp)
    )
  }

  Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceBetween) {
    Image(
      painter = rememberImagePainter(app.icon, builder = {
        transformations(RoundedCornersTransformation(16f))
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
