package cm.aptoide.pt.play_and_earn.sessions.data

import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusal
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalCode
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalException
import cm.aptoide.pt.exception_handler.ExceptionHandler
import cm.aptoide.pt.play_and_earn.sessions.data.model.InstallAnswerJson
import cm.aptoide.pt.play_and_earn.sessions.data.model.InstallClickRequestJson
import cm.aptoide.pt.play_and_earn.sessions.data.model.InstallReportRequestJson
import cm.aptoide.pt.test.gherkin.coScenario
import com.google.gson.Gson
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
import org.junit.jupiter.params.provider.ValueSource
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.time.Instant

// AND-954: the app says when a Play & Earn install starts (the click) and when it finished (the
// report); the daily limit and a restricted account come back as refusals.
@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultPaEInstallsRepositoryTest {

  private class FakeInstallsApi : InstallsApi {
    val clicks = mutableListOf<InstallClickRequestJson>()
    val reports = mutableListOf<InstallReportRequestJson>()
    var answer = InstallAnswerJson(eligible = true)
    var error: Throwable? = null

    override suspend fun click(request: InstallClickRequestJson): InstallAnswerJson {
      clicks += request
      return error?.let { throw it } ?: answer
    }

    override suspend fun report(request: InstallReportRequestJson): InstallAnswerJson {
      reports += request
      return error?.let { throw it } ?: answer
    }
  }

  private class RecordingExceptionHandler : ExceptionHandler {
    val recorded = mutableListOf<Throwable>()
    override fun recordException(exception: Throwable) {
      recorded += exception
    }
  }

  private val api = FakeInstallsApi()
  private val exceptionHandler = RecordingExceptionHandler()
  private val gson = Gson()

  private fun repository(scope: TestScope) = DefaultPaEInstallsRepository(
    api = api,
    dispatcher = StandardTestDispatcher(scope.testScheduler),
    exceptionHandler = exceptionHandler,
  )

  @Test
  fun `The click body names the game and the click id`() = coScenario { scope ->
    m Given "the backend records clicks"
    api.answer = InstallAnswerJson(eligible = true)

    m When "the start of an install is told"
    repository(scope).click("com.game", "click-1")

    m Then "one click is sent with the package and the click id"
    assertEquals(
      listOf("""{"package":"com.game","click_id":"click-1"}"""),
      api.clicks.map(gson::toJson)
    )
  }

  @Test
  fun `The report body carries the installer and the signature`() = coScenario { scope ->
    m Given "the backend takes reports"
    api.answer = InstallAnswerJson(eligible = true)

    m When "a finished install is reported with its evidence"
    val result = repository(scope).report(
      packageName = "com.game",
      clickId = "click-1",
      installer = "com.aptoide.android.aptoidegames",
      signature = "ab".repeat(32),
    )

    m Then "it succeeds, and the body has the four fields"
    assertTrue(result.isSuccess)
    assertEquals(
      listOf(
        """{"package":"com.game","click_id":"click-1",""" +
          """"installer":"com.aptoide.android.aptoidegames","signature":"${"ab".repeat(32)}"}"""
      ),
      api.reports.map(gson::toJson)
    )
  }

  @Test
  fun `A report without evidence leaves the fields out`() = coScenario { scope ->
    m Given "the installer and the signature could not be read"
    api.answer = InstallAnswerJson(eligible = true)

    m When "the finished install is reported"
    repository(scope).report("com.game", "click-1", installer = null, signature = null)

    m Then "the body has only the package and the click id"
    assertEquals(
      listOf("""{"package":"com.game","click_id":"click-1"}"""),
      api.reports.map(gson::toJson)
    )
  }

  @ParameterizedTest(name = "eligible {0}")
  @ValueSource(booleans = [true, false])
  fun `A recorded click says whether the game earns`(eligible: Boolean) = coScenario { scope ->
    m Given "the backend records the click and answers eligible $eligible"
    api.answer = InstallAnswerJson(eligible = eligible)

    m When "the click is sent"
    val result = repository(scope).click("com.game", "click-1")

    m Then "the answer is that"
    assertEquals(PaEInstallAnswer(eligible = eligible), result.getOrNull())
  }

  @Test
  fun `A click answer without the field is eligible`() = coScenario { scope ->
    m Given "the backend records the click with no eligible field"
    api.answer = InstallAnswerJson(eligible = null)

    m When "the click is sent"
    val result = repository(scope).click("com.game", "click-1")

    m Then "nothing is known against it"
    assertEquals(PaEInstallAnswer(eligible = true), result.getOrNull())
  }

  @Test
  fun `The day's limit is a refusal with its end`() = coScenario { scope ->
    m Given "the backend answers the click 429 INSTALL_LIMIT with a retry_at"
    api.error = httpError(
      429,
      """{"detail":{"code":"INSTALL_LIMIT","retry_at":"2026-11-28T00:00:00Z"}}"""
    )

    m When "the click is sent"
    val result = repository(scope).click("com.game", "click-1")

    m Then "it fails with the install limit and when it ends, and nothing is reported"
    assertEquals(
      PaERefusal(PaERefusalCode.INSTALL_LIMIT, Instant.parse("2026-11-28T00:00:00Z")),
      (result.exceptionOrNull() as PaERefusalException).refusal
    )
    assertEquals(emptyList<Throwable>(), exceptionHandler.recorded)
  }

  @Test
  fun `A restricted account is a refusal on both calls`() = coScenario { scope ->
    m Given "the backend answers 403 ACCOUNT_RESTRICTED"
    api.error = httpError(403, """{"detail":{"code":"ACCOUNT_RESTRICTED"}}""")
    val repository = repository(scope)

    m When "a click and a report are sent"
    val results = listOf(
      repository.click("com.game", "click-1"),
      repository.report("com.game", "click-1", installer = null, signature = null),
    )

    m Then "both fail with the account refusal, and nothing is reported"
    assertEquals(
      List(2) { PaERefusal(PaERefusalCode.ACCOUNT_RESTRICTED) },
      results.map { (it.exceptionOrNull() as PaERefusalException).refusal }
    )
    assertEquals(emptyList<Throwable>(), exceptionHandler.recorded)
  }

  @ParameterizedTest(name = "HTTP {0}")
  @ValueSource(ints = [422, 500, 503])
  fun `A server error is a failure, not a refusal, and is reported`(status: Int) =
    coScenario { scope ->
      m Given "the backend answers the click HTTP $status"
      val error = httpError(status, """{"detail":"Could not be recorded"}""")
      api.error = error

      m When "the click is sent"
      val result = repository(scope).click("com.game", "click-1")

      m Then "it fails with that error, which goes to Crashlytics"
      assertSame(error, result.exceptionOrNull())
      assertEquals(listOf<Throwable>(error), exceptionHandler.recorded)
    }

  @Test
  fun `A network failure is a failure that is not reported`() = coScenario { scope ->
    m Given "the report never reaches the server"
    val error = IOException("offline")
    api.error = error

    m When "the finished install is reported"
    val result =
      repository(scope).report("com.game", "click-1", installer = null, signature = null)

    m Then "it fails with that error, and nothing goes to Crashlytics"
    assertSame(error, result.exceptionOrNull())
    assertEquals(emptyList<Throwable>(), exceptionHandler.recorded)
  }

  private fun httpError(code: Int, body: String) = HttpException(
    Response.error<Any>(code, body.toResponseBody("application/json".toMediaType()))
  )
}
