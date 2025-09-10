package cm.aptoide.pt.wallet.gamification.domain

import java.math.BigDecimal
import java.util.Date

data class Levels(
  val levelList: List<Level> = emptyList(),
  val updateDate: Date? = null,
)

data class Level(
  val amount: BigDecimal,
  val bonus: Double,
  val level: Int
)
