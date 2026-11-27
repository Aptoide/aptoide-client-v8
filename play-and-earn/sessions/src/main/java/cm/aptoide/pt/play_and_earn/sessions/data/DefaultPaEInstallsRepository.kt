package cm.aptoide.pt.play_and_earn.sessions.data

import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalException
import cm.aptoide.pt.aptoide_network.play_and_earn.reportUnlessPaERefusal
import cm.aptoide.pt.exception_handler.ExceptionHandler
import cm.aptoide.pt.play_and_earn.sessions.data.model.InstallClickRequestJson
import cm.aptoide.pt.play_and_earn.sessions.data.model.InstallReportRequestJson
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.IOException

internal class DefaultPaEInstallsRepository(
  private val api: InstallsApi,
  private val dispatcher: CoroutineDispatcher,
  private val exceptionHandler: ExceptionHandler,
) : PaEInstallsRepository {

  override suspend fun click(packageName: String, clickId: String): Result<PaEInstallAnswer> =
    call {
      val answer = api.click(InstallClickRequestJson(packageName = packageName, clickId = clickId))
      // No field: recorded, and nothing known against it.
      PaEInstallAnswer(eligible = answer.eligible ?: true)
    }

  override suspend fun report(
    packageName: String,
    clickId: String,
    installer: String?,
    signature: String?,
  ): Result<Unit> = call {
    api.report(
      InstallReportRequestJson(
        packageName = packageName,
        clickId = clickId,
        installer = installer,
        signature = signature,
      )
    )
    Unit
  }

  private suspend fun <T> call(block: suspend () -> T): Result<T> = withContext(dispatcher) {
    try {
      Result.success(block())
    } catch (e: CancellationException) {
      throw e
    } catch (e: IOException) {
      // Offline or timed out: expected, and the call is tried again later. Not reported.
      Result.failure(e)
    } catch (e: Throwable) {
      Result.failure(e.reportedUnlessRefusal())
    }
  }

  /**
   * Reports an unexpected failure (422, 5xx, a body that is not the expected one) to
   * Crashlytics and returns it. A refusal is expected: it is only logged, and comes back as a
   * [PaERefusalException] so callers can tell why.
   */
  private fun Throwable.reportedUnlessRefusal(): Throwable {
    val refusal = reportUnlessPaERefusal(this) { error ->
      exceptionHandler.recordException(error)
    } ?: return this
    return this as? PaERefusalException ?: PaERefusalException(refusal, this)
  }
}
