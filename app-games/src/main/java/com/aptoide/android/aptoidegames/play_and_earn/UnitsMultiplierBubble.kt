package com.aptoide.android.aptoidegames.play_and_earn

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getGiftIcon
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import kotlin.random.Random
import kotlin.random.nextInt

@Composable
fun SmallUnitsMultiplierBubble(
  availableUnits: Long,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier,
    contentAlignment = Alignment.Center
  ) {
    Image(
      imageVector = unitsMultiplierBackground(),
      contentDescription = null
    )
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Image(
        imageVector = getGiftIcon(),
        contentDescription = null,
        modifier = Modifier.size(14.dp, 16.dp)
      )
      Text(
        text = "x${availableUnits / 100}",
        style = AGTypography.InputsS,
        color = Palette.White
      )
    }
  }
}

@Composable
fun LargeUnitsMultiplierBubble(
  availableUnits: Long,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier,
    contentAlignment = Alignment.Center
  ) {
    Image(
      imageVector = unitsMultiplierBackground(),
      contentDescription = null,
      modifier = Modifier.size(97.dp, 54.dp)
    )
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Image(
        imageVector = getGiftIcon(),
        contentDescription = null,
        modifier = Modifier.size(34.dp, 38.dp)
      )
      Text(
        text = "x${availableUnits / 100}",
        style = AGTypography.Title,
        color = Palette.White
      )
    }
  }
}

@Preview
@Composable
private fun UnitsMultiplierBubblePreviews() {
  val availableUnits = Random.nextInt(100..400).toLong()

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    SmallUnitsMultiplierBubble(availableUnits = availableUnits)
    LargeUnitsMultiplierBubble(availableUnits = availableUnits)
  }
}

private fun unitsMultiplierBackground() = ImageVector.Builder(
  defaultWidth = 44.0.dp,
  defaultHeight = 24.0.dp,
  viewportWidth = 44.0f,
  viewportHeight = 24.0f
).apply {
  path(
    fill = SolidColor(Color(0xFF913DD8)),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(0.0f, 16.0f)
    curveTo(0.0f, 7.163f, 7.163f, 0.0f, 16.0f, 0.0f)
    horizontalLineTo(32.0f)
    curveTo(38.627f, 0.0f, 44.0f, 5.373f, 44.0f, 12.0f)
    verticalLineTo(12.0f)
    curveTo(44.0f, 18.627f, 38.627f, 24.0f, 32.0f, 24.0f)
    horizontalLineTo(0.0f)
    verticalLineTo(16.0f)
    close()
  }
}.build()
