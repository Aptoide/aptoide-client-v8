package com.aptoide.android.aptoidegames.play_and_earn.presentation.level_up

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.design_system.AccentSmallButton
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getGiftIcon
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import java.util.Locale

@Composable
fun UnitsExchangeCard(
  availableUnits: Long,
  onExchangeClick: () -> Unit
) {
  if (availableUnits < 100L) {
    InsufficientUnitsCard(availableUnits)
  } else {
    ExchangeableUnitsCard(
      availableUnits = availableUnits,
      onExchangeClick = onExchangeClick
    )
  }
}

@Composable
private fun InsufficientUnitsCard(availableUnits: Long) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
      .background(Palette.Secondary.copy(alpha = 0.3f))
      .border(2.dp, Palette.Secondary)
  ) {
    Row(
      modifier = Modifier.padding(all = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Image(
        painter = painterResource(R.drawable.coins_stack),
        contentDescription = null
      )
      Text(
        text = "Collect ${100 - availableUnits} more units to earn \$2.00 in Aptoide Balance.",
        style = AGTypography.InputsS,
        color = Palette.White
      )
    }
  }
}

@Composable
private fun ExchangeableUnitsCard(
  availableUnits: Long,
  onExchangeClick: () -> Unit
) {
  val exchangeableUnits = (availableUnits / 100) * 100
  val currencyValue = (exchangeableUnits * 2f / 100f).let {
    String.format(Locale.getDefault(), "%.2f", it)
  }

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
      .background(Palette.Secondary.copy(alpha = 0.3f))
      .border(2.dp, Palette.Secondary)
  ) {
    Column(
      modifier = Modifier.padding(all = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Box {
          Image(
            painter = painterResource(R.drawable.coins_stack),
            contentDescription = null
          )
          Box(
            modifier = Modifier.padding(start = 38.dp),
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
        Text(
          text = "Exchange $exchangeableUnits units for \$${currencyValue} in your Aptoide Balance.",
          style = AGTypography.InputsS,
          color = Palette.White,
          textAlign = TextAlign.Center
        )
      }
      AccentSmallButton(
        title = "Exchange Now", //TODO: hardcoded string
        onClick = onExchangeClick,
        modifier = Modifier.fillMaxWidth(),
      )
    }
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
