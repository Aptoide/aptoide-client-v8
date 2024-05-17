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
fun TestCategories() {
  Image(
    imageVector = getCategories(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getCategories(): ImageVector = ImageVector.Builder(
  name = "Categories",
  defaultWidth = 24.dp,
  defaultHeight = 24.dp,
  viewportWidth = 24f,
  viewportHeight = 24f,
).apply {
  path(
    fill = SolidColor(greyLight),
  ) {
    moveTo(6.77515f, 11.0001f)
    lineTo(12.0751f, 2.3501f)
    lineTo(17.3501f, 11.0001f)
    horizontalLineTo(6.77515f)
    close()
    moveTo(17.5001f, 21.8501f)
    curveTo(16.2835f, 21.8501f, 15.2501f, 21.4251f, 14.4001f, 20.5751f)
    curveTo(13.5501f, 19.7251f, 13.1251f, 18.6918f, 13.1251f, 17.4751f)
    curveTo(13.1251f, 16.2668f, 13.5501f, 15.2355f, 14.4001f, 14.3813f)
    curveTo(15.2501f, 13.5272f, 16.2835f, 13.1001f, 17.5001f, 13.1001f)
    curveTo(18.7085f, 13.1001f, 19.7397f, 13.5272f, 20.5939f, 14.3813f)
    curveTo(21.4481f, 15.2355f, 21.8751f, 16.2668f, 21.8751f, 17.4751f)
    curveTo(21.8751f, 18.6918f, 21.4481f, 19.7251f, 20.5939f, 20.5751f)
    curveTo(19.7397f, 21.4251f, 18.7085f, 21.8501f, 17.5001f, 21.8501f)
    close()
    moveTo(3.27515f, 21.3501f)
    verticalLineTo(13.6251f)
    horizontalLineTo(11.0001f)
    verticalLineTo(21.3501f)
    horizontalLineTo(3.27515f)
    close()
  }
}.build()
