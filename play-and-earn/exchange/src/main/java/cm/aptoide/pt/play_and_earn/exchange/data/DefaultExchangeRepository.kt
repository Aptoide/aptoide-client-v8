package cm.aptoide.pt.play_and_earn.exchange.data

import cm.aptoide.pt.play_and_earn.exchange.data.model.ExchangeUnitsRequestJson
import cm.aptoide.pt.play_and_earn.exchange.domain.RedeemType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class DefaultExchangeRepository @Inject constructor(
  private val exchangeApi: ExchangeApi,
  private val dispatcher: CoroutineDispatcher
) : ExchangeRepository {

  private companion object {
    const val DEFAULT_COUNTRY_CODE = "PT"
  }

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
}

class ExchangeException(message: String) : Throwable(message)
