package cm.aptoide.pt.app_games.drawables.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.app_games.theme.gray5

@Preview
@Composable
fun TestPlanetSearch() {
  Image(
    imageVector = getPlanetSearch(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getPlanetSearch(): ImageVector = ImageVector.Builder(
  name = "PlanetSearch",
  defaultWidth = 48.dp,
  defaultHeight = 48.dp,
  viewportWidth = 48f,
  viewportHeight = 48f,
).apply {
  path(
    fill = SolidColor(gray5),
  ) {
    moveTo(22.9f, 43.95f)
    curveTo(20.2667f, 43.8167f, 17.8f, 43.2f, 15.5f, 42.1f)
    curveTo(13.2f, 41f, 11.2f, 39.5583f, 9.5f, 37.775f)
    curveTo(7.8f, 35.9917f, 6.45833f, 33.9167f, 5.475f, 31.55f)
    curveTo(4.49167f, 29.1833f, 4f, 26.6667f, 4f, 24f)
    curveTo(4f, 21.2333f, 4.525f, 18.6333f, 5.575f, 16.2f)
    curveTo(6.625f, 13.7667f, 8.05f, 11.65f, 9.85f, 9.85f)
    curveTo(11.65f, 8.05f, 13.7667f, 6.625f, 16.2f, 5.575f)
    curveTo(18.6333f, 4.525f, 21.2333f, 4f, 24f, 4f)
    curveTo(28.9667f, 4f, 33.2833f, 5.56667f, 36.95f, 8.7f)
    curveTo(40.6167f, 11.8333f, 42.8667f, 15.7667f, 43.7f, 20.5f)
    horizontalLineTo(40.65f)
    curveTo(40.0833f, 17.7f, 38.9f, 15.2f, 37.1f, 13f)
    curveTo(35.3f, 10.8f, 33.05f, 9.15f, 30.35f, 8.05f)
    verticalLineTo(8.95f)
    curveTo(30.35f, 10.1167f, 29.95f, 11.1333f, 29.15f, 12f)
    curveTo(28.35f, 12.8667f, 27.3667f, 13.3f, 26.2f, 13.3f)
    horizontalLineTo(21.85f)
    verticalLineTo(17.65f)
    curveTo(21.85f, 18.2167f, 21.625f, 18.6833f, 21.175f, 19.05f)
    curveTo(20.725f, 19.4167f, 20.2167f, 19.6f, 19.65f, 19.6f)
    horizontalLineTo(15.5f)
    verticalLineTo(24f)
    horizontalLineTo(21f)
    verticalLineTo(30.25f)
    horizontalLineTo(17.65f)
    lineTo(7.45f, 20.05f)
    curveTo(7.28333f, 20.7167f, 7.16667f, 21.375f, 7.1f, 22.025f)
    curveTo(7.03333f, 22.675f, 7f, 23.3333f, 7f, 24f)
    curveTo(7f, 28.5f, 8.51667f, 32.3833f, 11.55f, 35.65f)
    curveTo(14.5833f, 38.9167f, 18.3667f, 40.6833f, 22.9f, 40.95f)
    verticalLineTo(43.95f)
    close()
    moveTo(42.5f, 42.65f)
    lineTo(35.8f, 35.95f)
    curveTo(35.1f, 36.45f, 34.3417f, 36.8333f, 33.525f, 37.1f)
    curveTo(32.7083f, 37.3667f, 31.8667f, 37.5f, 31f, 37.5f)
    curveTo(28.6333f, 37.5f, 26.625f, 36.675f, 24.975f, 35.025f)
    curveTo(23.325f, 33.375f, 22.5f, 31.3667f, 22.5f, 29f)
    curveTo(22.5f, 26.6333f, 23.325f, 24.625f, 24.975f, 22.975f)
    curveTo(26.625f, 21.325f, 28.6333f, 20.5f, 31f, 20.5f)
    curveTo(33.3667f, 20.5f, 35.375f, 21.325f, 37.025f, 22.975f)
    curveTo(38.675f, 24.625f, 39.5f, 26.6333f, 39.5f, 29f)
    curveTo(39.5f, 29.8667f, 39.3583f, 30.7083f, 39.075f, 31.525f)
    curveTo(38.7917f, 32.3417f, 38.4167f, 33.1167f, 37.95f, 33.85f)
    lineTo(44.65f, 40.5f)
    lineTo(42.5f, 42.65f)
    close()
    moveTo(31f, 34.5f)
    curveTo(32.5333f, 34.5f, 33.8333f, 33.9667f, 34.9f, 32.9f)
    curveTo(35.9667f, 31.8333f, 36.5f, 30.5333f, 36.5f, 29f)
    curveTo(36.5f, 27.4667f, 35.9667f, 26.1667f, 34.9f, 25.1f)
    curveTo(33.8333f, 24.0333f, 32.5333f, 23.5f, 31f, 23.5f)
    curveTo(29.4667f, 23.5f, 28.1667f, 24.0333f, 27.1f, 25.1f)
    curveTo(26.0333f, 26.1667f, 25.5f, 27.4667f, 25.5f, 29f)
    curveTo(25.5f, 30.5333f, 26.0333f, 31.8333f, 27.1f, 32.9f)
    curveTo(28.1667f, 33.9667f, 29.4667f, 34.5f, 31f, 34.5f)
    close()
  }
}.build()
