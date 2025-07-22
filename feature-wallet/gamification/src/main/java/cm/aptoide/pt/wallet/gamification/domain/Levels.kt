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

val previewLevels = Levels(
  levelList = listOf(
    Level(
      level = 0,
      amount = BigDecimal.ZERO,
      bonus = 5.0,
    ),
    Level(
      level = 1,
      amount = BigDecimal.valueOf(2000),
      bonus = 6.0,
    ),
    Level(
      level = 2,
      amount = BigDecimal.valueOf(10000),
      bonus = 7.0,
    ),
    Level(
      level = 3,
      amount = BigDecimal.valueOf(50000),
      bonus = 8.5,
    ),
    Level(
      level = 4,
      amount = BigDecimal.valueOf(100000),
      bonus = 10.0,
    ),
    Level(
      level = 5,
      amount = BigDecimal.valueOf(250000),
      bonus = 12.0,
    ),
    Level(
      level = 6,
      amount = BigDecimal.valueOf(750000),
      bonus = 14.0,
    ),
    Level(
      level = 7,
      amount = BigDecimal.valueOf(1500000),
      bonus = 16.0,
    ),
    Level(
      level = 8,
      amount = BigDecimal.valueOf(2500000),
      bonus = 18.0,
    ),
    Level(
      level = 9,
      amount = BigDecimal.valueOf(5000000),
      bonus = 20.0,
    )
  )
)
