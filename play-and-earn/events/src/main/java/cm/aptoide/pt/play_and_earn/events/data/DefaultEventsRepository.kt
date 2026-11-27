package cm.aptoide.pt.play_and_earn.events.data

import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalException
import cm.aptoide.pt.aptoide_network.play_and_earn.reportUnlessPaERefusal
import cm.aptoide.pt.exception_handler.ExceptionHandler
import cm.aptoide.pt.play_and_earn.events.data.model.EventErrorJson
import cm.aptoide.pt.play_and_earn.events.data.model.SubmitEventRequestJson
import cm.aptoide.pt.play_and_earn.events.domain.EventType
import com.google.gson.Gson
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.net.HttpURLConnection
import javax.inject.Inject

internal class DefaultEventsRepository @Inject constructor(
  private val eventsApi: EventsApi,
  private val dispatcher: CoroutineDispatcher,
  private val exceptionHandler: ExceptionHandler,
) : EventsRepository {

  private val gson = Gson()

  override suspend fun submitEvent(
    eventType: EventType
  ): Result<Unit> = withContext(dispatcher) {
    try {
      eventsApi.submitEvent(SubmitEventRequestJson(eventType = eventType))
      Result.success(Unit)
    } catch (e: HttpException) {
      if (e.code() == HttpURLConnection.HTTP_CONFLICT) {
        // Expected: the event was granted before. Not a failure worth reporting.
        Result.failure(EventAlreadySubmittedException(e.parseErrorDetail() ?: e.message()))
      } else {
        Result.failure(
          e.reportedUnlessRefusal { EventException(e.parseErrorDetail() ?: e.message()) }
        )
      }
    } catch (e: CancellationException) {
      Result.failure(e)
    } catch (e: Throwable) {
      e.printStackTrace()
      Result.failure(e.reportedUnlessRefusal { e })
    }
  }

  /**
   * A refusal (AND-953) is expected: it is only logged and comes back as a
   * [PaERefusalException]. Any other failure is reported to Crashlytics and comes back as
   * [failure] makes it, read only then: the refusal check must peek the body before it is read.
   */
  private fun Throwable.reportedUnlessRefusal(failure: () -> Throwable): Throwable {
    val refusal = reportUnlessPaERefusal(this, exceptionHandler::recordException)
    return refusal?.let { PaERefusalException(it, this) } ?: failure()
  }

  // The error response body has the shape { "detail": "..." }. A 422 (unknown event type) has
  // no detail, so callers fall back to the HttpException message.
  private fun HttpException.parseErrorDetail(): String? = runCatching {
    response()?.errorBody()?.string()
      ?.let { gson.fromJson(it, EventErrorJson::class.java) }
      ?.detail
  }.getOrNull()
}

open class EventException(message: String) : Throwable(message)

// The event was already submitted for this user (HTTP 409).
class EventAlreadySubmittedException(message: String) : EventException(message)
