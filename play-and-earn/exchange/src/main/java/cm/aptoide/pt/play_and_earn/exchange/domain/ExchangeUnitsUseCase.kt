package cm.aptoide.pt.play_and_earn.exchange.domain

import cm.aptoide.pt.play_and_earn.exchange.data.ExchangeRepository
import javax.inject.Inject

class ExchangeUnitsUseCase @Inject constructor(
  private val exchangeRepository: ExchangeRepository
) {

  @Deprecated(
    "Use invoke(email, redeemType, requestId) instead.",
    ReplaceWith("invoke(email, redeemType, requestId)")
  )
  suspend operator fun invoke(): Result<Unit> {
    @Suppress("DEPRECATION")
    return exchangeRepository.exchangeUnits()
  }

  suspend operator fun invoke(
    email: String,
    redeemType: RedeemType,
    requestId: String,
  ): Result<Unit> {
    return exchangeRepository.exchangeUnits(email, redeemType, requestId)
  }
}
