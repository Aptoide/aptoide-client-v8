package cm.aptoide.pt.play_and_earn.sessions.data

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber

internal class DefaultPaEAccountStatusRepository(
  private val api: AccountStatusApi,
  private val dispatcher: CoroutineDispatcher,
) : PaEAccountStatusRepository {

  override suspend fun isRestricted(): Boolean = withContext(dispatcher) {
    try {
      api.getAccountStatus().restricted == true
    } catch (e: CancellationException) {
      throw e
    } catch (e: Throwable) {
      // Network, 5xx, an expired sign-in or a body that is not the expected one: the account is
      // treated as not restricted and the screens behave as without this call. Not a crash.
      Timber.w(e, "Couldn't read the Play & Earn account status")
      false
    }
  }
}
