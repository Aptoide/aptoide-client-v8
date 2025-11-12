package cm.aptoide.pt.play_and_earn.exchange.data

interface ExchangeRepository {

  suspend fun exchangeUnits(): Result<Unit>
}
