package cm.aptoide.pt.play_and_earn.sessions.data

import cm.aptoide.pt.play_and_earn.sessions.data.model.AccountStatusJson
import cm.aptoide.pt.test.gherkin.coScenario
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

// AND-951: the account status is a convenience, not a control: a clear answer is that answer,
// anything else is "could not tell", and its failures are nobody's crash.
@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultPaEAccountStatusRepositoryTest {

  private class FakeAccountStatusApi : AccountStatusApi {
    var answer: AccountStatusJson? = null
    var error: Throwable? = null

    override suspend fun getAccountStatus(): AccountStatusJson =
      error?.let { throw it } ?: answer ?: throw IllegalStateException("No answer")
  }

  private val api = FakeAccountStatusApi()

  @Test
  fun `A restricted account is restricted`() = coScenario { scope ->
    m Given "the backend says the account is restricted"
    api.answer = AccountStatusJson(restricted = true)

    m When "the status is asked"
    val status = repository(scope).getStatus()

    m Then "it is restricted"
    assertEquals(PaEAccountStatus.RESTRICTED, status)
  }

  @Test
  fun `An account in good standing is not restricted`() = coScenario { scope ->
    m Given "the backend says the account is not restricted"
    api.answer = AccountStatusJson(restricted = false)

    m When "the status is asked"
    val status = repository(scope).getStatus()

    m Then "it is not restricted"
    assertEquals(PaEAccountStatus.NOT_RESTRICTED, status)
  }

  @Test
  fun `An answer without the field cannot tell`() = coScenario { scope ->
    m Given "the backend answers without a restricted field"
    api.answer = AccountStatusJson(restricted = null)

    m When "the status is asked"
    val status = repository(scope).getStatus()

    m Then "it cannot tell"
    assertEquals(PaEAccountStatus.UNKNOWN, status)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("failures")
  fun `Any failure cannot tell`(name: String, error: Throwable) = coScenario { scope ->
    m Given "the status call fails with $name"
    api.error = error

    m When "the status is asked"
    val status = repository(scope).getStatus()

    m Then "it cannot tell, and the failure does not escape"
    assertEquals(PaEAccountStatus.UNKNOWN, status)
  }

  // Nothing to report through: the repository takes no exception handler at all.
  private fun repository(scope: TestScope) =
    DefaultPaEAccountStatusRepository(
      api = api,
      dispatcher = StandardTestDispatcher(scope.testScheduler),
    )

  companion object {
    private fun httpError(code: Int, body: String) = HttpException(
      Response.error<Any>(code, body.toResponseBody("application/json".toMediaType()))
    )

    @JvmStatic
    fun failures() = listOf(
      arrayOf("a network failure", IOException("offline")),
      arrayOf("a server error", httpError(500, """{"detail":"Internal error"}""")),
      arrayOf("a 502 page", httpError(502, "<html>Bad gateway</html>")),
      arrayOf("an expired sign-in", httpError(401, """{"detail":"Not authenticated"}""")),
      arrayOf("a malformed body", JsonSyntaxException("Expected BOOLEAN")),
    )
  }
}
