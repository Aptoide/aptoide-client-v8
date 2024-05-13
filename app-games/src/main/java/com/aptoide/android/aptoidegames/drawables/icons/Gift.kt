package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.primary

@Preview
@Composable
fun TestGift() {
  Image(
    imageVector = getGift(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getGift(): ImageVector = ImageVector.Builder(
  name = "Gift",
  defaultWidth = 88.dp,
  defaultHeight = 88.dp,
  viewportWidth = 88f,
  viewportHeight = 88f,
).apply {
  path(
    pathFillType = PathFillType.EvenOdd,
    fill = SolidColor(primary),
  ) {
    moveTo(40.3f, 21.8f)
    horizontalLineTo(47.7f)
    verticalLineTo(14.4f)
    lineTo(55.1001f, 14.4f)
    verticalLineTo(21.8f)
    lineTo(51.4004f, 21.8f)
    horizontalLineTo(36.6004f)
    verticalLineTo(29.2f)
    horizontalLineTo(40.3003f)
    lineTo(40.3003f, 36.6f)
    horizontalLineTo(47.7003f)
    lineTo(47.7003f, 29.2f)
    horizontalLineTo(51.4004f)
    lineTo(66.2003f, 29.2f)
    lineTo(66.2004f, 29.2f)
    horizontalLineTo(81.0003f)
    lineTo(81.0003f, 21.8f)
    horizontalLineTo(66.2004f)
    lineTo(66.2003f, 21.8f)
    lineTo(62.5001f, 21.8f)
    lineTo(55.1004f, 7.0006f)
    verticalLineTo(7.00001f)
    lineTo(55.1001f, 7.00001f)
    lineTo(47.7f, 7f)
    horizontalLineTo(40.3f)
    lineTo(32.9004f, 7.00001f)
    lineTo(25.5004f, 21.8f)
    lineTo(32.9004f, 21.8f)
    lineTo(32.9004f, 14.4f)
    lineTo(40.3f, 14.4f)
    verticalLineTo(21.8f)
    close()
    moveTo(36.5998f, 36.6f)
    horizontalLineTo(21.8f)
    verticalLineTo(36.6f)
    lineTo(14.4004f, 36.6f)
    lineTo(14.4004f, 29.2f)
    horizontalLineTo(21.7999f)
    horizontalLineTo(21.8f)
    horizontalLineTo(36.5999f)
    lineTo(36.5998f, 21.8f)
    horizontalLineTo(21.8f)
    horizontalLineTo(21.7999f)
    horizontalLineTo(7f)
    lineTo(7f, 29.2f)
    horizontalLineTo(7.00045f)
    lineTo(7.00045f, 36.6f)
    lineTo(7f, 36.6f)
    lineTo(7f, 44f)
    horizontalLineTo(14.4003f)
    lineTo(14.4003f, 58.8f)
    lineTo(14.4003f, 73.6f)
    lineTo(14.4003f, 81f)
    horizontalLineTo(29.2001f)
    horizontalLineTo(29.2003f)
    horizontalLineTo(44f)
    horizontalLineTo(44.0001f)
    horizontalLineTo(58.8f)
    lineTo(73.6f, 81f)
    lineTo(73.6f, 73.6f)
    verticalLineTo(58.8f)
    lineTo(64.788f, 58.8f)
    horizontalLineTo(73.6003f)
    lineTo(73.6003f, 44f)
    lineTo(81.0003f, 44f)
    lineTo(81.0003f, 36.6f)
    lineTo(81.0001f, 36.6f)
    verticalLineTo(29.2f)
    horizontalLineTo(73.6002f)
    lineTo(73.6001f, 36.6f)
    lineTo(66.2004f, 36.6f)
    lineTo(66.2003f, 36.6f)
    horizontalLineTo(51.4004f)
    verticalLineTo(36.6f)
    horizontalLineTo(36.6004f)
    verticalLineTo(44f)
    horizontalLineTo(36.5998f)
    lineTo(36.5998f, 36.6f)
    close()
  }
}.build()
