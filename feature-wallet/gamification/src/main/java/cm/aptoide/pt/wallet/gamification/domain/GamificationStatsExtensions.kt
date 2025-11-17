package cm.aptoide.pt.wallet.gamification.domain

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Returns current level progress from 0 to 1.
 */
fun GamificationStats.getCurrentLevelProgressRatio(): Float {
  return if (nextLevelAmount != null) {
    val levelRange = nextLevelAmount.max(BigDecimal.ONE)
    totalSpend.divide(levelRange, 2, RoundingMode.DOWN).toFloat()
  } else {
    1f
  }
}
