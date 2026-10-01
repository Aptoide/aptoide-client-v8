package cm.aptoide.pt.device_api.error

import cm.aptoide.pt.test.gherkin.scenario
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response

// The device API answers every failure with an RFC 9457 problem+json body. Callers decide what
// to show from the typed failure alone, so each status that carries product meaning has to map
// to its own type, and a body that cannot be read must never hide the status.
internal class ProblemMapperTest {

  @Test
  fun `An unknown app maps to not in variant`() = scenario {
    m Given "a 404 problem saying the app is not in the catalog"
    val error = httpError(404, problem(404, detail = "App 'a.b' is not in the catalog."))

    m When "it is mapped"
    val failure = ProblemMapper.map(error)

    m Then "it is a not-in-variant failure carrying the detail"
    assertInstanceOf(DeviceApiException.NotInVariant::class.java, failure)
    assertEquals(404, failure.statusCode)
    assertEquals("App 'a.b' is not in the catalog.", failure.message)
  }

  @Test
  fun `A removed app keeps its public reason`() = scenario {
    m Given "a 410 problem with the public removal reason"
    val error = httpError(410, problem(410, detail = "Removed at the developer's request."))

    m When "it is mapped"
    val failure = ProblemMapper.map(error)

    m Then "the reason is available to show"
    val removed = assertInstanceOf(DeviceApiException.Removed::class.java, failure)
    assertEquals("Removed at the developer's request.", removed.reason)
  }

  @Test
  fun `A country block maps to country unavailable`() = scenario {
    m Given "a 451 problem"
    val error = httpError(451, problem(451, detail = "Not available in your country."))

    m When "it is mapped"
    val failure = ProblemMapper.map(error)

    m Then "it is a country failure"
    assertInstanceOf(DeviceApiException.CountryUnavailable::class.java, failure)
    assertEquals(451, failure.statusCode)
  }

  @Test
  fun `An age restriction carries the age rating`() = scenario {
    m Given "the typed 403 age-restricted problem"
    val body = """
      {"type": "https://api.aptoide.com/problems/age-restricted", "title": "Age restricted",
       "status": 403, "detail": "Restricted.", "age_rating": 18}
    """.trimIndent()

    m When "it is mapped"
    val failure = ProblemMapper.map(httpError(403, body))

    m Then "the rating is kept"
    val restricted = assertInstanceOf(DeviceApiException.AgeRestricted::class.java, failure)
    assertEquals(18, restricted.ageRating)
  }

  @Test
  fun `An age restriction without a rating is still an age restriction`() = scenario {
    m Given "the age-restricted problem with no age rating in it"
    val body = """
      {"type": "https://api.aptoide.com/problems/age-restricted", "status": 403,
       "detail": "Restricted."}
    """.trimIndent()

    m When "it is mapped"
    val failure = ProblemMapper.map(httpError(403, body))

    m Then "the type is kept and the rating is unknown"
    val restricted = assertInstanceOf(DeviceApiException.AgeRestricted::class.java, failure)
    assertNull(restricted.ageRating)
  }

  @Test
  fun `A plain 403 is not an age restriction`() = scenario {
    m Given "a 403 problem of any other type"
    val error = httpError(403, problem(403, detail = "Forbidden."))

    m When "it is mapped"
    val failure = ProblemMapper.map(error)

    m Then "it is a generic failure with its status"
    assertInstanceOf(DeviceApiException.Generic::class.java, failure)
    assertEquals(403, failure.statusCode)
  }

  @Test
  fun `An upstream outage maps to upstream`() = scenario {
    m Given "a 502 with an empty json body, as the service answers when its source is down"
    val error = httpError(502, "{}")

    m When "it is mapped"
    val failure = ProblemMapper.map(error)

    m Then "it is an upstream failure"
    assertInstanceOf(DeviceApiException.Upstream::class.java, failure)
    assertEquals(502, failure.statusCode)
  }

  @Test
  fun `The title is used when there is no detail`() = scenario {
    m Given "a 404 problem with only a title"
    val error = httpError(404, """{"title": "Not Found", "status": 404}""")

    m When "it is mapped"
    val failure = ProblemMapper.map(error)

    m Then "the title is the message"
    assertEquals("Not Found", failure.message)
  }

  @Test
  fun `The title is used when the detail is blank`() = scenario {
    m Given "a 404 problem whose detail is empty"
    val error = httpError(404, """{"title": "Not Found", "status": 404, "detail": " "}""")

    m When "it is mapped"
    val failure = ProblemMapper.map(error)

    m Then "the title is the message, as a blank reason cannot be shown"
    assertEquals("Not Found", failure.message)
  }

  @Test
  fun `An empty body still maps by status`() = scenario {
    m Given "a 404 with nothing in its body"
    val error = httpError(404, "")

    m When "it is mapped"
    val failure = ProblemMapper.map(error)

    m Then "the status decides the type and there is no reason"
    assertInstanceOf(DeviceApiException.NotInVariant::class.java, failure)
    assertNull(failure.message)
  }

  @Test
  fun `An unreadable body still maps by status`() = scenario {
    m Given "a 404 whose body is not json"
    val error = httpError(404, "<html>gateway</html>")

    m When "it is mapped"
    val failure = ProblemMapper.map(error)

    m Then "the status decides the type and there is no reason"
    assertInstanceOf(DeviceApiException.NotInVariant::class.java, failure)
    assertNull(failure.message)
  }

  @Test
  fun `Any other status is generic and keeps its cause`() = scenario {
    m Given "a 422 problem"
    val error = httpError(422, problem(422, detail = "Provide at most one of 'q' or 'category'."))

    m When "it is mapped"
    val failure = ProblemMapper.map(error)

    m Then "it is generic, with the status, the detail and the original error"
    assertInstanceOf(DeviceApiException.Generic::class.java, failure)
    assertEquals(422, failure.statusCode)
    assertEquals("Provide at most one of 'q' or 'category'.", failure.message)
    assertSame(error, failure.cause)
  }
}

internal fun httpError(code: Int, body: String): HttpException = HttpException(
  Response.error<Any>(code, body.toResponseBody("application/problem+json".toMediaType()))
)

internal fun problem(status: Int, detail: String): String =
  """{"type": "about:blank", "title": "Problem", "status": $status, "detail": "$detail"}"""
