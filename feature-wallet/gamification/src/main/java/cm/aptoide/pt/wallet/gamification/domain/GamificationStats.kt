package cm.aptoide.pt.wallet.gamification.domain

import java.math.BigDecimal

data class GamificationStats(
  val level: Int = INITIAL_LEVEL,
  val bonus: Double = DEFAULT_BONUS,
  val totalSpend: BigDecimal = BigDecimal.ZERO, //current amount
  val nextLevelAmount: BigDecimal? = BigDecimal.ZERO,
  val isActive: Boolean = false,
  val gamificationStatus: GamificationLevelStatus
) {

  companion object {
    const val INITIAL_LEVEL = 0
    const val DEFAULT_BONUS = 5.0
  }
}

enum class GamificationLevelStatus {
  NONE,
  STANDARD,
  APPROACHING_NEXT_LEVEL,
  APPROACHING_VIP,
  VIP,
  APPROACHING_VIP_MAX,
  VIP_MAX;
}
