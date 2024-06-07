package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun TestAptoideGamesToolbarLogo() {
  Image(
    imageVector = getAptoideGamesToolbarLogo(Color.White),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getAptoideGamesToolbarLogo(color: Color): ImageVector =
  Builder(
    name = "Logo",
    defaultWidth = 145.0.dp,
    defaultHeight = 32.0.dp,
    viewportWidth = 145.0f,
    viewportHeight = 32.0f
  ).apply {
    path(
      fill = SolidColor(color),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = StrokeCap.Butt,
      strokeLineJoin = StrokeJoin.Miter,
      strokeLineMiter = 4.0f,
      pathFillType = PathFillType.NonZero
    ) {
      moveTo(128.327f, 29.257f)
      verticalLineTo(26.985f)
      horizontalLineTo(126.054f)
      verticalLineTo(22.06f)
      horizontalLineTo(130.978f)
      verticalLineTo(24.333f)
      horizontalLineTo(140.069f)
      verticalLineTo(19.788f)
      horizontalLineTo(137.796f)
      verticalLineTo(17.136f)
      horizontalLineTo(128.327f)
      verticalLineTo(14.864f)
      horizontalLineTo(126.054f)
      verticalLineTo(5.016f)
      horizontalLineTo(128.327f)
      verticalLineTo(2.743f)
      horizontalLineTo(142.72f)
      verticalLineTo(5.016f)
      horizontalLineTo(144.993f)
      verticalLineTo(9.94f)
      horizontalLineTo(140.069f)
      verticalLineTo(7.667f)
      horizontalLineTo(130.978f)
      verticalLineTo(12.212f)
      horizontalLineTo(140.447f)
      verticalLineTo(14.864f)
      horizontalLineTo(142.72f)
      verticalLineTo(17.136f)
      horizontalLineTo(144.993f)
      verticalLineTo(26.985f)
      horizontalLineTo(142.72f)
      verticalLineTo(29.257f)
      horizontalLineTo(128.327f)
      close()
    }
    path(
      fill = SolidColor(color),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = StrokeCap.Butt,
      strokeLineJoin = StrokeJoin.Miter,
      strokeLineMiter = 4.0f,
      pathFillType = PathFillType.NonZero
    ) {
      moveTo(104.085f, 5.029f)
      verticalLineTo(26.971f)
      horizontalLineTo(106.514f)
      verticalLineTo(29.257f)
      horizontalLineTo(123.402f)
      verticalLineTo(26.514f)
      horizontalLineTo(120.686f)
      verticalLineTo(24.333f)
      horizontalLineTo(109.009f)
      verticalLineTo(17.136f)
      horizontalLineTo(117.943f)
      verticalLineTo(15.086f)
      horizontalLineTo(120.751f)
      verticalLineTo(12.212f)
      horizontalLineTo(109.009f)
      verticalLineTo(7.667f)
      horizontalLineTo(123.402f)
      verticalLineTo(5.029f)
      horizontalLineTo(120.686f)
      verticalLineTo(2.743f)
      horizontalLineTo(106.514f)
      verticalLineTo(5.029f)
      horizontalLineTo(104.085f)
      close()
    }
    path(
      fill = SolidColor(color),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = StrokeCap.Butt,
      strokeLineJoin = StrokeJoin.Miter,
      strokeLineMiter = 4.0f,
      pathFillType = PathFillType.NonZero
    ) {
      moveTo(96.888f, 29.257f)
      verticalLineTo(14.864f)
      horizontalLineTo(94.616f)
      verticalLineTo(17.136f)
      horizontalLineTo(89.691f)
      verticalLineTo(14.864f)
      horizontalLineTo(87.419f)
      verticalLineTo(29.257f)
      horizontalLineTo(82.495f)
      verticalLineTo(2.743f)
      horizontalLineTo(87.419f)
      verticalLineTo(7.667f)
      horizontalLineTo(89.691f)
      verticalLineTo(9.94f)
      horizontalLineTo(94.616f)
      verticalLineTo(7.667f)
      horizontalLineTo(96.888f)
      verticalLineTo(2.743f)
      horizontalLineTo(101.812f)
      verticalLineTo(29.257f)
      horizontalLineTo(96.888f)
      close()
    }
    path(
      fill = SolidColor(color),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = StrokeCap.Butt,
      strokeLineJoin = StrokeJoin.Miter,
      strokeLineMiter = 4.0f,
      pathFillType = PathFillType.NonZero
    ) {
      moveTo(75.298f, 29.257f)
      verticalLineTo(22.06f)
      horizontalLineTo(65.829f)
      verticalLineTo(29.257f)
      horizontalLineTo(60.904f)
      verticalLineTo(9.94f)
      horizontalLineTo(63.177f)
      verticalLineTo(7.667f)
      horizontalLineTo(65.829f)
      verticalLineTo(5.016f)
      horizontalLineTo(68.101f)
      verticalLineTo(2.743f)
      horizontalLineTo(73.025f)
      verticalLineTo(5.016f)
      horizontalLineTo(75.298f)
      verticalLineTo(7.667f)
      horizontalLineTo(77.949f)
      verticalLineTo(9.94f)
      horizontalLineTo(80.222f)
      verticalLineTo(29.257f)
      horizontalLineTo(75.298f)
      close()
      moveTo(75.298f, 17.136f)
      verticalLineTo(12.591f)
      horizontalLineTo(73.025f)
      verticalLineTo(9.94f)
      horizontalLineTo(68.101f)
      verticalLineTo(12.591f)
      horizontalLineTo(65.829f)
      verticalLineTo(17.136f)
      horizontalLineTo(75.298f)
      close()
    }
    path(
      fill = SolidColor(color),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = StrokeCap.Butt,
      strokeLineJoin = StrokeJoin.Miter,
      strokeLineMiter = 4.0f,
      pathFillType = PathFillType.NonZero
    ) {
      moveTo(41.587f, 29.257f)
      verticalLineTo(26.985f)
      horizontalLineTo(39.314f)
      verticalLineTo(5.016f)
      horizontalLineTo(41.587f)
      verticalLineTo(2.743f)
      horizontalLineTo(55.98f)
      verticalLineTo(5.016f)
      horizontalLineTo(58.253f)
      verticalLineTo(9.974f)
      horizontalLineTo(53.708f)
      verticalLineTo(7.667f)
      horizontalLineTo(43.86f)
      verticalLineTo(24.333f)
      horizontalLineTo(53.329f)
      verticalLineTo(19.788f)
      horizontalLineTo(48.784f)
      verticalLineTo(14.864f)
      horizontalLineTo(58.253f)
      verticalLineTo(26.985f)
      horizontalLineTo(55.98f)
      verticalLineTo(29.257f)
      horizontalLineTo(41.587f)
      close()
    }
    path(
      fill = SolidColor(color),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = StrokeCap.Butt,
      strokeLineJoin = StrokeJoin.Miter,
      strokeLineMiter = 4.0f,
      pathFillType = PathFillType.EvenOdd
    ) {
      moveTo(4.571f, 0.0f)
      horizontalLineTo(27.429f)
      verticalLineTo(2.743f)
      horizontalLineTo(32.0f)
      verticalLineTo(29.257f)
      horizontalLineTo(27.429f)
      verticalLineTo(32.0f)
      horizontalLineTo(4.571f)
      verticalLineTo(27.429f)
      horizontalLineTo(0.0f)
      verticalLineTo(3.2f)
      horizontalLineTo(4.571f)
      verticalLineTo(0.0f)
      close()
      moveTo(22.857f, 6.857f)
      horizontalLineTo(20.571f)
      verticalLineTo(9.143f)
      horizontalLineTo(18.286f)
      verticalLineTo(11.886f)
      horizontalLineTo(16.0f)
      verticalLineTo(13.714f)
      horizontalLineTo(13.714f)
      verticalLineTo(11.429f)
      horizontalLineTo(11.429f)
      verticalLineTo(9.143f)
      horizontalLineTo(9.143f)
      verticalLineTo(6.857f)
      horizontalLineTo(6.857f)
      verticalLineTo(18.286f)
      horizontalLineTo(9.143f)
      verticalLineTo(20.571f)
      horizontalLineTo(11.429f)
      verticalLineTo(22.857f)
      horizontalLineTo(13.714f)
      verticalLineTo(25.143f)
      horizontalLineTo(18.286f)
      verticalLineTo(22.857f)
      horizontalLineTo(20.571f)
      verticalLineTo(20.571f)
      horizontalLineTo(22.857f)
      verticalLineTo(18.286f)
      horizontalLineTo(25.143f)
      verticalLineTo(9.143f)
      horizontalLineTo(22.857f)
      verticalLineTo(6.857f)
      close()
      moveTo(11.429f, 16.0f)
      verticalLineTo(18.286f)
      horizontalLineTo(9.143f)
      verticalLineTo(16.0f)
      horizontalLineTo(11.429f)
      close()
      moveTo(11.429f, 16.0f)
      horizontalLineTo(13.714f)
      verticalLineTo(13.714f)
      horizontalLineTo(11.429f)
      verticalLineTo(16.0f)
      close()
    }
  }.build()
