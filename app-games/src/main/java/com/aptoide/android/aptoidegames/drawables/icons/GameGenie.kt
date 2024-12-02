package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.Palette

@Preview
@Composable
fun TestGameGenie() {
  Image(
    imageVector = getGameGenie(Palette.Primary),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getGameGenie(color: Color): ImageVector = ImageVector.Builder(
  name = "gamegenie",
  defaultWidth = 24.dp,
  defaultHeight = 24.dp,
  viewportWidth = 24f,
  viewportHeight = 24f,
).apply {
  path(
    pathFillType = PathFillType.EvenOdd,
    fill = SolidColor(color),
  ) {
    moveTo(5.73913f, 22f)
    curveTo(4.94203f, 22f, 4.26449f, 21.721f, 3.70652f, 21.163f)
    curveTo(3.14855f, 20.6051f, 2.86957f, 19.9275f, 2.86957f, 19.1304f)
    curveTo(2.86957f, 18.3333f, 3.14855f, 17.6558f, 3.70652f, 17.0978f)
    curveTo(4.26449f, 16.5399f, 4.94203f, 16.2609f, 5.73913f, 16.2609f)
    curveTo(5.96232f, 16.2609f, 6.16957f, 16.2848f, 6.36087f, 16.3326f)
    curveTo(6.55217f, 16.3804f, 6.73551f, 16.4442f, 6.91087f, 16.5239f)
    lineTo(8.27391f, 14.8261f)
    curveTo(7.82754f, 14.3319f, 7.51667f, 13.7739f, 7.3413f, 13.1522f)
    curveTo(7.16594f, 12.5304f, 7.12609f, 11.9087f, 7.22174f, 11.287f)
    lineTo(5.28478f, 10.6413f)
    curveTo(5.01377f, 11.0399f, 4.67101f, 11.3587f, 4.25652f, 11.5978f)
    curveTo(3.84203f, 11.837f, 3.37971f, 11.9565f, 2.86957f, 11.9565f)
    curveTo(2.07246f, 11.9565f, 1.39493f, 11.6775f, 0.836957f, 11.1196f)
    curveTo(0.278985f, 10.5616f, 0f, 9.88406f, 0f, 9.08696f)
    curveTo(0f, 8.28986f, 0.278985f, 7.61232f, 0.836957f, 7.05435f)
    curveTo(1.39493f, 6.49638f, 2.07246f, 6.21739f, 2.86957f, 6.21739f)
    curveTo(3.66667f, 6.21739f, 4.3442f, 6.49638f, 4.90217f, 7.05435f)
    curveTo(5.46014f, 7.61232f, 5.73913f, 8.28986f, 5.73913f, 9.08696f)
    verticalLineTo(9.27826f)
    lineTo(7.67609f, 9.94783f)
    curveTo(7.99493f, 9.37391f, 8.42138f, 8.88768f, 8.95543f, 8.48913f)
    curveTo(9.48949f, 8.09058f, 10.0913f, 7.83551f, 10.7609f, 7.72391f)
    verticalLineTo(5.64348f)
    curveTo(10.1391f, 5.46812f, 9.625f, 5.12935f, 9.21848f, 4.62717f)
    curveTo(8.81196f, 4.125f, 8.6087f, 3.53913f, 8.6087f, 2.86957f)
    curveTo(8.6087f, 2.07246f, 8.88768f, 1.39493f, 9.44565f, 0.836957f)
    curveTo(10.0036f, 0.278985f, 10.6812f, 0f, 11.4783f, 0f)
    curveTo(12.2754f, 0f, 12.9529f, 0.278985f, 13.5109f, 0.836957f)
    curveTo(14.0688f, 1.39493f, 14.3478f, 2.07246f, 14.3478f, 2.86957f)
    curveTo(14.3478f, 3.53913f, 14.1406f, 4.125f, 13.7261f, 4.62717f)
    curveTo(13.3116f, 5.12935f, 12.8014f, 5.46812f, 12.1957f, 5.64348f)
    verticalLineTo(7.72391f)
    curveTo(12.8652f, 7.83551f, 13.467f, 8.09058f, 14.0011f, 8.48913f)
    curveTo(14.5351f, 8.88768f, 14.9616f, 9.37391f, 15.2804f, 9.94783f)
    lineTo(17.2174f, 9.27826f)
    verticalLineTo(9.08696f)
    curveTo(17.2174f, 8.28986f, 17.4964f, 7.61232f, 18.0543f, 7.05435f)
    curveTo(18.6123f, 6.49638f, 19.2899f, 6.21739f, 20.087f, 6.21739f)
    curveTo(20.8841f, 6.21739f, 21.5616f, 6.49638f, 22.1196f, 7.05435f)
    curveTo(22.6775f, 7.61232f, 22.9565f, 8.28986f, 22.9565f, 9.08696f)
    curveTo(22.9565f, 9.88406f, 22.6775f, 10.5616f, 22.1196f, 11.1196f)
    curveTo(21.5616f, 11.6775f, 20.8841f, 11.9565f, 20.087f, 11.9565f)
    curveTo(19.5768f, 11.9565f, 19.1105f, 11.837f, 18.688f, 11.5978f)
    curveTo(18.2656f, 11.3587f, 17.9268f, 11.0399f, 17.6717f, 10.6413f)
    lineTo(15.7348f, 11.287f)
    curveTo(15.8304f, 11.9087f, 15.7906f, 12.5264f, 15.6152f, 13.1402f)
    curveTo(15.4399f, 13.754f, 15.129f, 14.3159f, 14.6826f, 14.8261f)
    lineTo(16.0457f, 16.5f)
    curveTo(16.221f, 16.4203f, 16.4043f, 16.3605f, 16.5957f, 16.3207f)
    curveTo(16.787f, 16.2808f, 16.9942f, 16.2609f, 17.2174f, 16.2609f)
    curveTo(18.0145f, 16.2609f, 18.692f, 16.5399f, 19.25f, 17.0978f)
    curveTo(19.808f, 17.6558f, 20.087f, 18.3333f, 20.087f, 19.1304f)
    curveTo(20.087f, 19.9275f, 19.808f, 20.6051f, 19.25f, 21.163f)
    curveTo(18.692f, 21.721f, 18.0145f, 22f, 17.2174f, 22f)
    curveTo(16.4203f, 22f, 15.7428f, 21.721f, 15.1848f, 21.163f)
    curveTo(14.6268f, 20.6051f, 14.3478f, 19.9275f, 14.3478f, 19.1304f)
    curveTo(14.3478f, 18.8116f, 14.3996f, 18.5047f, 14.5033f, 18.2098f)
    curveTo(14.6069f, 17.9149f, 14.7464f, 17.6478f, 14.9217f, 17.4087f)
    lineTo(13.5587f, 15.7109f)
    curveTo(12.9051f, 16.0775f, 12.2076f, 16.2609f, 11.4663f, 16.2609f)
    curveTo(10.725f, 16.2609f, 10.0275f, 16.0775f, 9.37391f, 15.7109f)
    lineTo(8.03478f, 17.4087f)
    curveTo(8.21015f, 17.6478f, 8.34964f, 17.9149f, 8.45326f, 18.2098f)
    curveTo(8.55688f, 18.5047f, 8.6087f, 18.8116f, 8.6087f, 19.1304f)
    curveTo(8.6087f, 19.9275f, 8.32971f, 20.6051f, 7.77174f, 21.163f)
    curveTo(7.21377f, 21.721f, 6.53623f, 22f, 5.73913f, 22f)
    close()
    moveTo(12.7526f, 10f)
    horizontalLineTo(13.5336f)
    verticalLineTo(14.0199f)
    horizontalLineTo(12.7526f)
    verticalLineTo(10f)
    close()
    moveTo(10.8489f, 10f)
    horizontalLineTo(10.1368f)
    lineTo(8.66669f, 14.0199f)
    horizontalLineTo(9.45919f)
    lineTo(9.78652f, 13.1298f)
    horizontalLineTo(11.1992f)
    lineTo(11.5266f, 14.0199f)
    horizontalLineTo(12.3191f)
    lineTo(10.8489f, 10f)
    close()
    moveTo(10.4929f, 10.959f)
    lineTo(11.027f, 12.4866f)
    horizontalLineTo(9.96455f)
    lineTo(10.4814f, 10.959f)
    horizontalLineTo(10.4929f)
    close()
  }
}.build()
