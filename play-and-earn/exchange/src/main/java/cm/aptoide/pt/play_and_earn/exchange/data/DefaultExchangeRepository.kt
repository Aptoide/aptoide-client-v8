package cm.aptoide.pt.play_and_earn.exchange.data

import cm.aptoide.pt.play_and_earn.exchange.data.model.ExchangeRateJson
import cm.aptoide.pt.play_and_earn.exchange.data.model.ExchangeUnitsRequestJson
import cm.aptoide.pt.play_and_earn.exchange.domain.ExchangeRate
import cm.aptoide.pt.play_and_earn.exchange.domain.RedeemType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class DefaultExchangeRepository @Inject constructor(
  private val exchangeApi: ExchangeApi,
  private val dispatcher: CoroutineDispatcher
) : ExchangeRepository {

  private companion object {
    const val DEFAULT_COUNTRY_CODE = "PT"
    const val DEFAULT_EXCHANGE_RATE_COUNTRY_CODE = "US"
  }

  private val exchangeRateMutex = Mutex()
  private val cachedExchangeRates = mutableMapOf<String, ExchangeRate>()

  @Deprecated(
    "Use exchangeUnits(email, redeemType) instead.",
    ReplaceWith("exchangeUnits(email, redeemType)")
  )
  override suspend fun exchangeUnits(): Result<Unit> =
    withContext(dispatcher) {
      try {
        @Suppress("DEPRECATION")
        val response = exchangeApi.exchangeUnits(DEFAULT_COUNTRY_CODE)

        if (response.isSuccess()) {
          Result.success(Unit)
        } else {
          val errorMessage = response.detail?.message ?: ""
          Result.failure(ExchangeException(errorMessage))
        }
      } catch (e: Throwable) {
        e.printStackTrace()
        Result.failure(e)
      }
    }

  override suspend fun exchangeUnits(email: String, redeemType: RedeemType): Result<Unit> =
    withContext(dispatcher) {
      try {
        val response = exchangeApi.exchangeUnitsV2(
          ExchangeUnitsRequestJson(email = email, redeemType = redeemType)
        )

        if (response.isSuccess()) {
          Result.success(Unit)
        } else {
          val errorMessage = response.detail?.message ?: ""
          Result.failure(ExchangeException(errorMessage))
        }
      } catch (e: Throwable) {
        e.printStackTrace()
        Result.failure(e)
      }
    }

  override suspend fun getExchangeRate(): Result<ExchangeRate> =
    withContext(dispatcher) {
      val countryCode = DEFAULT_EXCHANGE_RATE_COUNTRY_CODE

      cachedExchangeRates[countryCode]?.let { return@withContext Result.success(it) }

      exchangeRateMutex.withLock {
        // Re-check inside the lock in case another caller fetched it while we waited.
        cachedExchangeRates[countryCode]?.let { return@withLock Result.success(it) }

        try {
          val rate = exchangeApi.getExchangeRate(countryCode).toDomainModel()
          cachedExchangeRates[countryCode] = rate
          Result.success(rate)
        } catch (e: Throwable) {
          e.printStackTrace()
          Result.failure(e)
        }
      }
    }
}

private fun ExchangeRateJson.toDomainModel() = ExchangeRate(
  countryCode = countryCode.orEmpty(),
  rate = rate ?: 0.0,
  creditsAmount = creditsAmount ?: 0
)

class ExchangeException(message: String) : Throwable(message)
