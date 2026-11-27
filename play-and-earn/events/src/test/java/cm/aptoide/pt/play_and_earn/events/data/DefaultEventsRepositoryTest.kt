package cm.aptoide.pt.play_and_earn.events.data

import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusal
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalCode
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalException
import cm.aptoide.pt.exception_handler.ExceptionHandler
import cm.aptoide.pt.play_and_earn.events.data.model.SubmitEventRequestJson
import cm.aptoide.pt.play_and_earn.events.data.model.SubmitEventResponseJson
import cm.aptoide.pt.play_and_earn.events.domain.EventType
import cm.aptoide.pt.test.gherkin.coScenario
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

// AND-953: the sign-in bonus event tells a refusal apart from a failure worth retrying.
@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultEventsRepositoryTest {

  private class FakeEventsApi : EventsApi {
    var error: Throwable? = null

    override suspend fun submitEvent(request: SubmitEventRequestJson) =
      error?.let { throw it } ?: SubmitEventResponseJson("ok")
  }

  private class RecordingExceptionHandler : ExceptionHandler {
    val recorded = mutableListOf<Throwable>()
    override fun recordException(exception: Throwable) {
      recorded += exception
    }
  }

  private val api = FakeEventsApi()
  private val exceptionHandler = RecordingExceptionHandler()

  @ParameterizedTest(name = "{0}")
  @EnumSource(
    PaERefusalCode::class,
    names = [
      "ACCOUNT_RESTRICTED", "REGION_NOT_SUPPORTED", "DEVICE_NOT_SUPPORTED", "UPDATE_REQUIRED",
      "VPN_NOT_ALLOWED", "SIGN_IN_BONUS_DEVICE_LIMIT",
    ]
  )
  fun `A refused event fails with its code and is not reported`(code: PaERefusalCode) =
    coScenario { scope ->
      m Given "the backend refuses the sign-in bonus with ${code.name}"
      api.error = httpError(
        code.httpStatus,
        """{"detail":{"code":"${code.name}","message":"Refused"}}"""
      )

      m When "the event is submitted"
      val result = repository(scope).submitEvent(EventType.FIRST_SIGN_IN)

      m Then "it fails with that refusal, and nothing goes to Crashlytics"
      val error = result.exceptionOrNull()
      assertTrue(error is PaERefusalException, error.toString())
      assertEquals(PaERefusal(code), (error as PaERefusalException).refusal)
      assertEquals(emptyList<Throwable>(), exceptionHandler.recorded)
    }

  @Test
  fun `An already submitted event stays a conflict and is not reported`() = coScenario { scope ->
    m Given "the backend answers 409, the bonus already granted"
    api.error = httpError(409, """{"detail":"Event already submitted"}""")

    m When "the event is submitted"
    val result = repository(scope).submitEvent(EventType.FIRST_SIGN_IN)

    m Then "it fails as already submitted, as today, and nothing goes to Crashlytics"
    val error = result.exceptionOrNull()
    assertTrue(error is EventAlreadySubmittedException, error.toString())
    assertEquals("Event already submitted", error?.message)
    assertEquals(emptyList<Throwable>(), exceptionHandler.recorded)
  }

  @Test
  fun `A 403 with a string detail stays a plain event error and is not reported`() =
    coScenario { scope ->
      m Given "the backend answers 403 Not available, a per-network limit without a code"
      api.error = httpError(403, """{"detail":"Not available"}""")

      m When "the event is submitted"
      val result = repository(scope).submitEvent(EventType.FIRST_SIGN_IN)

      m Then "it is a retryable event error with that detail, and nothing is reported"
      val error = result.exceptionOrNull()
      assertEquals(EventException::class.java, error?.javaClass)
      assertEquals("Not available", error?.message)
      assertEquals(emptyList<Throwable>(), exceptionHandler.recorded)
    }

  @ParameterizedTest(name = "{0}")
  @ValueSource(ints = [500, 503])
  fun `A server error stays a plain event error and is reported`(status: Int) =
    coScenario { scope ->
      m Given "the backend answers $status with a string detail"
      val httpError = httpError(status, """{"detail":"Unavailable"}""")
      api.error = httpError

      m When "the event is submitted"
      val result = repository(scope).submitEvent(EventType.FIRST_SIGN_IN)

      m Then "it is a retryable event error with that detail; the HTTP error is reported"
      val error = result.exceptionOrNull()
      assertEquals(EventException::class.java, error?.javaClass)
      assertEquals("Unavailable", error?.message)
      assertEquals(listOf<Throwable>(httpError), exceptionHandler.recorded)
    }

  @ParameterizedTest(name = "{0}")
  @ValueSource(strings = ["""{"detail":[{"msg":"bad event"}]}""", ""])
  fun `An unexpected client error is reported`(body: String) = coScenario { scope ->
    m Given "the backend answers 422 with body '$body'"
    val httpError = httpError(422, body)
    api.error = httpError

    m When "the event is submitted"
    val result = repository(scope).submitEvent(EventType.FIRST_SIGN_IN)

    m Then "it is a plain event error; the HTTP error is reported"
    assertEquals(EventException::class.java, result.exceptionOrNull()?.javaClass)
    assertEquals(listOf<Throwable>(httpError), exceptionHandler.recorded)
  }

  @Test
  fun `A 403 without a detail is reported`() = coScenario { scope ->
    m Given "the backend answers 403 with an empty body"
    val httpError = httpError(403, "")
    api.error = httpError

    m When "the event is submitted"
    val result = repository(scope).submitEvent(EventType.FIRST_SIGN_IN)

    m Then "it is a plain event error; the HTTP error is reported"
    assertEquals(EventException::class.java, result.exceptionOrNull()?.javaClass)
    assertEquals(listOf<Throwable>(httpError), exceptionHandler.recorded)
  }

  @Test
  fun `A refusal code on a server error is not a refusal`() = coScenario { scope ->
    m Given "a 502 whose body carries a region refusal"
    val httpError = httpError(502, """{"detail":{"code":"REGION_NOT_SUPPORTED"}}""")
    api.error = httpError

    m When "the event is submitted"
    val result = repository(scope).submitEvent(EventType.FIRST_SIGN_IN)

    m Then "it is a retryable event error; the HTTP error is reported"
    val error = result.exceptionOrNull()
    assertEquals(EventException::class.java, error?.javaClass)
    assertEquals(listOf<Throwable>(httpError), exceptionHandler.recorded)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("networkFailures")
  fun `A network failure is returned as is and not reported`(networkError: IOException) =
    coScenario { scope ->
      m Given "the event never reaches the server: ${networkError.javaClass.simpleName}"
      api.error = networkError

      m When "the event is submitted"
      val result = repository(scope).submitEvent(EventType.FIRST_SIGN_IN)

      m Then "it fails with that error, and nothing is reported"
      assertSame(networkError, result.exceptionOrNull())
      assertEquals(emptyList<Throwable>(), exceptionHandler.recorded)
    }

  @Test
  fun `An unexpected failure is returned as is and reported`() = coScenario { scope ->
    m Given "the response cannot be read"
    val unexpected = IllegalStateException("malformed")
    api.error = unexpected

    m When "the event is submitted"
    val result = repository(scope).submitEvent(EventType.FIRST_SIGN_IN)

    m Then "it fails with that error, reported to Crashlytics"
    assertSame(unexpected, result.exceptionOrNull())
    assertEquals(listOf<Throwable>(unexpected), exceptionHandler.recorded)
  }

  @Test
  fun `A cancelled request is rethrown and not reported`() = coScenario { scope ->
    m Given "the request is cancelled while in flight"
    api.error = CancellationException("left the screen")

    m When "the event is submitted"
    val thrown = runCatching { repository(scope).submitEvent(EventType.FIRST_SIGN_IN) }

    m Then "the cancellation propagates, and nothing is reported"
    assertTrue(thrown.exceptionOrNull() is CancellationException, thrown.toString())
    assertEquals(emptyList<Throwable>(), exceptionHandler.recorded)
  }

  @Test
  fun `An accepted event succeeds`() = coScenario { scope ->
    m Given "the backend accepts the event"
    api.error = null

    m When "the event is submitted"
    val result = repository(scope).submitEvent(EventType.FIRST_SIGN_IN)

    m Then "it succeeds and nothing is reported"
    assertTrue(result.isSuccess)
    assertEquals(emptyList<Throwable>(), exceptionHandler.recorded)
  }

  private fun repository(scope: TestScope) = DefaultEventsRepository(
    eventsApi = api,
    dispatcher = StandardTestDispatcher(scope.testScheduler),
    exceptionHandler = exceptionHandler,
  )

  companion object {
    @JvmStatic
    fun networkFailures() = listOf(
      IOException("offline"),
      UnknownHostException("rewards.aptoide.com"),
      SocketTimeoutException("timeout"),
    )
  }

  private fun httpError(status: Int, body: String) = HttpException(
    Response.error<Any>(status, body.toResponseBody("application/json".toMediaType()))
  )
}
