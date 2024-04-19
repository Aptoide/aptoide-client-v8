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
import cm.aptoide.pt.app_games.theme.gray4

@Preview
@Composable
fun TestHistoryOutlined() {
  Image(
    imageVector = getHistoryOutlined(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getHistoryOutlined(): ImageVector = ImageVector.Builder(
  name = "HistoryOutlined",
  defaultWidth = 24.dp,
  defaultHeight = 24.dp,
  viewportWidth = 24f,
  viewportHeight = 24f,
).apply {
  path(
    fill = SolidColor(gray4),
  ) {
    moveTo(13f, 3f)
    curveTo(8.13058f, 3f, 4.1635f, 6.86853f, 4.00493f, 11.6994f)
    curveTo(3.99949f, 11.865f, 3.86569f, 12f, 3.7f, 12f)
    horizontalLineTo(1.72426f)
    curveTo(1.45699f, 12f, 1.32314f, 12.3231f, 1.51213f, 12.5121f)
    lineTo(4.8784f, 15.8784f)
    curveTo(4.88608f, 15.8861f, 4.89248f, 15.895f, 4.89734f, 15.9047f)
    verticalLineTo(15.9047f)
    curveTo(4.92818f, 15.9664f, 5.01037f, 15.9798f, 5.0592f, 15.931f)
    lineTo(8.48633f, 12.5124f)
    curveTo(8.67568f, 12.3235f, 8.54192f, 12f, 8.27446f, 12f)
    horizontalLineTo(6.3f)
    curveTo(6.13431f, 12f, 5.99934f, 11.8653f, 6.0063f, 11.6998f)
    curveTo(6.16321f, 7.9694f, 9.23059f, 5f, 13f, 5f)
    curveTo(16.87f, 5f, 20f, 8.13f, 20f, 12f)
    curveTo(20f, 15.87f, 16.87f, 19f, 13f, 19f)
    curveTo(11.4314f, 19f, 9.98175f, 18.4782f, 8.81739f, 17.601f)
    curveTo(8.37411f, 17.267f, 7.74104f, 17.259f, 7.3486f, 17.6514f)
    verticalLineTo(17.6514f)
    curveTo(6.95725f, 18.0428f, 6.95413f, 18.6823f, 7.38598f, 19.0284f)
    curveTo(8.92448f, 20.2615f, 10.8708f, 21f, 13f, 21f)
    curveTo(17.97f, 21f, 22f, 16.97f, 22f, 12f)
    curveTo(22f, 7.03f, 17.97f, 3f, 13f, 3f)
    close()
    moveTo(12.3f, 8f)
    curveTo(12.1343f, 8f, 12f, 8.13431f, 12f, 8.3f)
    verticalLineTo(12.8291f)
    curveTo(12f, 12.935f, 12.0559f, 13.0331f, 12.147f, 13.0872f)
    lineTo(15.9936f, 15.368f)
    curveTo(16.1355f, 15.4521f, 16.3186f, 15.4059f, 16.4037f, 15.2646f)
    lineTo(16.8645f, 14.4986f)
    curveTo(16.9502f, 14.356f, 16.9036f, 14.1709f, 16.7606f, 14.086f)
    lineTo(13.6468f, 12.2372f)
    curveTo(13.5558f, 12.1831f, 13.5f, 12.0851f, 13.5f, 11.9792f)
    verticalLineTo(8.3f)
    curveTo(13.5f, 8.13431f, 13.3657f, 8f, 13.2f, 8f)
    horizontalLineTo(12.3f)
    close()
  }
}.build()
