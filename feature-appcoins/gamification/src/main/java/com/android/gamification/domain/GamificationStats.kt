package com.android.gamification.domain

import com.android.gamification.data.model.GamificationStatus
import java.math.BigDecimal

data class GamificationStats(
  val level: Int = INVALID_LEVEL,
  val bonus: Double = INVALID_BONUS,
  val totalSpend: BigDecimal = BigDecimal.ZERO, //current amount
  val nextLevelAmount: BigDecimal? = BigDecimal.ZERO,
  val isActive: Boolean = false,
  val gamificationStatus: GamificationStatus
) {

  companion object {
    const val INVALID_LEVEL = -1
    const val INVALID_BONUS = -1.0
  }
}
