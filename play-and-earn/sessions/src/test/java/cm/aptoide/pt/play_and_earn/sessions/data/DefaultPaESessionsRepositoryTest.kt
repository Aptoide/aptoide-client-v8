package cm.aptoide.pt.play_and_earn.sessions.data

import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusal
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalCode
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalException
import cm.aptoide.pt.environment_info.DeviceIdProvider
import cm.aptoide.pt.exception_handler.ExceptionHandler
import cm.aptoide.pt.play_and_earn.sessions.data.model.SessionEndInfoJson
import cm.aptoide.pt.play_and_earn.sessions.data.model.SessionEndRequestData
import cm.aptoide.pt.play_and_earn.sessions.data.model.SessionHeartbeatData
import cm.aptoide.pt.play_and_earn.sessions.data.model.SessionInfoJson
import cm.aptoide.pt.play_and_earn.sessions.data.model.SessionStartInfoJson
import cm.aptoide.pt.play_and_earn.sessions.data.model.SessionStartRequestData
import cm.aptoide.pt.play_and_earn.sessions.data.model.SessionStatus
import cm.aptoide.pt.test.gherkin.coScenario
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultPaESessionsRepositoryTest {

  private class FakeSessionsApi : SessionsApi {
    var startError: Throwable? = null
    var heartbeatStatus: SessionStatus? = SessionStatus.OK
    var endError: Throwable? = null

    override suspend fun startSession(sessionStartData: SessionStartRequestData) =
      startError?.let { throw it } ?: SessionStartInfoJson("session", stolen = null, ttl = 180)

    override suspend fun heartbeatSession(sessionHeartbeatData: SessionHeartbeatData) =
      SessionInfoJson(heartbeatStatus, appliedSeconds = 10, ttl = 180, events = null)

    override suspend fun endSession(sessionEndRequestData: SessionEndRequestData) =
      throw endError ?: UnsupportedOperationException()
  }

  private class RecordingExceptionHandler : ExceptionHandler {
    val recorded = mutableListOf<Throwable>()
    override fun recordException(exception: Throwable) {
      recorded += exception
    }
  }

  private val api = FakeSessionsApi()
  private val exceptionHandler = RecordingExceptionHandler()

  private fun repository(dispatcher: CoroutineDispatcher) =
    DefaultPaESessionsRepository(
      sessionsApi = api,
      deviceIdProvider = object : DeviceIdProvider {
        override suspend fun getDeviceId() = "device"
      },
      dispatcher = dispatcher,
      exceptionHandler = exceptionHandler,
    )

  @ParameterizedTest(name = "{0}")
  @ValueSource(
    strings = [
      "ACCOUNT_RESTRICTED", "REGION_NOT_SUPPORTED", "DEVICE_NOT_SUPPORTED", "UPDATE_REQUIRED",
      "VPN_NOT_ALLOWED",
    ]
  )
  fun `A refused session start fails with its code and is not reported`(code: String) =
    coScenario { scope ->
      m Given "the backend refuses the session start with $code"
      api.startError = httpError(if (code == "UPDATE_REQUIRED") 426 else 403, code)

      m When "a session is started"
      val result = repository(StandardTestDispatcher(scope.testScheduler)).startSession("game")

      m Then "it fails with that refusal, and nothing goes to Crashlytics"
      val error = result.exceptionOrNull()
      assertTrue(error is PaERefusalException)
      assertEquals(
        PaERefusal(PaERefusalCode.valueOf(code)),
        (error as PaERefusalException).refusal
      )
      assertEquals(emptyList<Throwable>(), exceptionHandler.recorded)
    }

  @Test
  fun `A refusal without a code is reported as before`() = coScenario { scope ->
    m Given "the backend answers a plain 403 with a string detail"
    val error = HttpException(
      Response.error<Any>(
        403,
        """{"detail":"Not available"}""".toResponseBody("application/json".toMediaType())
      )
    )
    api.startError = error

    m When "a session is started"
    val result = repository(StandardTestDispatcher(scope.testScheduler)).startSession("game")

    m Then "it fails with the HTTP error, which goes to Crashlytics as today"
    assertSame(error, result.exceptionOrNull())
    assertEquals(listOf<Throwable>(error), exceptionHandler.recorded)
  }

  @Test
  fun `A network failure is reported as before`() = coScenario { scope ->
    m Given "the session start never reaches the server"
    val error = IOException("offline")
    api.startError = error

    m When "a session is started"
    val result = repository(StandardTestDispatcher(scope.testScheduler)).startSession("game")

    m Then "it fails with that error, which goes to Crashlytics as today"
    assertSame(error, result.exceptionOrNull())
    assertEquals(listOf<Throwable>(error), exceptionHandler.recorded)
  }

  @Test
  fun `A restricted heartbeat fails as an account refusal and is not reported`() =
    coScenario { scope ->
      m Given "the backend answers a heartbeat with status account_restricted"
      api.heartbeatStatus = SessionStatus.ACCOUNT_RESTRICTED

      m When "a heartbeat is sent"
      val result = repository(StandardTestDispatcher(scope.testScheduler))
        .heartbeatSession("session", "game", sequence = 0, seconds = 10)

      m Then "it fails with the account refusal, and nothing goes to Crashlytics"
      val error = result.exceptionOrNull()
      assertTrue(error is PaERefusalException)
      assertEquals(
        PaERefusalCode.ACCOUNT_RESTRICTED,
        (error as PaERefusalException).refusal.code
      )
      assertEquals(emptyList<Throwable>(), exceptionHandler.recorded)
    }

  @Test
  fun `A refused session end fails with its code and is not reported`() = coScenario { scope ->
    m Given "the backend refuses the session end, the account restricted meanwhile"
    api.endError = httpError(403, "ACCOUNT_RESTRICTED")

    m When "the session is ended"
    val result = repository(StandardTestDispatcher(scope.testScheduler))
      .endSession("session", "game")

    m Then "it fails with that refusal, and nothing goes to Crashlytics"
    assertEquals(
      PaERefusalCode.ACCOUNT_RESTRICTED,
      (result.exceptionOrNull() as? PaERefusalException)?.refusal?.code
    )
    assertEquals(emptyList<Throwable>(), exceptionHandler.recorded)
  }

  @Test
  fun `A failed session end is reported as before`() = coScenario { scope ->
    m Given "the session end fails on the network"
    val error = IOException("offline")
    api.endError = error

    m When "the session is ended"
    val result = repository(StandardTestDispatcher(scope.testScheduler))
      .endSession("session", "game")

    m Then "it fails with that error, which goes to Crashlytics as today"
    assertSame(error, result.exceptionOrNull())
    assertEquals(listOf<Throwable>(error), exceptionHandler.recorded)
  }

  @Test
  fun `A refusal code on a server error is reported as before`() = coScenario { scope ->
    m Given "a 502 whose body carries a region refusal"
    val error = HttpException(
      Response.error<Any>(
        502,
        """{"detail":{"code":"REGION_NOT_SUPPORTED"}}"""
          .toResponseBody("application/json".toMediaType())
      )
    )
    api.startError = error

    m When "a session is started"
    val result = repository(StandardTestDispatcher(scope.testScheduler)).startSession("game")

    m Then "it is the HTTP error, reported to Crashlytics"
    assertSame(error, result.exceptionOrNull())
    assertEquals(listOf<Throwable>(error), exceptionHandler.recorded)
  }

  @Test
  fun `An ok heartbeat still succeeds`() = coScenario { scope ->
    m Given "the backend answers a heartbeat with status ok"
    api.heartbeatStatus = SessionStatus.OK

    m When "a heartbeat is sent"
    val result = repository(StandardTestDispatcher(scope.testScheduler))
      .heartbeatSession("session", "game", sequence = 0, seconds = 10)

    m Then "it succeeds with the applied seconds"
    assertEquals(10, result.getOrNull()?.appliedSeconds)
  }

  private fun httpError(status: Int, code: String) = HttpException(
    Response.error<Any>(
      status,
      """{"detail":{"code":"$code","message":"Refused"}}"""
        .toResponseBody("application/json".toMediaType())
    )
  )
}
