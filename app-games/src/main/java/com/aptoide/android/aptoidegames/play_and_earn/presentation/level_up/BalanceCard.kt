package com.aptoide.android.aptoidegames.play_and_earn.presentation.level_up

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getPaESmallLogo
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

@Composable
fun BalanceCard(
  balance: BigDecimal,
  currency: String,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .requiredHeight(158.dp)
      .background(Palette.GreyDark),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Text(
        text = "Aptoide Balance", //TODO: hardcoded string
        style = AGTypography.InputsM,
        color = Palette.White,
      )
      Image(
        imageVector = getPaESmallLogo(color1 = Palette.White, color2 = Palette.GreyDark),
        contentDescription = null,
        modifier = Modifier.size(58.dp)
      )
      Text(
        text = formatCurrency(balance, currency),
        style = AGTypography.InputsL,
        color = Palette.White,
      )
    }
  }
}

/**
 * Formats a currency amount to ensure correct positioning of its symbol, based on the locale.
 */
private fun formatCurrency(balance: BigDecimal, currencyCode: String): String {
  return try {
    val currency = Currency.getInstance(currencyCode)

    val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
    formatter.currency = currency
    formatter.roundingMode = RoundingMode.DOWN

    formatter.format(balance)
  } catch (_: Throwable) {
    "${balance.setScale(2, RoundingMode.DOWN)} $currencyCode"
  }
}
