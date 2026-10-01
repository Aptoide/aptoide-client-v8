package cm.aptoide.pt.device_api.error

import cm.aptoide.pt.test.gherkin.coScenario
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.MalformedJsonException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import java.io.IOException

// Repositories wrap every device API request in deviceApiCall. Service failures become typed,
// but connectivity and cancellation must reach the caller untouched: the UI tells "no
// connection" apart by IOException, and swallowing a cancellation would keep dead work alive.
@ExperimentalCoroutinesApi
internal class DeviceApiCallTest {

  @Test
  fun `A successful call returns its value`() = coScenario {
    m Given "a call that succeeds"
    val call: suspend () -> String = { "ok" }

    m When "it runs"
    val result = deviceApiCall(call)

    m Then "the value comes through"
    assertEquals("ok", result)
  }

  @Test
  fun `A service error becomes a typed failure`() = coScenario {
    m Given "a call answered with a 404 problem"
    val call: suspend () -> String = { throw httpError(404, problem(404, detail = "Unknown.")) }

    m When "it runs"
    val failure = runCatching { deviceApiCall(call) }.exceptionOrNull()

    m Then "the caller sees the typed failure"
    assertInstanceOf(DeviceApiException.NotInVariant::class.java, failure)
  }

  @Test
  fun `A response that cannot be parsed becomes a generic failure`() = coScenario {
    m Given "a call whose body does not match the contract"
    val cause = JsonSyntaxException("Expected BEGIN_OBJECT")
    val call: suspend () -> String = { throw cause }

    m When "it runs"
    val failure = runCatching { deviceApiCall(call) }.exceptionOrNull()

    m Then "it is a generic failure keeping the cause"
    val generic = assertInstanceOf(DeviceApiException.Generic::class.java, failure)
    assertSame(cause, generic.cause)
  }

  // What Retrofit's converter really throws for a body that is not json, such as a gateway's
  // html page answered with a 200. It is an IOException, so left alone it would read as "no
  // connection" when the service is what failed.
  @Test
  fun `A body that is not json becomes a generic failure`() = coScenario {
    m Given "a call answered with something that is not json"
    val cause = MalformedJsonException("Use JsonReader.setStrictness to accept malformed JSON")
    val call: suspend () -> String = { throw cause }

    m When "it runs"
    val failure = runCatching { deviceApiCall(call) }.exceptionOrNull()

    m Then "it is a generic failure, not a connectivity error"
    val generic = assertInstanceOf(DeviceApiException.Generic::class.java, failure)
    assertSame(cause, generic.cause)
  }

  @Test
  fun `A connectivity error passes through untouched`() = coScenario {
    m Given "a call that fails to connect"
    val cause = IOException("timeout")
    val call: suspend () -> String = { throw cause }

    m When "it runs"
    val failure = runCatching { deviceApiCall(call) }.exceptionOrNull()

    m Then "the same error reaches the caller"
    assertSame(cause, failure)
  }

  @Test
  fun `A cancellation passes through untouched`() = coScenario {
    m Given "a call that is cancelled"
    val cause = CancellationException("cancelled")
    val call: suspend () -> String = { throw cause }

    m When "it runs"
    val failure = runCatching { deviceApiCall(call) }.exceptionOrNull()

    m Then "the same cancellation reaches the caller"
    assertSame(cause, failure)
  }
}
