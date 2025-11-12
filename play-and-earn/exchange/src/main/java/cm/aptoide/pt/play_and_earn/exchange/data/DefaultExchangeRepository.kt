package cm.aptoide.pt.play_and_earn.exchange.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

internal class DefaultExchangeRepository @Inject constructor(
  private val exchangeApi: ExchangeApi,
  private val dispatcher: CoroutineDispatcher
) : ExchangeRepository {

  override suspend fun exchangeUnits(): Result<Unit> =
    withContext(dispatcher) {
      try {
        val countryCode = Locale.getDefault().country

        val response = exchangeApi.exchangeUnits(countryCode)

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
