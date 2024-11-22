package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun TestGameGenieIcon() {
  Image(
    imageVector = getGameGenieIcon(Color.Gray),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getGameGenieIcon(
  color: Color
): ImageVector = ImageVector.Builder(
  name = "gamegenie_icon",
  defaultHeight = 24.dp,
  defaultWidth = 24.dp,
  viewportWidth = 960f,
  viewportHeight = 960f,
).apply {
  group {
    path(
      fill = SolidColor(color),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(323f, 800f)
      quadToRelative(-11f, 0f, -20.5f, -5.5f)
      reflectiveQuadTo(288f, 779f)
      lineToRelative(-78f, -139f)
      horizontalLineToRelative(58f)
      lineToRelative(40f, 80f)
      horizontalLineToRelative(92f)
      verticalLineToRelative(-40f)
      horizontalLineToRelative(-68f)
      lineToRelative(-40f, -80f)
      lineTo(188f, 600f)
      lineToRelative(-57f, -100f)
      quadToRelative(-2f, -5f, -3.5f, -10f)
      reflectiveQuadToRelative(-1.5f, -10f)
      quadToRelative(0f, -4f, 5f, -20f)
      lineToRelative(57f, -100f)
      horizontalLineToRelative(104f)
      lineToRelative(40f, -80f)
      horizontalLineToRelative(68f)
      verticalLineToRelative(-40f)
      horizontalLineToRelative(-92f)
      lineToRelative(-40f, 80f)
      horizontalLineToRelative(-58f)
      lineToRelative(78f, -139f)
      quadToRelative(5f, -10f, 14.5f, -15.5f)
      reflectiveQuadTo(323f, 160f)
      horizontalLineToRelative(97f)
      quadToRelative(17f, 0f, 28.5f, 11.5f)
      reflectiveQuadTo(460f, 200f)
      verticalLineToRelative(160f)
      horizontalLineToRelative(-60f)
      lineToRelative(-40f, 40f)
      horizontalLineToRelative(100f)
      verticalLineToRelative(120f)
      horizontalLineToRelative(-88f)
      lineToRelative(-40f, -80f)
      horizontalLineToRelative(-92f)
      lineToRelative(-40f, 40f)
      horizontalLineToRelative(108f)
      lineToRelative(40f, 80f)
      horizontalLineToRelative(112f)
      verticalLineToRelative(200f)
      quadToRelative(0f, 17f, -11.5f, 28.5f)
      reflectiveQuadTo(420f, 800f)
      horizontalLineToRelative(-97f)
      close()
    }
    path(
      fill = SolidColor(color),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = EvenOdd
    ) {
      moveTo(560f, 800f)
      quadToRelative(-33f, 0f, -56.5f, -23.5f)
      reflectiveQuadTo(480f, 720f)
      quadToRelative(0f, -23f, 11f, -40.5f)
      reflectiveQuadToRelative(29f, -28.5f)
      verticalLineToRelative(-342f)
      quadToRelative(-18f, -11f, -29f, -28.5f)
      reflectiveQuadTo(480f, 240f)
      quadToRelative(0f, -33f, 23.5f, -56.5f)
      reflectiveQuadTo(560f, 160f)
      quadToRelative(33f, 0f, 56.5f, 23.5f)
      reflectiveQuadTo(640f, 240f)
      quadToRelative(0f, 23f, -11f, 40.5f)
      reflectiveQuadTo(600f, 309f)
      verticalLineToRelative(101f)
      lineToRelative(80f, -48f)
      quadToRelative(0f, -34f, 23.5f, -58f)
      reflectiveQuadToRelative(56.5f, -24f)
      quadToRelative(33f, 0f, 56.5f, 23.5f)
      reflectiveQuadTo(840f, 360f)
      quadToRelative(0f, 33f, -23.5f, 56.5f)
      reflectiveQuadTo(760f, 440f)
      quadToRelative(-11f, 0f, -20.5f, -2.5f)
      reflectiveQuadTo(721f, 430f)
      lineToRelative(-91f, 55f)
      lineToRelative(101f, 80f)
      quadToRelative(7f, -3f, 14f, -4f)
      reflectiveQuadToRelative(15f, -1f)
      quadToRelative(33f, 0f, 56.5f, 23.5f)
      reflectiveQuadTo(840f, 640f)
      quadToRelative(0f, 33f, -23.5f, 56.5f)
      reflectiveQuadTo(760f, 720f)
      quadToRelative(-37f, 0f, -60.5f, -28f)
      reflectiveQuadTo(681f, 628f)
      lineToRelative(-81f, -65f)
      verticalLineToRelative(89f)
      quadToRelative(18f, 11f, 28.5f, 28.5f)
      reflectiveQuadTo(639f, 720f)
      quadToRelative(0f, 33f, -23f, 56.5f)
      reflectiveQuadTo(560f, 800f)
      close()
    }
  }
}.build()
