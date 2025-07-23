package com.aptoide.android.aptoidegames.play_and_earn

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.aptoide.android.aptoidegames.play_and_earn.domain.Level
import com.aptoide.android.aptoidegames.play_and_earn.domain.LevelCheckpointStatus
import com.aptoide.android.aptoidegames.play_and_earn.domain.Tier
import com.aptoide.android.aptoidegames.play_and_earn.domain.levels
import kotlin.random.Random
import kotlin.random.nextInt

@Composable
fun rememberLevelUpState(): LevelUpUiState {
  val levelUpUiState = remember {
    LevelUpUiState.Idle(
      currentLevel = Level(
        xp = 1500,
        level = 2,
        tier = Tier.BRONZE_PLUS,
        bonus = 0f,
        status = LevelCheckpointStatus.CURRENT
      ),
      availableUnits = 245,
      balance = Random.nextInt(2..10).toFloat(),
      levels = levels
    )
  }

  return levelUpUiState
}