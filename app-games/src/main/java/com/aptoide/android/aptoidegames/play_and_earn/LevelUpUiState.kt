package com.aptoide.android.aptoidegames.play_and_earn

import com.aptoide.android.aptoidegames.play_and_earn.domain.Level

sealed class LevelUpUiState {
  data class Idle(
    val currentLevel: Level,
    val availableUnits: Int,
    val balance: Float,
    val levels: List<Level>
  ) : LevelUpUiState()

  object Loading : LevelUpUiState()
  object NoConnection : LevelUpUiState()
  object Error : LevelUpUiState()
}
