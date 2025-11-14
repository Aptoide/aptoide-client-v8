package cm.aptoide.pt.play_and_earn.exchange.data

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

  override suspend fun exchangeUnits(): Result<Unit> =
    withContext(dispatcher) {
      try {
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
}

class ExchangeException(message: String) : Throwable(message)
