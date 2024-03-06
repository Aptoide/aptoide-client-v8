package cm.aptoide.pt.app_games.drawables.backgrounds

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
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
fun GetSettingsDialogBackground() {
  Image(
    imageVector = getSettingsDialogBackground(),
    contentDescription = null,
  )
}

fun getSettingsDialogBackground(): ImageVector = ImageVector.Builder(
  name = "NotificationsDialog",
  defaultWidth = 328.0.dp,
  defaultHeight = 279.0.dp,
  viewportWidth = 328.0f,
  viewportHeight = 279.0f
).apply {
  group {
    path(
      fill = SolidColor(Color(0xFFF5F5F5)),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = StrokeCap.Butt,
      strokeLineJoin = StrokeJoin.Miter,
      strokeLineMiter = 4.0f,
      pathFillType = PathFillType.NonZero
    ) {
      moveTo(-6.0f, -137.0f)
      horizontalLineToRelative(484.0f)
      verticalLineToRelative(482.0f)
      horizontalLineToRelative(-484.0f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFF5F5F5)),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = StrokeCap.Butt,
      strokeLineJoin = StrokeJoin.Miter,
      strokeLineMiter = 4.0f,
      pathFillType = PathFillType.NonZero
    ) {
      moveTo(-6.0f, -137.0f)
      horizontalLineToRelative(484.0f)
      verticalLineToRelative(482.0f)
      horizontalLineToRelative(-484.0f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFBEBEBE)),
      stroke = null,
      fillAlpha = 0.1f,
      strokeAlpha = 0.1f,
      strokeLineWidth = 0.0f,
      strokeLineCap = StrokeCap.Butt,
      strokeLineJoin = StrokeJoin.Miter,
      strokeLineMiter = 4.0f,
      pathFillType = PathFillType.NonZero
    ) {
      moveTo(328.338f, 276.338f)
      moveToRelative(-319.399f, 93.939f)
      arcToRelative(332.927f, 332.927f, 118.611f, true, true, 638.798f, -187.878f)
      arcToRelative(332.927f, 332.927f, 118.611f, true, true, -638.798f, 187.878f)
    }
    path(
      fill = SolidColor(Color(0xFFBEBEBE)),
      stroke = null,
      fillAlpha = 0.2f,
      strokeAlpha = 0.2f,
      strokeLineWidth = 0.0f,
      strokeLineCap = StrokeCap.Butt,
      strokeLineJoin = StrokeJoin.Miter,
      strokeLineMiter = 4.0f,
      pathFillType = PathFillType.NonZero
    ) {
      moveTo(328.108f, 276.005f)
      moveToRelative(-85.557f, -169.449f)
      arcToRelative(189.823f, 189.823f, 108.21f, true, true, 171.114f, 338.897f)
      arcToRelative(189.823f, 189.823f, 108.21f, true, true, -171.114f, -338.897f)
    }
    path(
      fill = SolidColor(Color(0xFFBEBEBE)),
      stroke = null,
      fillAlpha = 0.1f,
      strokeAlpha = 0.1f,
      strokeLineWidth = 0.0f,
      strokeLineCap = StrokeCap.Butt,
      strokeLineJoin = StrokeJoin.Miter,
      strokeLineMiter = 4.0f,
      pathFillType = PathFillType.NonZero
    ) {
      moveTo(328.567f, 276.585f)
      moveToRelative(-33.076f, -65.509f)
      arcToRelative(73.386f, 73.386f, 108.21f, true, true, 66.153f, 131.018f)
      arcToRelative(73.386f, 73.386f, 108.21f, true, true, -66.153f, -131.018f)
    }
  }
}
  .build()
