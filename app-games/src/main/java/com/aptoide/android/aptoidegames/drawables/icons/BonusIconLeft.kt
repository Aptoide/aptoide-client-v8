package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun TestBonusIconLeft() {
  Image(
    imageVector = getBonusIconLeft(Color.Green, Color.Black, Color.Gray),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getBonusIconLeft(
  iconColor: Color,
  outlineColor: Color,
  backgroundColor: Color,
): ImageVector = ImageVector.Builder(
  name = "BonusIconLeft",
  defaultWidth = 40.dp,
  defaultHeight = 40.dp,
  viewportWidth = 40f,
  viewportHeight = 40f,
).apply {
  path(
    pathFillType = PathFillType.EvenOdd,
    fill = SolidColor(backgroundColor),
  ) {
    moveTo(40f, 34f)
    verticalLineTo(13f)
    horizontalLineTo(35f)
    verticalLineTo(0f)
    horizontalLineTo(0f)
    verticalLineTo(34f)
    horizontalLineTo(6f)
    verticalLineTo(40f)
    horizontalLineTo(27f)
    verticalLineTo(34f)
    horizontalLineTo(40f)
    close()
  }
  path(
    fill = SolidColor(iconColor),
  ) {
    moveTo(28.8891f, 18.8889f)
    verticalLineTo(31.1112f)
    horizontalLineTo(11.1113f)
    verticalLineTo(18.8889f)
    horizontalLineTo(8.88911f)
    verticalLineTo(12.2223f)
    horizontalLineTo(14.6669f)
    curveTo(14.5928f, 12.0371f, 14.5373f, 11.8565f, 14.5002f, 11.6806f)
    curveTo(14.4632f, 11.5047f, 14.4447f, 11.3149f, 14.4447f, 11.1112f)
    curveTo(14.4447f, 10.1852f, 14.7687f, 9.3982f, 15.4169f, 8.75005f)
    curveTo(16.065f, 8.10191f, 16.8521f, 7.77783f, 17.778f, 7.77783f)
    curveTo(18.2039f, 7.77783f, 18.6021f, 7.85191f, 18.9724f, 8.00005f)
    curveTo(19.3428f, 8.1482f, 19.6854f, 8.37042f, 20.0002f, 8.66672f)
    curveTo(20.315f, 8.38894f, 20.6576f, 8.17135f, 21.028f, 8.01394f)
    curveTo(21.3984f, 7.85654f, 21.7965f, 7.77783f, 22.2224f, 7.77783f)
    curveTo(23.1484f, 7.77783f, 23.9354f, 8.10191f, 24.5836f, 8.75005f)
    curveTo(25.2317f, 9.3982f, 25.5558f, 10.1852f, 25.5558f, 11.1112f)
    curveTo(25.5558f, 11.3149f, 25.5419f, 11.5093f, 25.5141f, 11.6945f)
    curveTo(25.4863f, 11.8797f, 25.4261f, 12.0556f, 25.3335f, 12.2223f)
    horizontalLineTo(31.1113f)
    verticalLineTo(18.8889f)
    horizontalLineTo(28.8891f)
    close()
  }
  path(
    fill = SolidColor(outlineColor),
  ) {
    moveTo(28.8891f, 31.1112f)
    verticalLineTo(18.8889f)
    horizontalLineTo(31.1113f)
    verticalLineTo(12.2223f)
    horizontalLineTo(25.3335f)
    curveTo(25.4261f, 12.0556f, 25.4863f, 11.8797f, 25.5141f, 11.6945f)
    curveTo(25.5419f, 11.5093f, 25.5558f, 11.3149f, 25.5558f, 11.1112f)
    curveTo(25.5558f, 10.1852f, 25.2317f, 9.3982f, 24.5836f, 8.75005f)
    curveTo(23.9354f, 8.10191f, 23.1484f, 7.77783f, 22.2224f, 7.77783f)
    curveTo(21.7965f, 7.77783f, 21.3984f, 7.85654f, 21.028f, 8.01394f)
    curveTo(20.6576f, 8.17135f, 20.315f, 8.38894f, 20.0002f, 8.66672f)
    curveTo(19.6854f, 8.37042f, 19.3428f, 8.1482f, 18.9724f, 8.00005f)
    curveTo(18.6021f, 7.85191f, 18.2039f, 7.77783f, 17.778f, 7.77783f)
    curveTo(16.8521f, 7.77783f, 16.065f, 8.10191f, 15.4169f, 8.75005f)
    curveTo(14.7687f, 9.3982f, 14.4447f, 10.1852f, 14.4447f, 11.1112f)
    curveTo(14.4447f, 11.3149f, 14.4632f, 11.5047f, 14.5002f, 11.6806f)
    curveTo(14.5373f, 11.8565f, 14.5928f, 12.0371f, 14.6669f, 12.2223f)
    horizontalLineTo(8.88911f)
    verticalLineTo(18.8889f)
    horizontalLineTo(11.1113f)
    verticalLineTo(31.1112f)
    horizontalLineTo(28.8891f)
    close()
    moveTo(17.778f, 10.0001f)
    curveTo(18.0928f, 10.0001f, 18.3567f, 10.1065f, 18.5697f, 10.3195f)
    curveTo(18.7826f, 10.5325f, 18.8891f, 10.7964f, 18.8891f, 11.1112f)
    curveTo(18.8891f, 11.426f, 18.7826f, 11.6899f, 18.5697f, 11.9028f)
    curveTo(18.3567f, 12.1158f, 18.0928f, 12.2223f, 17.778f, 12.2223f)
    curveTo(17.4632f, 12.2223f, 17.1993f, 12.1158f, 16.9863f, 11.9028f)
    curveTo(16.7734f, 11.6899f, 16.6669f, 11.426f, 16.6669f, 11.1112f)
    curveTo(16.6669f, 10.7964f, 16.7734f, 10.5325f, 16.9863f, 10.3195f)
    curveTo(17.1993f, 10.1065f, 17.4632f, 10.0001f, 17.778f, 10.0001f)
    close()
    moveTo(23.3336f, 11.1112f)
    curveTo(23.3336f, 11.426f, 23.2271f, 11.6899f, 23.0141f, 11.9028f)
    curveTo(22.8011f, 12.1158f, 22.5373f, 12.2223f, 22.2224f, 12.2223f)
    curveTo(21.9076f, 12.2223f, 21.6437f, 12.1158f, 21.4308f, 11.9028f)
    curveTo(21.2178f, 11.6899f, 21.1113f, 11.426f, 21.1113f, 11.1112f)
    curveTo(21.1113f, 10.7964f, 21.2178f, 10.5325f, 21.4308f, 10.3195f)
    curveTo(21.6437f, 10.1065f, 21.9076f, 10.0001f, 22.2224f, 10.0001f)
    curveTo(22.5373f, 10.0001f, 22.8011f, 10.1065f, 23.0141f, 10.3195f)
    curveTo(23.2271f, 10.5325f, 23.3336f, 10.7964f, 23.3336f, 11.1112f)
    close()
    moveTo(28.8891f, 14.4445f)
    verticalLineTo(16.6667f)
    horizontalLineTo(21.1113f)
    verticalLineTo(14.4445f)
    horizontalLineTo(28.8891f)
    close()
    moveTo(21.1113f, 28.8889f)
    verticalLineTo(18.8889f)
    horizontalLineTo(26.6669f)
    verticalLineTo(28.8889f)
    horizontalLineTo(21.1113f)
    close()
    moveTo(18.8891f, 28.8889f)
    horizontalLineTo(13.3336f)
    verticalLineTo(18.8889f)
    horizontalLineTo(18.8891f)
    verticalLineTo(28.8889f)
    close()
    moveTo(11.1113f, 16.6667f)
    verticalLineTo(14.4445f)
    horizontalLineTo(18.8891f)
    verticalLineTo(16.6667f)
    horizontalLineTo(11.1113f)
    close()
  }
}.build()
