package com.aptoide.android.aptoidegames.play_and_earn.presentation.unit_exchange_flow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.PaERewardType
import com.aptoide.android.aptoidegames.theme.Palette

val PaERewardType.exchangeDisplayName: String
  get() = when (this) {
    PaERewardType.ROBUX -> "Robux"
    PaERewardType.DIAMONDS -> "Free Fire Diamonds"
  }

private val ExchangeBackgroundGradient = Brush.verticalGradient(
  colors = listOf(
    Color(0x99913DD8),
    Palette.Black,
  )
)

@Composable
fun ExchangeFlowBackground(
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Palette.Black)
      .background(ExchangeBackgroundGradient),
  ) {
    content()
  }
}
