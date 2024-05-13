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
fun TestBug() {
  Image(
    imageVector = getBug(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getBug(): ImageVector = ImageVector.Builder(
  name = "Bug",
  defaultWidth = 88.dp,
  defaultHeight = 88.dp,
  viewportWidth = 88f,
  viewportHeight = 88f,
).apply {
  path(
    fill = SolidColor(primary),
  ) {
    moveTo(44.0001f, 77f)
    curveTo(40.0279f, 77f, 36.3459f, 76.0222f, 32.9542f, 74.0667f)
    curveTo(29.5626f, 72.1111f, 26.889f, 69.4222f, 24.9334f, 66f)
    horizontalLineTo(14.6667f)
    verticalLineTo(58.6667f)
    horizontalLineTo(22.3667f)
    curveTo(22.1834f, 57.4444f, 22.0765f, 56.2222f, 22.0459f, 55f)
    curveTo(22.0154f, 53.7778f, 22.0001f, 52.5556f, 22.0001f, 51.3333f)
    horizontalLineTo(14.6667f)
    verticalLineTo(44f)
    horizontalLineTo(22.0001f)
    curveTo(22.0001f, 42.7778f, 22.0154f, 41.5556f, 22.0459f, 40.3333f)
    curveTo(22.0765f, 39.1111f, 22.1834f, 37.8889f, 22.3667f, 36.6667f)
    horizontalLineTo(14.6667f)
    verticalLineTo(29.3333f)
    horizontalLineTo(24.9334f)
    curveTo(25.789f, 27.9278f, 26.7515f, 26.6139f, 27.8209f, 25.3917f)
    curveTo(28.8904f, 24.1694f, 30.1279f, 23.1f, 31.5334f, 22.1833f)
    lineTo(25.6667f, 16.1333f)
    lineTo(30.8001f, 11f)
    lineTo(38.6834f, 18.8833f)
    curveTo(40.3945f, 18.3333f, 42.1362f, 18.0583f, 43.9084f, 18.0583f)
    curveTo(45.6806f, 18.0583f, 47.4223f, 18.3333f, 49.1334f, 18.8833f)
    lineTo(57.2001f, 11f)
    lineTo(62.3334f, 16.1333f)
    lineTo(56.2834f, 22.1833f)
    curveTo(57.689f, 23.1f, 58.957f, 24.1542f, 60.0876f, 25.3458f)
    curveTo(61.2181f, 26.5375f, 62.2112f, 27.8667f, 63.0667f, 29.3333f)
    horizontalLineTo(73.3334f)
    verticalLineTo(36.6667f)
    horizontalLineTo(65.6334f)
    curveTo(65.8167f, 37.8889f, 65.9237f, 39.1111f, 65.9543f, 40.3333f)
    curveTo(65.9848f, 41.5556f, 66.0001f, 42.7778f, 66.0001f, 44f)
    horizontalLineTo(73.3334f)
    verticalLineTo(51.3333f)
    horizontalLineTo(66.0001f)
    curveTo(66.0001f, 52.5556f, 65.9848f, 53.7778f, 65.9543f, 55f)
    curveTo(65.9237f, 56.2222f, 65.8167f, 57.4444f, 65.6334f, 58.6667f)
    horizontalLineTo(73.3334f)
    verticalLineTo(66f)
    horizontalLineTo(63.0667f)
    curveTo(61.1112f, 69.4222f, 58.4376f, 72.1111f, 55.0459f, 74.0667f)
    curveTo(51.6542f, 76.0222f, 47.9723f, 77f, 44.0001f, 77f)
    close()
    moveTo(36.6667f, 58.6667f)
    horizontalLineTo(51.3334f)
    verticalLineTo(51.3333f)
    horizontalLineTo(36.6667f)
    verticalLineTo(58.6667f)
    close()
    moveTo(36.6667f, 44f)
    horizontalLineTo(51.3334f)
    verticalLineTo(36.6667f)
    horizontalLineTo(36.6667f)
    verticalLineTo(44f)
    close()
  }
}.build()
