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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.design_system.AccentSmallButton
import com.aptoide.android.aptoidegames.play_and_earn.SmallUnitsMultiplierBubble
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
          SmallUnitsMultiplierBubble(
            availableUnits = availableUnits,
            Modifier.padding(start = 38.dp)
          )
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
