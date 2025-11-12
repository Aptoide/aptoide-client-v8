package cm.aptoide.pt.play_and_earn.exchange.domain

import cm.aptoide.pt.play_and_earn.exchange.data.ExchangeRepository
import javax.inject.Inject

class ExchangeUnitsUseCase @Inject constructor(
  private val exchangeRepository: ExchangeRepository
) {

  suspend operator fun invoke(): Result<Unit> {
    return exchangeRepository.exchangeUnits()
  }
}
