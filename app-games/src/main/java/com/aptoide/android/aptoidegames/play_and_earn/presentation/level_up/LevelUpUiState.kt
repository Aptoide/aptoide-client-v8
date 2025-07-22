package com.aptoide.android.aptoidegames.play_and_earn.presentation.level_up

import cm.aptoide.pt.appcoins.domain.WalletInfo
import cm.aptoide.pt.wallet.gamification.domain.GamificationStats
import cm.aptoide.pt.wallet.gamification.domain.Levels

sealed class LevelUpUiState {
  data class Idle(
    val walletInfo: WalletInfo,
    val gamificationStats: GamificationStats,
    val levels: Levels,
  ) : LevelUpUiState()

  object Loading : LevelUpUiState()
  object NoConnection : LevelUpUiState()
  object Error : LevelUpUiState()
}
