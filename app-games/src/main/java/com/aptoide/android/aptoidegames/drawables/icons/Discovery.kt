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
import com.aptoide.android.aptoidegames.theme.greyLight

@Preview
@Composable
fun TestDiscovery() {
  Image(
    imageVector = getDiscovery(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getDiscovery(): ImageVector = ImageVector.Builder(
  name = "Discovery",
  defaultWidth = 24.dp,
  defaultHeight = 24.dp,
  viewportWidth = 24f,
  viewportHeight = 24f,
).apply {
  path(
    fill = SolidColor(greyLight),
  ) {
    moveTo(7.62498f, 17.8001f)
    curveTo(8.02081f, 17.8001f, 8.35727f, 17.6616f, 8.63435f, 17.3845f)
    curveTo(8.91143f, 17.1074f, 9.04998f, 16.7709f, 9.04998f, 16.3751f)
    curveTo(9.04998f, 15.9793f, 8.91143f, 15.6428f, 8.63435f, 15.3657f)
    curveTo(8.35727f, 15.0886f, 8.02081f, 14.9501f, 7.62498f, 14.9501f)
    curveTo(7.22914f, 14.9501f, 6.89268f, 15.0886f, 6.6156f, 15.3657f)
    curveTo(6.33852f, 15.6428f, 6.19998f, 15.9793f, 6.19998f, 16.3751f)
    curveTo(6.19998f, 16.7709f, 6.33852f, 17.1074f, 6.6156f, 17.3845f)
    curveTo(6.89268f, 17.6616f, 7.22914f, 17.8001f, 7.62498f, 17.8001f)
    close()
    moveTo(7.62498f, 9.0501f)
    curveTo(8.02081f, 9.0501f, 8.35727f, 8.91156f, 8.63435f, 8.63447f)
    curveTo(8.91143f, 8.35739f, 9.04998f, 8.02093f, 9.04998f, 7.6251f)
    curveTo(9.04998f, 7.22926f, 8.91143f, 6.89281f, 8.63435f, 6.61572f)
    curveTo(8.35727f, 6.33864f, 8.02081f, 6.2001f, 7.62498f, 6.2001f)
    curveTo(7.22914f, 6.2001f, 6.89268f, 6.33864f, 6.6156f, 6.61572f)
    curveTo(6.33852f, 6.89281f, 6.19998f, 7.22926f, 6.19998f, 7.6251f)
    curveTo(6.19998f, 8.02093f, 6.33852f, 8.35739f, 6.6156f, 8.63447f)
    curveTo(6.89268f, 8.91156f, 7.22914f, 9.0501f, 7.62498f, 9.0501f)
    close()
    moveTo(12f, 13.4251f)
    curveTo(12.3958f, 13.4251f, 12.7323f, 13.2866f, 13.0094f, 13.0095f)
    curveTo(13.2864f, 12.7324f, 13.425f, 12.3959f, 13.425f, 12.0001f)
    curveTo(13.425f, 11.6043f, 13.2864f, 11.2678f, 13.0094f, 10.9907f)
    curveTo(12.7323f, 10.7136f, 12.3958f, 10.5751f, 12f, 10.5751f)
    curveTo(11.6041f, 10.5751f, 11.2677f, 10.7136f, 10.9906f, 10.9907f)
    curveTo(10.7135f, 11.2678f, 10.575f, 11.6043f, 10.575f, 12.0001f)
    curveTo(10.575f, 12.3959f, 10.7135f, 12.7324f, 10.9906f, 13.0095f)
    curveTo(11.2677f, 13.2866f, 11.6041f, 13.4251f, 12f, 13.4251f)
    close()
    moveTo(16.375f, 17.8001f)
    curveTo(16.7708f, 17.8001f, 17.1073f, 17.6616f, 17.3844f, 17.3845f)
    curveTo(17.6614f, 17.1074f, 17.8f, 16.7709f, 17.8f, 16.3751f)
    curveTo(17.8f, 15.9793f, 17.6614f, 15.6428f, 17.3844f, 15.3657f)
    curveTo(17.1073f, 15.0886f, 16.7708f, 14.9501f, 16.375f, 14.9501f)
    curveTo(15.9791f, 14.9501f, 15.6427f, 15.0886f, 15.3656f, 15.3657f)
    curveTo(15.0885f, 15.6428f, 14.95f, 15.9793f, 14.95f, 16.3751f)
    curveTo(14.95f, 16.7709f, 15.0885f, 17.1074f, 15.3656f, 17.3845f)
    curveTo(15.6427f, 17.6616f, 15.9791f, 17.8001f, 16.375f, 17.8001f)
    close()
    moveTo(16.375f, 9.0501f)
    curveTo(16.7708f, 9.0501f, 17.1073f, 8.91156f, 17.3844f, 8.63447f)
    curveTo(17.6614f, 8.35739f, 17.8f, 8.02093f, 17.8f, 7.6251f)
    curveTo(17.8f, 7.22926f, 17.6614f, 6.89281f, 17.3844f, 6.61572f)
    curveTo(17.1073f, 6.33864f, 16.7708f, 6.2001f, 16.375f, 6.2001f)
    curveTo(15.9791f, 6.2001f, 15.6427f, 6.33864f, 15.3656f, 6.61572f)
    curveTo(15.0885f, 6.89281f, 14.95f, 7.22926f, 14.95f, 7.6251f)
    curveTo(14.95f, 8.02093f, 15.0885f, 8.35739f, 15.3656f, 8.63447f)
    curveTo(15.6427f, 8.91156f, 15.9791f, 9.0501f, 16.375f, 9.0501f)
    close()
    moveTo(3.22498f, 20.7751f)
    verticalLineTo(3.2251f)
    horizontalLineTo(20.775f)
    verticalLineTo(20.7751f)
    horizontalLineTo(3.22498f)
    close()
  }
}.build()
