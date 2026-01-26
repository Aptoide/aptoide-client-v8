package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun CompanionChessIconPreview() {
  Image(
    imageVector = getCompanionChessIcon(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getCompanionChessIcon(): ImageVector = ImageVector.Builder(
  name = "companion_chess_icon",
  defaultWidth = 24.dp,
  defaultHeight = 24.dp,
  viewportWidth = 24f,
  viewportHeight = 24f,
  tintColor = Color.Unspecified,
).apply {
//<mask id="mask0_25021_12270" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="24" height="24">
//<rect width="24" height="24" fill="#D9D9D9"/>
//</mask>
//<g mask="url(#mask0_25021_12270)">
  path(
    fill = SolidColor(Color(0xFFC8ED4F)),
  ) {
    moveTo(3f, 22f)
    verticalLineTo(16f)
    horizontalLineTo(6.3f)
    lineTo(6.85f, 12f)
    horizontalLineTo(4f)
    verticalLineTo(10f)
    horizontalLineTo(20f)
    verticalLineTo(12f)
    horizontalLineTo(17.15f)
    lineTo(17.7f, 16f)
    horizontalLineTo(21f)
    verticalLineTo(22f)
    horizontalLineTo(3f)
    close()
    moveTo(6.45f, 8.5f)
    lineTo(5f, 2f)
    curveTo(5.55f, 2.41667f, 6.11667f, 2.80833f, 6.7f, 3.175f)
    curveTo(7.28333f, 3.54167f, 7.925f, 3.725f, 8.625f, 3.725f)
    curveTo(9.29167f, 3.725f, 9.90417f, 3.55417f, 10.4625f, 3.2125f)
    curveTo(11.0208f, 2.87083f, 11.5333f, 2.46667f, 12f, 2f)
    curveTo(12.4667f, 2.46667f, 12.9792f, 2.87083f, 13.5375f, 3.2125f)
    curveTo(14.0958f, 3.55417f, 14.7083f, 3.725f, 15.375f, 3.725f)
    curveTo(16.075f, 3.725f, 16.7167f, 3.54167f, 17.3f, 3.175f)
    curveTo(17.8833f, 2.80833f, 18.45f, 2.41667f, 19f, 2f)
    lineTo(17.575f, 8.5f)
    horizontalLineTo(6.45f)
    close()
  }
//</g>
}.build()
