package cm.aptoide.pt.play_and_earn.events.data

import cm.aptoide.pt.play_and_earn.events.data.model.EventErrorJson
import cm.aptoide.pt.play_and_earn.events.data.model.SubmitEventRequestJson
import cm.aptoide.pt.play_and_earn.events.domain.EventType
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.net.HttpURLConnection
import javax.inject.Inject

internal class DefaultEventsRepository @Inject constructor(
  private val eventsApi: EventsApi,
  private val dispatcher: CoroutineDispatcher
) : EventsRepository {

  private val gson = Gson()

  override suspend fun submitEvent(
    guestId: String,
    eventType: EventType
  ): Result<Unit> = withContext(dispatcher) {
    try {
      eventsApi.submitEvent(SubmitEventRequestJson(guestId = guestId, eventType = eventType))
      Result.success(Unit)
    } catch (e: HttpException) {
      val detail = e.parseErrorDetail()
      val error = if (e.code() == HttpURLConnection.HTTP_CONFLICT) {
        EventAlreadySubmittedException(detail ?: e.message())
      } else {
        EventException(detail ?: e.message())
      }
      Result.failure(error)
    } catch (e: Throwable) {
      e.printStackTrace()
      Result.failure(e)
    }
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
