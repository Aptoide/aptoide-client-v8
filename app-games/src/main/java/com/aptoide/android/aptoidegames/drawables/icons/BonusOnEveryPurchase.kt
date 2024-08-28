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
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
private fun BonusOnEveryPurchasePreview() {
  Image(
    imageVector = getBonusOnEveryPurchase(Color.Green, Color.Black, Color.Magenta, Color.Gray),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getBonusOnEveryPurchase(
  iconColor: Color,
  outlineColor: Color,
  backgroundColor: Color,
  themeColor: Color,
): ImageVector = ImageVector.Builder(
  name = "A bonus on every purchase",
  defaultWidth = 360.dp,
  defaultHeight = 296.dp,
  viewportWidth = 360f,
  viewportHeight = 296f
).apply {
  path(
    fill = SolidColor(backgroundColor),
    fillAlpha = 1.0f,
    stroke = null,
    strokeAlpha = 1.0f,
    strokeLineWidth = 1.0f,
    strokeLineCap = StrokeCap.Butt,
    strokeLineJoin = StrokeJoin.Miter,
    strokeLineMiter = 1.0f,
    pathFillType = PathFillType.NonZero
  ) {
    moveTo(0f, 40f)
    horizontalLineTo(360f)
    verticalLineTo(275f)
    horizontalLineTo(0f)
    verticalLineTo(40f)
    close()
  }
  path(
    fill = SolidColor(themeColor),
    fillAlpha = 1.0f,
    stroke = null,
    strokeAlpha = 1.0f,
    strokeLineWidth = 1.0f,
    strokeLineCap = StrokeCap.Butt,
    strokeLineJoin = StrokeJoin.Miter,
    strokeLineMiter = 1.0f,
    pathFillType = PathFillType.NonZero
  ) {
    moveTo(120f, 264f)
    horizontalLineTo(203f)
    verticalLineTo(280f)
    horizontalLineTo(120f)
    verticalLineTo(264f)
    close()
  }
  path(
    fill = SolidColor(backgroundColor),
    fillAlpha = 1.0f,
    stroke = null,
    strokeAlpha = 1.0f,
    strokeLineWidth = 1.0f,
    strokeLineCap = StrokeCap.Butt,
    strokeLineJoin = StrokeJoin.Miter,
    strokeLineMiter = 1.0f,
    pathFillType = PathFillType.EvenOdd
  ) {
    moveTo(319f, 290f)
    verticalLineTo(269f)
    horizontalLineTo(314.125f)
    verticalLineTo(256f)
    horizontalLineTo(280f)
    verticalLineTo(290f)
    horizontalLineTo(285.85f)
    verticalLineTo(296f)
    horizontalLineTo(306.325f)
    verticalLineTo(290f)
    horizontalLineTo(319f)
    close()
  }
  group {
    path(
      fill = SolidColor(iconColor),
      fillAlpha = 1.0f,
      stroke = null,
      strokeAlpha = 1.0f,
      strokeLineWidth = 1.0f,
      strokeLineCap = StrokeCap.Butt,
      strokeLineJoin = StrokeJoin.Miter,
      strokeLineMiter = 1.0f,
      pathFillType = PathFillType.NonZero
    ) {
      moveTo(308.166f, 274.889f)
      verticalLineTo(287.111f)
      horizontalLineTo(290.833f)
      verticalLineTo(274.889f)
      horizontalLineTo(288.666f)
      verticalLineTo(268.222f)
      horizontalLineTo(294.3f)
      curveTo(294.227f, 268.037f, 294.173f, 267.857f, 294.137f, 267.681f)
      curveTo(294.101f, 267.505f, 294.083f, 267.315f, 294.083f, 267.111f)
      curveTo(294.083f, 266.185f, 294.399f, 265.398f, 295.031f, 264.75f)
      curveTo(295.663f, 264.102f, 296.43f, 263.778f, 297.333f, 263.778f)
      curveTo(297.748f, 263.778f, 298.136f, 263.852f, 298.497f, 264f)
      curveTo(298.859f, 264.148f, 299.193f, 264.37f, 299.5f, 264.667f)
      curveTo(299.806f, 264.389f, 300.141f, 264.171f, 300.502f, 264.014f)
      curveTo(300.863f, 263.857f, 301.251f, 263.778f, 301.666f, 263.778f)
      curveTo(302.569f, 263.778f, 303.336f, 264.102f, 303.968f, 264.75f)
      curveTo(304.6f, 265.398f, 304.916f, 266.185f, 304.916f, 267.111f)
      curveTo(304.916f, 267.315f, 304.903f, 267.509f, 304.876f, 267.694f)
      curveTo(304.849f, 267.88f, 304.79f, 268.056f, 304.7f, 268.222f)
      horizontalLineTo(310.333f)
      verticalLineTo(274.889f)
      horizontalLineTo(308.166f)
      close()
    }
    path(
      fill = SolidColor(outlineColor),
      fillAlpha = 1.0f,
      stroke = null,
      strokeAlpha = 1.0f,
      strokeLineWidth = 1.0f,
      strokeLineCap = StrokeCap.Butt,
      strokeLineJoin = StrokeJoin.Miter,
      strokeLineMiter = 1.0f,
      pathFillType = PathFillType.NonZero
    ) {
      moveTo(308.166f, 287.111f)
      verticalLineTo(274.889f)
      horizontalLineTo(310.333f)
      verticalLineTo(268.222f)
      horizontalLineTo(304.7f)
      curveTo(304.79f, 268.056f, 304.849f, 267.88f, 304.876f, 267.694f)
      curveTo(304.903f, 267.509f, 304.916f, 267.315f, 304.916f, 267.111f)
      curveTo(304.916f, 266.185f, 304.6f, 265.398f, 303.968f, 264.75f)
      curveTo(303.336f, 264.102f, 302.569f, 263.778f, 301.666f, 263.778f)
      curveTo(301.251f, 263.778f, 300.863f, 263.857f, 300.502f, 264.014f)
      curveTo(300.141f, 264.171f, 299.806f, 264.389f, 299.5f, 264.667f)
      curveTo(299.193f, 264.37f, 298.859f, 264.148f, 298.497f, 264f)
      curveTo(298.136f, 263.852f, 297.748f, 263.778f, 297.333f, 263.778f)
      curveTo(296.43f, 263.778f, 295.663f, 264.102f, 295.031f, 264.75f)
      curveTo(294.399f, 265.398f, 294.083f, 266.185f, 294.083f, 267.111f)
      curveTo(294.083f, 267.315f, 294.101f, 267.505f, 294.137f, 267.681f)
      curveTo(294.173f, 267.857f, 294.227f, 268.037f, 294.3f, 268.222f)
      horizontalLineTo(288.666f)
      verticalLineTo(274.889f)
      horizontalLineTo(290.833f)
      verticalLineTo(287.111f)
      horizontalLineTo(308.166f)
      close()
      moveTo(297.333f, 266f)
      curveTo(297.64f, 266f, 297.897f, 266.107f, 298.105f, 266.319f)
      curveTo(298.312f, 266.532f, 298.416f, 266.796f, 298.416f, 267.111f)
      curveTo(298.416f, 267.426f, 298.312f, 267.69f, 298.105f, 267.903f)
      curveTo(297.897f, 268.116f, 297.64f, 268.222f, 297.333f, 268.222f)
      curveTo(297.026f, 268.222f, 296.769f, 268.116f, 296.561f, 267.903f)
      curveTo(296.353f, 267.69f, 296.25f, 267.426f, 296.25f, 267.111f)
      curveTo(296.25f, 266.796f, 296.353f, 266.532f, 296.561f, 266.319f)
      curveTo(296.769f, 266.107f, 297.026f, 266f, 297.333f, 266f)
      close()
      moveTo(302.75f, 267.111f)
      curveTo(302.75f, 267.426f, 302.646f, 267.69f, 302.438f, 267.903f)
      curveTo(302.23f, 268.116f, 301.973f, 268.222f, 301.666f, 268.222f)
      curveTo(301.359f, 268.222f, 301.102f, 268.116f, 300.894f, 267.903f)
      curveTo(300.687f, 267.69f, 300.583f, 267.426f, 300.583f, 267.111f)
      curveTo(300.583f, 266.796f, 300.687f, 266.532f, 300.894f, 266.319f)
      curveTo(301.102f, 266.107f, 301.359f, 266f, 301.666f, 266f)
      curveTo(301.973f, 266f, 302.23f, 266.107f, 302.438f, 266.319f)
      curveTo(302.646f, 266.532f, 302.75f, 266.796f, 302.75f, 267.111f)
      close()
      moveTo(308.166f, 270.444f)
      verticalLineTo(272.667f)
      horizontalLineTo(300.583f)
      verticalLineTo(270.444f)
      horizontalLineTo(308.166f)
      close()
      moveTo(300.583f, 284.889f)
      verticalLineTo(274.889f)
      horizontalLineTo(306f)
      verticalLineTo(284.889f)
      horizontalLineTo(300.583f)
      close()
      moveTo(298.416f, 284.889f)
      horizontalLineTo(293f)
      verticalLineTo(274.889f)
      horizontalLineTo(298.416f)
      verticalLineTo(284.889f)
      close()
      moveTo(290.833f, 272.667f)
      verticalLineTo(270.444f)
      horizontalLineTo(298.416f)
      verticalLineTo(272.667f)
      horizontalLineTo(290.833f)
      close()
    }
  }
  path(
    fill = SolidColor(backgroundColor),
    fillAlpha = 1.0f,
    stroke = null,
    strokeAlpha = 1.0f,
    strokeLineWidth = 1.0f,
    strokeLineCap = StrokeCap.Butt,
    strokeLineJoin = StrokeJoin.Miter,
    strokeLineMiter = 1.0f,
    pathFillType = PathFillType.EvenOdd
  ) {
    moveTo(352f, 50f)
    verticalLineTo(29f)
    horizontalLineTo(347f)
    verticalLineTo(16f)
    horizontalLineTo(312f)
    verticalLineTo(50f)
    horizontalLineTo(318f)
    verticalLineTo(56f)
    horizontalLineTo(339f)
    verticalLineTo(50f)
    horizontalLineTo(352f)
    close()
  }
  group {
    path(
      fill = SolidColor(iconColor),
      fillAlpha = 1.0f,
      stroke = null,
      strokeAlpha = 1.0f,
      strokeLineWidth = 1.0f,
      strokeLineCap = StrokeCap.Butt,
      strokeLineJoin = StrokeJoin.Miter,
      strokeLineMiter = 1.0f,
      pathFillType = PathFillType.NonZero
    ) {
      moveTo(340.889f, 34.8889f)
      verticalLineTo(47.1112f)
      horizontalLineTo(323.111f)
      verticalLineTo(34.8889f)
      horizontalLineTo(320.889f)
      verticalLineTo(28.2223f)
      horizontalLineTo(326.667f)
      curveTo(326.593f, 28.0371f, 326.537f, 27.8565f, 326.5f, 27.6806f)
      curveTo(326.463f, 27.5047f, 326.445f, 27.3149f, 326.445f, 27.1112f)
      curveTo(326.445f, 26.1852f, 326.769f, 25.3982f, 327.417f, 24.7501f)
      curveTo(328.065f, 24.1019f, 328.852f, 23.7778f, 329.778f, 23.7778f)
      curveTo(330.204f, 23.7778f, 330.602f, 23.8519f, 330.972f, 24.0001f)
      curveTo(331.343f, 24.1482f, 331.685f, 24.3704f, 332f, 24.6667f)
      curveTo(332.315f, 24.3889f, 332.658f, 24.1714f, 333.028f, 24.0139f)
      curveTo(333.398f, 23.8565f, 333.797f, 23.7778f, 334.222f, 23.7778f)
      curveTo(335.148f, 23.7778f, 335.935f, 24.1019f, 336.584f, 24.7501f)
      curveTo(337.232f, 25.3982f, 337.556f, 26.1852f, 337.556f, 27.1112f)
      curveTo(337.556f, 27.3149f, 337.542f, 27.5093f, 337.514f, 27.6945f)
      curveTo(337.486f, 27.8797f, 337.426f, 28.0556f, 337.334f, 28.2223f)
      horizontalLineTo(343.111f)
      verticalLineTo(34.8889f)
      horizontalLineTo(340.889f)
      close()
    }
    path(
      fill = SolidColor(outlineColor),
      fillAlpha = 1.0f,
      stroke = null,
      strokeAlpha = 1.0f,
      strokeLineWidth = 1.0f,
      strokeLineCap = StrokeCap.Butt,
      strokeLineJoin = StrokeJoin.Miter,
      strokeLineMiter = 1.0f,
      pathFillType = PathFillType.NonZero
    ) {
      moveTo(340.889f, 47.1112f)
      verticalLineTo(34.8889f)
      horizontalLineTo(343.111f)
      verticalLineTo(28.2223f)
      horizontalLineTo(337.334f)
      curveTo(337.426f, 28.0556f, 337.486f, 27.8797f, 337.514f, 27.6945f)
      curveTo(337.542f, 27.5093f, 337.556f, 27.3149f, 337.556f, 27.1112f)
      curveTo(337.556f, 26.1852f, 337.232f, 25.3982f, 336.584f, 24.7501f)
      curveTo(335.935f, 24.1019f, 335.148f, 23.7778f, 334.222f, 23.7778f)
      curveTo(333.797f, 23.7778f, 333.398f, 23.8565f, 333.028f, 24.0139f)
      curveTo(332.658f, 24.1714f, 332.315f, 24.3889f, 332f, 24.6667f)
      curveTo(331.685f, 24.3704f, 331.343f, 24.1482f, 330.972f, 24.0001f)
      curveTo(330.602f, 23.8519f, 330.204f, 23.7778f, 329.778f, 23.7778f)
      curveTo(328.852f, 23.7778f, 328.065f, 24.1019f, 327.417f, 24.7501f)
      curveTo(326.769f, 25.3982f, 326.445f, 26.1852f, 326.445f, 27.1112f)
      curveTo(326.445f, 27.3149f, 326.463f, 27.5047f, 326.5f, 27.6806f)
      curveTo(326.537f, 27.8565f, 326.593f, 28.0371f, 326.667f, 28.2223f)
      horizontalLineTo(320.889f)
      verticalLineTo(34.8889f)
      horizontalLineTo(323.111f)
      verticalLineTo(47.1112f)
      horizontalLineTo(340.889f)
      close()
      moveTo(329.778f, 26.0001f)
      curveTo(330.093f, 26.0001f, 330.357f, 26.1065f, 330.57f, 26.3195f)
      curveTo(330.783f, 26.5325f, 330.889f, 26.7964f, 330.889f, 27.1112f)
      curveTo(330.889f, 27.426f, 330.783f, 27.6899f, 330.57f, 27.9028f)
      curveTo(330.357f, 28.1158f, 330.093f, 28.2223f, 329.778f, 28.2223f)
      curveTo(329.463f, 28.2223f, 329.199f, 28.1158f, 328.986f, 27.9028f)
      curveTo(328.773f, 27.6899f, 328.667f, 27.426f, 328.667f, 27.1112f)
      curveTo(328.667f, 26.7964f, 328.773f, 26.5325f, 328.986f, 26.3195f)
      curveTo(329.199f, 26.1065f, 329.463f, 26.0001f, 329.778f, 26.0001f)
      close()
      moveTo(335.334f, 27.1112f)
      curveTo(335.334f, 27.426f, 335.227f, 27.6899f, 335.014f, 27.9028f)
      curveTo(334.801f, 28.1158f, 334.537f, 28.2223f, 334.222f, 28.2223f)
      curveTo(333.908f, 28.2223f, 333.644f, 28.1158f, 333.431f, 27.9028f)
      curveTo(333.218f, 27.6899f, 333.111f, 27.426f, 333.111f, 27.1112f)
      curveTo(333.111f, 26.7964f, 333.218f, 26.5325f, 333.431f, 26.3195f)
      curveTo(333.644f, 26.1065f, 333.908f, 26.0001f, 334.222f, 26.0001f)
      curveTo(334.537f, 26.0001f, 334.801f, 26.1065f, 335.014f, 26.3195f)
      curveTo(335.227f, 26.5325f, 335.334f, 26.7964f, 335.334f, 27.1112f)
      close()
      moveTo(340.889f, 30.4445f)
      verticalLineTo(32.6667f)
      horizontalLineTo(333.111f)
      verticalLineTo(30.4445f)
      horizontalLineTo(340.889f)
      close()
      moveTo(333.111f, 44.8889f)
      verticalLineTo(34.8889f)
      horizontalLineTo(338.667f)
      verticalLineTo(44.8889f)
      horizontalLineTo(333.111f)
      close()
      moveTo(330.889f, 44.8889f)
      horizontalLineTo(325.334f)
      verticalLineTo(34.8889f)
      horizontalLineTo(330.889f)
      verticalLineTo(44.8889f)
      close()
      moveTo(323.111f, 32.6667f)
      verticalLineTo(30.4445f)
      horizontalLineTo(330.889f)
      verticalLineTo(32.6667f)
      horizontalLineTo(323.111f)
      close()
    }
  }
  path(
    fill = SolidColor(backgroundColor),
    fillAlpha = 1.0f,
    stroke = null,
    strokeAlpha = 1.0f,
    strokeLineWidth = 1.0f,
    strokeLineCap = StrokeCap.Butt,
    strokeLineJoin = StrokeJoin.Miter,
    strokeLineMiter = 1.0f,
    pathFillType = PathFillType.NonZero
  ) {
    moveTo(40f, 10f)
    horizontalLineTo(262f)
    verticalLineTo(34f)
    horizontalLineTo(40f)
    verticalLineTo(10f)
    close()
  }
  path(
    fill = SolidColor(backgroundColor),
    fillAlpha = 1.0f,
    stroke = null,
    strokeAlpha = 1.0f,
    strokeLineWidth = 1.0f,
    strokeLineCap = StrokeCap.Butt,
    strokeLineJoin = StrokeJoin.Miter,
    strokeLineMiter = 1.0f,
    pathFillType = PathFillType.EvenOdd
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
  group {
    path(
      fill = SolidColor(iconColor),
      fillAlpha = 1.0f,
      stroke = null,
      strokeAlpha = 1.0f,
      strokeLineWidth = 1.0f,
      strokeLineCap = StrokeCap.Butt,
      strokeLineJoin = StrokeJoin.Miter,
      strokeLineMiter = 1.0f,
      pathFillType = PathFillType.NonZero
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
      curveTo(14.4447f, 10.1852f, 14.7687f, 9.3982f, 15.4169f, 8.75f)
      curveTo(16.065f, 8.1019f, 16.8521f, 7.7778f, 17.778f, 7.7778f)
      curveTo(18.2039f, 7.7778f, 18.6021f, 7.8519f, 18.9724f, 8f)
      curveTo(19.3428f, 8.1482f, 19.6854f, 8.3704f, 20.0002f, 8.6667f)
      curveTo(20.315f, 8.3889f, 20.6576f, 8.1714f, 21.028f, 8.0139f)
      curveTo(21.3984f, 7.8565f, 21.7965f, 7.7778f, 22.2224f, 7.7778f)
      curveTo(23.1484f, 7.7778f, 23.9354f, 8.1019f, 24.5836f, 8.75f)
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
      fillAlpha = 1.0f,
      stroke = null,
      strokeAlpha = 1.0f,
      strokeLineWidth = 1.0f,
      strokeLineCap = StrokeCap.Butt,
      strokeLineJoin = StrokeJoin.Miter,
      strokeLineMiter = 1.0f,
      pathFillType = PathFillType.NonZero
    ) {
      moveTo(28.8891f, 31.1112f)
      verticalLineTo(18.8889f)
      horizontalLineTo(31.1113f)
      verticalLineTo(12.2223f)
      horizontalLineTo(25.3336f)
      curveTo(25.4261f, 12.0556f, 25.4863f, 11.8797f, 25.5141f, 11.6945f)
      curveTo(25.5419f, 11.5093f, 25.5558f, 11.3149f, 25.5558f, 11.1112f)
      curveTo(25.5558f, 10.1852f, 25.2317f, 9.3982f, 24.5836f, 8.75f)
      curveTo(23.9354f, 8.1019f, 23.1484f, 7.7778f, 22.2224f, 7.7778f)
      curveTo(21.7965f, 7.7778f, 21.3984f, 7.8565f, 21.028f, 8.0139f)
      curveTo(20.6576f, 8.1714f, 20.315f, 8.3889f, 20.0002f, 8.6667f)
      curveTo(19.6854f, 8.3704f, 19.3428f, 8.1482f, 18.9724f, 8f)
      curveTo(18.6021f, 7.8519f, 18.2039f, 7.7778f, 17.778f, 7.7778f)
      curveTo(16.8521f, 7.7778f, 16.065f, 8.1019f, 15.4169f, 8.75f)
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
  }
}.build()
