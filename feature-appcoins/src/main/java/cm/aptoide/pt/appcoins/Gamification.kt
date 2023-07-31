package cm.aptoide.pt.appcoins

import androidx.annotation.Keep

@Keep
data class Level(
  val amount: Int,
  val bonus: Float,
  val level: Int,
)

data class Gamification(private val levels: List<Level>) {
  val bonusPercentage: Float = levels.last().bonus
}
