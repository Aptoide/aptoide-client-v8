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
import com.aptoide.android.aptoidegames.theme.Palette

@Preview
@Composable
fun TestErrorOutlined() {
  Image(
    imageVector = getErrorOutlined(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getErrorOutlined(): ImageVector = ImageVector.Builder(
  name = "ErrorOutlined",
  defaultWidth = 16.dp,
  defaultHeight = 16.dp,
  viewportWidth = 16f,
  viewportHeight = 16f,
).apply {
  path(
    fill = SolidColor(Palette.Error),
  ) {
    moveTo(7.2f, 10.4f)
    horizontalLineTo(8.8f)
    verticalLineTo(12f)
    horizontalLineTo(7.2f)
    verticalLineTo(10.4f)
    close()
    moveTo(7.2f, 4f)
    horizontalLineTo(8.8f)
    verticalLineTo(8.8f)
    horizontalLineTo(7.2f)
    verticalLineTo(4f)
    close()
    moveTo(8f, 0f)
    curveTo(6.41775f, 0f, 4.87103f, 0.469192f, 3.55544f, 1.34824f)
    curveTo(2.23985f, 2.22729f, 1.21447f, 3.47672f, 0.608967f, 4.93853f)
    curveTo(0.00346627f, 6.40034f, -0.15496f, 8.00888f, 0.153721f, 9.56072f)
    curveTo(0.462403f, 11.1126f, 1.22433f, 12.538f, 2.34315f, 13.6569f)
    curveTo(3.46197f, 14.7757f, 4.88743f, 15.5376f, 6.43928f, 15.8463f)
    curveTo(7.99113f, 16.155f, 9.59966f, 15.9965f, 11.0615f, 15.391f)
    curveTo(12.5233f, 14.7855f, 13.7727f, 13.7602f, 14.6518f, 12.4446f)
    curveTo(15.5308f, 11.129f, 16f, 9.58225f, 16f, 8f)
    curveTo(16f, 6.94943f, 15.7931f, 5.90914f, 15.391f, 4.93853f)
    curveTo(14.989f, 3.96793f, 14.3997f, 3.08601f, 13.6569f, 2.34315f)
    curveTo(12.914f, 1.60028f, 12.0321f, 1.011f, 11.0615f, 0.608964f)
    curveTo(10.0909f, 0.206926f, 9.05058f, 0f, 8f, 0f)
    verticalLineTo(0f)
    close()
    moveTo(8f, 14.4f)
    curveTo(6.7342f, 14.4f, 5.49683f, 14.0246f, 4.44435f, 13.3214f)
    curveTo(3.39188f, 12.6182f, 2.57158f, 11.6186f, 2.08717f, 10.4492f)
    curveTo(1.60277f, 9.27973f, 1.47603f, 7.9929f, 1.72298f, 6.75142f)
    curveTo(1.96992f, 5.50994f, 2.57946f, 4.36957f, 3.47452f, 3.47452f)
    curveTo(4.36958f, 2.57946f, 5.50995f, 1.96992f, 6.75143f, 1.72297f)
    curveTo(7.9929f, 1.47603f, 9.27973f, 1.60277f, 10.4492f, 2.08717f)
    curveTo(11.6186f, 2.57157f, 12.6182f, 3.39188f, 13.3214f, 4.44435f)
    curveTo(14.0246f, 5.49683f, 14.4f, 6.7342f, 14.4f, 8f)
    curveTo(14.4f, 9.69739f, 13.7257f, 11.3253f, 12.5255f, 12.5255f)
    curveTo(11.3253f, 13.7257f, 9.69739f, 14.4f, 8f, 14.4f)
    verticalLineTo(14.4f)
    close()
  }
}.build()
