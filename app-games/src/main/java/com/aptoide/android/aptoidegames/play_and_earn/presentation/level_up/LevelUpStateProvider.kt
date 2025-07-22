package com.aptoide.android.aptoidegames.play_and_earn.presentation.level_up

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.aptoide.android.aptoidegames.play_and_earn.data.rememberAvailableUnits
import com.aptoide.android.aptoidegames.play_and_earn.data.rememberBalance
import com.aptoide.android.aptoidegames.play_and_earn.domain.UserStats
import com.aptoide.android.aptoidegames.play_and_earn.domain.levels

@Composable
fun rememberLevelUpState(): LevelUpUiState {
  val (units, _) = rememberAvailableUnits()
  val (balance, _) = rememberBalance()

  val levelUpUiState = remember(units) {
    LevelUpUiState.Idle(
      userStats = UserStats(
        currentAmount = 35000f,
        currentAmountCurrency = 35000f,
        level = 2,
        nextLevelAmount = 50000,
        nextLevelAmountCurrency = 50000
      ),
      availableUnits = units,
      balance = balance,
      levels = levels
    )
  }

  return levelUpUiState
}