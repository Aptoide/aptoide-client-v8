package cm.aptoide.pt.play_and_earn.exchange.domain

import java.math.BigDecimal
import java.math.RoundingMode

data class ExchangeRate(
  val countryCode: String,
  // How much money [creditsAmount] units are worth.
  val rate: Double,
  // The amount of units the [rate] refers to (e.g. 100 units -> [rate] money).
  val creditsAmount: Int
) {
  // Returns how much money the given number of [units] is worth, according to this rate.
  fun moneyValueOf(units: Long): BigDecimal {
    if (creditsAmount <= 0) return BigDecimal.ZERO
    return BigDecimal.valueOf(units)
      .multiply(BigDecimal.valueOf(rate))
      .divide(BigDecimal.valueOf(creditsAmount.toLong()), 2, RoundingMode.HALF_UP)
  }
}
