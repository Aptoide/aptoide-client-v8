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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.design_system.AccentSmallButton
import com.aptoide.android.aptoidegames.play_and_earn.presentation.unit_exchange_flow.REDEEM_AMOUNT
import com.aptoide.android.aptoidegames.play_and_earn.presentation.unit_exchange_flow.formattedRedeemAmount
import com.aptoide.android.aptoidegames.play_and_earn.presentation.unit_exchange_flow.unitsToRedeem
import com.aptoide.android.aptoidegames.play_and_earn.presentation.units.rememberUnitsExchangeThreshold
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

/**
 * Level Up's way into the redeem flow. Below the redeem target it says how many units are
 * missing; from the target on it offers the redeem, with the amount the redeem screen shows.
 */
@Composable
fun UnitsExchangeCard(
  availableUnits: Long,
  onExchangeClick: () -> Unit,
  targetUnits: Long = rememberUnitsExchangeThreshold(),
) {
  val unitsToGo = unitsToRedeem(availableUnits, targetUnits)
  if (unitsToGo > 0L) {
    InsufficientUnitsCard(unitsToGo)
  } else {
    RedeemableUnitsCard(
      availableUnits = availableUnits,
      onExchangeClick = onExchangeClick
    )
  }
}

@Composable
private fun InsufficientUnitsCard(unitsToGo: Long) {
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
        text = stringResource(R.string.play_and_earn_exchange_units_to_go, unitsToGo),
        style = AGTypography.InputsS,
        color = Palette.White
      )
    }
  }
}

@Composable
private fun RedeemableUnitsCard(
  availableUnits: Long,
  onExchangeClick: () -> Unit
) {
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
        Image(
          painter = painterResource(R.drawable.coins_stack),
          contentDescription = null
        )
        Text(
          text = stringResource(R.string.play_and_earn_exchange_units_value, availableUnits),
          style = AGTypography.InputsS,
          color = Palette.White,
          textAlign = TextAlign.Center
        )
      }
      AccentSmallButton(
        title = stringResource(
          R.string.play_and_earn_exchange_redeem_button,
          formattedRedeemAmount(REDEEM_AMOUNT)
        ),
        onClick = onExchangeClick,
        modifier = Modifier.fillMaxWidth(),
      )
    }
  }
}
