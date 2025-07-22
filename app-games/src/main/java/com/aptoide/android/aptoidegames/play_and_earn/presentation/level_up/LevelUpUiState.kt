package com.aptoide.android.aptoidegames.play_and_earn.presentation.level_up

import com.aptoide.android.aptoidegames.play_and_earn.domain.Level
import com.aptoide.android.aptoidegames.play_and_earn.domain.UserStats

sealed class LevelUpUiState {
  data class Idle(
    val userStats: UserStats,
    val availableUnits: Int,
    val balance: Float,
    val levels: List<Level>
  ) : LevelUpUiState()

  object Loading : LevelUpUiState()
  object NoConnection : LevelUpUiState()
  object Error : LevelUpUiState()
}
