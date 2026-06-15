package cm.aptoide.pt.play_and_earn.exchange.data

import cm.aptoide.pt.play_and_earn.exchange.domain.ExchangeRate
import cm.aptoide.pt.play_and_earn.exchange.domain.RedeemType

interface ExchangeRepository {

  @Deprecated(
    "Use exchangeUnits(email, redeemType) instead.",
    ReplaceWith("exchangeUnits(email, redeemType)")
  )
  suspend fun exchangeUnits(): Result<Unit>

  suspend fun exchangeUnits(email: String, redeemType: RedeemType): Result<Unit>

  suspend fun getExchangeRate(): Result<ExchangeRate>
}
