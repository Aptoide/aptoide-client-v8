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
import com.aptoide.android.aptoidegames.theme.agWhite

@Preview
@Composable
fun TestSearch() {
  Image(
    imageVector = getSearch(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getSearch(): ImageVector = ImageVector.Builder(
  name = "search",
  defaultWidth = 24.dp,
  defaultHeight = 24.dp,
  viewportWidth = 24f,
  viewportHeight = 24f,
).apply {
  path(
    fill = SolidColor(agWhite),
  ) {
    moveTo(19.6f, 21f)
    lineTo(13.3f, 14.7f)
    curveTo(12.8f, 15.1f, 12.225f, 15.4167f, 11.575f, 15.65f)
    curveTo(10.925f, 15.8833f, 10.2333f, 16f, 9.5f, 16f)
    curveTo(7.68333f, 16f, 6.14583f, 15.3708f, 4.8875f, 14.1125f)
    curveTo(3.62917f, 12.8542f, 3f, 11.3167f, 3f, 9.5f)
    curveTo(3f, 7.68333f, 3.62917f, 6.14583f, 4.8875f, 4.8875f)
    curveTo(6.14583f, 3.62917f, 7.68333f, 3f, 9.5f, 3f)
    curveTo(11.3167f, 3f, 12.8542f, 3.62917f, 14.1125f, 4.8875f)
    curveTo(15.3708f, 6.14583f, 16f, 7.68333f, 16f, 9.5f)
    curveTo(16f, 10.2333f, 15.8833f, 10.925f, 15.65f, 11.575f)
    curveTo(15.4167f, 12.225f, 15.1f, 12.8f, 14.7f, 13.3f)
    lineTo(21f, 19.6f)
    lineTo(19.6f, 21f)
    close()
    moveTo(9.5f, 14f)
    curveTo(10.75f, 14f, 11.8125f, 13.5625f, 12.6875f, 12.6875f)
    curveTo(13.5625f, 11.8125f, 14f, 10.75f, 14f, 9.5f)
    curveTo(14f, 8.25f, 13.5625f, 7.1875f, 12.6875f, 6.3125f)
    curveTo(11.8125f, 5.4375f, 10.75f, 5f, 9.5f, 5f)
    curveTo(8.25f, 5f, 7.1875f, 5.4375f, 6.3125f, 6.3125f)
    curveTo(5.4375f, 7.1875f, 5f, 8.25f, 5f, 9.5f)
    curveTo(5f, 10.75f, 5.4375f, 11.8125f, 6.3125f, 12.6875f)
    curveTo(7.1875f, 13.5625f, 8.25f, 14f, 9.5f, 14f)
    close()
  }
}.build()
