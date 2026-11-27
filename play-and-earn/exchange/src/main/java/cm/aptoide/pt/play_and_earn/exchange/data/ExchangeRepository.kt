package cm.aptoide.pt.play_and_earn.exchange.data

import cm.aptoide.pt.play_and_earn.exchange.domain.ExchangeRate
import cm.aptoide.pt.play_and_earn.exchange.domain.RedeemType

interface ExchangeRepository {

  /**
   * @param requestId the redeem's own id: sending it again with the same email and type replays
   * the first answer instead of redeeming twice.
   */
  suspend fun exchangeUnits(
    email: String,
    redeemType: RedeemType,
    requestId: String,
  ): Result<Unit>

  suspend fun getExchangeRate(): Result<ExchangeRate>
}
