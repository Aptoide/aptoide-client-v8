package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun TestGetBonusIcon() {
  Image(
    imageVector = getBonusIcon(Color.Black, Color.Green),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getBonusIcon(outlineColor: Color, giftColor: Color): ImageVector = ImageVector.Builder(
  name = "featured_seasonal_and_gifts",
  defaultWidth = 28.dp,
  defaultHeight = 28.dp,
  viewportWidth = 28f,
  viewportHeight = 28f,
).apply {
  path(
    fill = SolidColor(giftColor),
  ) {
    moveTo(22.8891f, 12.8889f)
    verticalLineTo(25.1112f)
    horizontalLineTo(5.11133f)
    verticalLineTo(12.8889f)
    horizontalLineTo(2.88911f)
    verticalLineTo(6.22228f)
    horizontalLineTo(8.66688f)
    curveTo(8.59281f, 6.03709f, 8.53725f, 5.85654f, 8.50022f, 5.68061f)
    curveTo(8.46318f, 5.50468f, 8.44466f, 5.31487f, 8.44466f, 5.11117f)
    curveTo(8.44466f, 4.18524f, 8.76874f, 3.3982f, 9.41688f, 2.75005f)
    curveTo(10.065f, 2.10191f, 10.8521f, 1.77783f, 11.778f, 1.77783f)
    curveTo(12.2039f, 1.77783f, 12.6021f, 1.85191f, 12.9724f, 2.00005f)
    curveTo(13.3428f, 2.1482f, 13.6854f, 2.37042f, 14.0002f, 2.66672f)
    curveTo(14.315f, 2.38894f, 14.6576f, 2.17135f, 15.028f, 2.01394f)
    curveTo(15.3984f, 1.85654f, 15.7965f, 1.77783f, 16.2224f, 1.77783f)
    curveTo(17.1484f, 1.77783f, 17.9354f, 2.10191f, 18.5836f, 2.75005f)
    curveTo(19.2317f, 3.3982f, 19.5558f, 4.18524f, 19.5558f, 5.11117f)
    curveTo(19.5558f, 5.31487f, 19.5419f, 5.50931f, 19.5141f, 5.6945f)
    curveTo(19.4863f, 5.87968f, 19.4261f, 6.05561f, 19.3335f, 6.22228f)
    horizontalLineTo(25.1113f)
    verticalLineTo(12.8889f)
    horizontalLineTo(22.8891f)
    close()
  }
  path(
    fill = SolidColor(outlineColor),
  ) {
    moveTo(22.8891f, 25.1112f)
    verticalLineTo(12.8889f)
    horizontalLineTo(25.1113f)
    verticalLineTo(6.22228f)
    horizontalLineTo(19.3335f)
    curveTo(19.4261f, 6.05561f, 19.4863f, 5.87968f, 19.5141f, 5.6945f)
    curveTo(19.5419f, 5.50931f, 19.5558f, 5.31487f, 19.5558f, 5.11117f)
    curveTo(19.5558f, 4.18524f, 19.2317f, 3.3982f, 18.5836f, 2.75005f)
    curveTo(17.9354f, 2.10191f, 17.1484f, 1.77783f, 16.2224f, 1.77783f)
    curveTo(15.7965f, 1.77783f, 15.3984f, 1.85654f, 15.028f, 2.01394f)
    curveTo(14.6576f, 2.17135f, 14.315f, 2.38894f, 14.0002f, 2.66672f)
    curveTo(13.6854f, 2.37042f, 13.3428f, 2.1482f, 12.9724f, 2.00005f)
    curveTo(12.6021f, 1.85191f, 12.2039f, 1.77783f, 11.778f, 1.77783f)
    curveTo(10.8521f, 1.77783f, 10.065f, 2.10191f, 9.41688f, 2.75005f)
    curveTo(8.76874f, 3.3982f, 8.44466f, 4.18524f, 8.44466f, 5.11117f)
    curveTo(8.44466f, 5.31487f, 8.46318f, 5.50468f, 8.50022f, 5.68061f)
    curveTo(8.53725f, 5.85654f, 8.59281f, 6.03709f, 8.66688f, 6.22228f)
    horizontalLineTo(2.88911f)
    verticalLineTo(12.8889f)
    horizontalLineTo(5.11133f)
    verticalLineTo(25.1112f)
    horizontalLineTo(22.8891f)
    close()
    moveTo(11.778f, 4.00005f)
    curveTo(12.0928f, 4.00005f, 12.3567f, 4.10654f, 12.5697f, 4.3195f)
    curveTo(12.7826f, 4.53246f, 12.8891f, 4.79635f, 12.8891f, 5.11117f)
    curveTo(12.8891f, 5.42598f, 12.7826f, 5.68987f, 12.5697f, 5.90283f)
    curveTo(12.3567f, 6.1158f, 12.0928f, 6.22228f, 11.778f, 6.22228f)
    curveTo(11.4632f, 6.22228f, 11.1993f, 6.1158f, 10.9863f, 5.90283f)
    curveTo(10.7734f, 5.68987f, 10.6669f, 5.42598f, 10.6669f, 5.11117f)
    curveTo(10.6669f, 4.79635f, 10.7734f, 4.53246f, 10.9863f, 4.3195f)
    curveTo(11.1993f, 4.10654f, 11.4632f, 4.00005f, 11.778f, 4.00005f)
    close()
    moveTo(17.3336f, 5.11117f)
    curveTo(17.3336f, 5.42598f, 17.2271f, 5.68987f, 17.0141f, 5.90283f)
    curveTo(16.8011f, 6.1158f, 16.5373f, 6.22228f, 16.2224f, 6.22228f)
    curveTo(15.9076f, 6.22228f, 15.6437f, 6.1158f, 15.4308f, 5.90283f)
    curveTo(15.2178f, 5.68987f, 15.1113f, 5.42598f, 15.1113f, 5.11117f)
    curveTo(15.1113f, 4.79635f, 15.2178f, 4.53246f, 15.4308f, 4.3195f)
    curveTo(15.6437f, 4.10654f, 15.9076f, 4.00005f, 16.2224f, 4.00005f)
    curveTo(16.5373f, 4.00005f, 16.8011f, 4.10654f, 17.0141f, 4.3195f)
    curveTo(17.2271f, 4.53246f, 17.3336f, 4.79635f, 17.3336f, 5.11117f)
    close()
    moveTo(22.8891f, 8.4445f)
    verticalLineTo(10.6667f)
    horizontalLineTo(15.1113f)
    verticalLineTo(8.4445f)
    horizontalLineTo(22.8891f)
    close()
    moveTo(15.1113f, 22.8889f)
    verticalLineTo(12.8889f)
    horizontalLineTo(20.6669f)
    verticalLineTo(22.8889f)
    horizontalLineTo(15.1113f)
    close()
    moveTo(12.8891f, 22.8889f)
    horizontalLineTo(7.33355f)
    verticalLineTo(12.8889f)
    horizontalLineTo(12.8891f)
    verticalLineTo(22.8889f)
    close()
    moveTo(5.11133f, 10.6667f)
    verticalLineTo(8.4445f)
    horizontalLineTo(12.8891f)
    verticalLineTo(10.6667f)
    horizontalLineTo(5.11133f)
    close()
  }
}.build()
