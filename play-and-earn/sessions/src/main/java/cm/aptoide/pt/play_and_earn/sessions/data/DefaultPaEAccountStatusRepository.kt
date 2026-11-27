package cm.aptoide.pt.play_and_earn.sessions.data

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber

internal class DefaultPaEAccountStatusRepository(
  private val api: AccountStatusApi,
  private val dispatcher: CoroutineDispatcher,
) : PaEAccountStatusRepository {

  override suspend fun getStatus(): PaEAccountStatus = withContext(dispatcher) {
    try {
      when (api.getAccountStatus().restricted) {
        true -> PaEAccountStatus.RESTRICTED
        false -> PaEAccountStatus.NOT_RESTRICTED
        null -> PaEAccountStatus.UNKNOWN
      }
    } catch (e: CancellationException) {
      throw e
    } catch (e: Throwable) {
      // Network, 5xx, an expired sign-in or a body that is not the expected one: nothing is
      // known, and the screens keep whatever they knew. Not a crash.
      Timber.w(e, "Couldn't read the Play & Earn account status")
      PaEAccountStatus.UNKNOWN
    }
  }
}
