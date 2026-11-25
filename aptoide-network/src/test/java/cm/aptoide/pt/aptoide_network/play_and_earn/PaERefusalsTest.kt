package cm.aptoide.pt.aptoide_network.play_and_earn

import cm.aptoide.pt.test.gherkin.scenario
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.time.Instant

// AND-950: a refusal is the only Play & Earn error whose `detail` is an object with a `code`.
internal class PaERefusalsTest {

  @ParameterizedTest(name = "{0}")
  @EnumSource(PaERefusalCode::class)
  fun `Reads every refusal code without a retry time`(code: PaERefusalCode) = scenario {
    m Given "a refusal with code ${code.name} and no retry_at"
    val error = httpError(403, """{"detail":{"code":"${code.name}","message":"Refused"}}""")

    m When "it is parsed"
    val refusal = PaERefusals.from(error)

    m Then "it is that code, with no retry time"
    assertEquals(PaERefusal(code, retryAt = null), refusal)
  }

  @Test
  fun `Reads the retry time of a limit`() = scenario {
    m Given "a redeem limit with a retry_at"
    val error = httpError(
      429,
      """{"detail":{"code":"REDEEM_LIMIT","message":"Tomorrow","retry_at":"2026-11-20T14:05:00Z"}}"""
    )

    m When "it is parsed"
    val refusal = PaERefusals.from(error)

    m Then "the retry time comes with the code"
    assertEquals(
      PaERefusal(PaERefusalCode.REDEEM_LIMIT, Instant.parse("2026-11-20T14:05:00Z")),
      refusal
    )
  }

  @Test
  fun `Reads a retry time written with an offset`() = scenario {
    m Given "an install limit whose retry_at carries +00:00 instead of Z"
    val error = httpError(
      429,
      """{"detail":{"code":"INSTALL_LIMIT","retry_at":"2026-11-20T14:05:00+00:00"}}"""
    )

    m When "it is parsed"
    val refusal = PaERefusals.from(error)

    m Then "the retry time is the same instant"
    assertEquals(Instant.parse("2026-11-20T14:05:00Z"), refusal?.retryAt)
  }

  @Test
  fun `An unreadable retry time keeps the refusal`() = scenario {
    m Given "a too-many-attempts refusal whose retry_at is not a date"
    val error = httpError(429, """{"detail":{"code":"TOO_MANY_ATTEMPTS","retry_at":"soon"}}""")

    m When "it is parsed"
    val refusal = PaERefusals.from(error)

    m Then "it is still that refusal, without a retry time"
    assertEquals(PaERefusal(PaERefusalCode.TOO_MANY_ATTEMPTS, retryAt = null), refusal)
  }

  @ParameterizedTest(name = "{0}")
  @ValueSource(
    strings = [
      """{"detail":{"code":"SOMETHING_NEW","message":"New"}}""",
      """{"detail":{"message":"No code"}}""",
      """{"detail":{"code":null}}""",
      """{"detail":{"code":42}}""",
      """{"detail":"Not available"}""",
      """{"detail":[{"loc":["body","package"],"msg":"field required","type":"value_error"}]}""",
      """{"error":"other shape"}""",
      """[]""",
      """"just a string"""",
      """{"detail":{"code":"REGION_NOT_SUPPORTED""",
      """<html>Bad gateway</html>""",
      "",
    ]
  )
  fun `Anything else is not a refusal`(body: String) = scenario {
    m Given "an error body that is not a known refusal: $body"
    val error = httpError(403, body)

    m When "it is parsed"
    val refusal = PaERefusals.from(error)

    m Then "there is no refusal: the caller keeps its generic error"
    assertNull(refusal)
  }

  @Test
  fun `An error without a body is not a refusal`() = scenario {
    m Given "an HTTP error whose response has no error body"
    val error = HttpException(Response.success(null))

    m When "it is parsed"
    val refusal = PaERefusals.from(error)

    m Then "there is no refusal"
    assertNull(refusal)
  }

  @Test
  fun `A network failure is not a refusal`() = scenario {
    m Given "a failure that never reached the server"
    val error = IOException("offline")

    m When "it is parsed"
    val refusal = PaERefusals.from(error)

    m Then "there is no refusal"
    assertNull(refusal)
  }

  @Test
  fun `Nothing is not a refusal`() = scenario {
    m Given "no failure at all"
    m When "it is parsed"
    val refusal = PaERefusals.from(null)

    m Then "there is no refusal"
    assertNull(refusal)
  }

  @Test
  fun `The same error reads the same twice`() = scenario {
    m Given "a region refusal already parsed once"
    val error = httpError(403, """{"detail":{"code":"REGION_NOT_SUPPORTED"}}""")
    PaERefusals.from(error)

    m When "it is parsed again, e.g. by the crash reporting filter"
    val refusal = PaERefusals.from(error)

    m Then "the error body was not consumed by the first read"
    assertEquals(PaERefusal(PaERefusalCode.REGION_NOT_SUPPORTED, retryAt = null), refusal)
  }

  @Test
  fun `A typed refusal reads as itself`() = scenario {
    m Given "a refusal a repository already turned into a typed exception"
    val refusal = PaERefusal(PaERefusalCode.ACCOUNT_RESTRICTED, retryAt = null)
    val error = PaERefusalException(refusal)

    m When "it is parsed"
    val parsed = PaERefusals.from(error)

    m Then "it is the same refusal"
    assertEquals(refusal, parsed)
  }

  @Test
  fun `A refusal wrapped in another exception is found`() = scenario {
    m Given "a device refusal wrapped as the cause of another exception"
    val error = IllegalStateException(
      "wrapped",
      httpError(403, """{"detail":{"code":"DEVICE_NOT_SUPPORTED"}}""")
    )

    m When "it is parsed"
    val refusal = PaERefusals.from(error)

    m Then "the refusal of the cause is read"
    assertEquals(PaERefusal(PaERefusalCode.DEVICE_NOT_SUPPORTED, retryAt = null), refusal)
  }

  private fun httpError(code: Int, body: String) = HttpException(
    Response.error<Any>(code, body.toResponseBody("application/json".toMediaType()))
  )
}
