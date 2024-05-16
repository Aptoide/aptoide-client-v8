package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.primary

@Preview
@Composable
fun TestSingleGamepad() {
  Image(
    imageVector = getSingleGamepad(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getSingleGamepad(): ImageVector = ImageVector.Builder(
  name = "SingleGamepad",
  defaultWidth = 40.dp,
  defaultHeight = 40.dp,
  viewportWidth = 40f,
  viewportHeight = 40f,
).apply {
  path(
    fill = SolidColor(primary),
  ) {
    moveTo(3f, 31.6667f)
    lineTo(6.25f, 8.33337f)
    horizontalLineTo(34.3333f)
    lineTo(37.5833f, 31.6667f)
    horizontalLineTo(30.9583f)
    lineTo(25.9583f, 26.6667f)
    horizontalLineTo(14.625f)
    lineTo(9.625f, 31.6667f)
    horizontalLineTo(3f)
    close()
    moveTo(28.625f, 21.6667f)
    curveTo(29.0972f, 21.6667f, 29.4931f, 21.507f, 29.8125f, 21.1875f)
    curveTo(30.1319f, 20.8681f, 30.2917f, 20.4723f, 30.2917f, 20f)
    curveTo(30.2917f, 19.5278f, 30.1319f, 19.132f, 29.8125f, 18.8125f)
    curveTo(29.4931f, 18.4931f, 29.0972f, 18.3334f, 28.625f, 18.3334f)
    curveTo(28.1528f, 18.3334f, 27.7569f, 18.4931f, 27.4375f, 18.8125f)
    curveTo(27.1181f, 19.132f, 26.9583f, 19.5278f, 26.9583f, 20f)
    curveTo(26.9583f, 20.4723f, 27.1181f, 20.8681f, 27.4375f, 21.1875f)
    curveTo(27.7569f, 21.507f, 28.1528f, 21.6667f, 28.625f, 21.6667f)
    close()
    moveTo(25.2917f, 16.6667f)
    curveTo(25.7639f, 16.6667f, 26.1597f, 16.507f, 26.4792f, 16.1875f)
    curveTo(26.7986f, 15.8681f, 26.9583f, 15.4723f, 26.9583f, 15f)
    curveTo(26.9583f, 14.5278f, 26.7986f, 14.132f, 26.4792f, 13.8125f)
    curveTo(26.1597f, 13.4931f, 25.7639f, 13.3334f, 25.2917f, 13.3334f)
    curveTo(24.8194f, 13.3334f, 24.4236f, 13.4931f, 24.1042f, 13.8125f)
    curveTo(23.7847f, 14.132f, 23.625f, 14.5278f, 23.625f, 15f)
    curveTo(23.625f, 15.4723f, 23.7847f, 15.8681f, 24.1042f, 16.1875f)
    curveTo(24.4236f, 16.507f, 24.8194f, 16.6667f, 25.2917f, 16.6667f)
    close()
    moveTo(13.2083f, 21.6667f)
    horizontalLineTo(15.7083f)
    verticalLineTo(18.75f)
    horizontalLineTo(18.625f)
    verticalLineTo(16.25f)
    horizontalLineTo(15.7083f)
    verticalLineTo(13.3334f)
    horizontalLineTo(13.2083f)
    verticalLineTo(16.25f)
    horizontalLineTo(10.2917f)
    verticalLineTo(18.75f)
    horizontalLineTo(13.2083f)
    verticalLineTo(21.6667f)
    close()
  }
}.build()
