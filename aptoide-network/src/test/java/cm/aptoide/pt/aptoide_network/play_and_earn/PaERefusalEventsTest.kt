package cm.aptoide.pt.aptoide_network.play_and_earn

import cm.aptoide.pt.test.gherkin.coScenario
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

// AND-951: every refusal read by the crash reporting filter is told to whoever listens, so the
// restricted account is learnt from any Play & Earn call without each caller wiring it.
@OptIn(ExperimentalCoroutinesApi::class)
internal class PaERefusalEventsTest {

  @Test
  fun `A refusal read by the filter is told`() = coScenario { scope ->
    m Given "someone listening to the refusals"
    val heard = mutableListOf<PaERefusal>()
    scope.backgroundScope.launch(UnconfinedTestDispatcher(scope.testScheduler)) {
      PaERefusalEvents.refusals.collect { heard += it }
    }

    m When "an account restriction goes through the crash reporting filter"
    reportUnlessPaERefusal(httpError(403, """{"detail":{"code":"ACCOUNT_RESTRICTED"}}""")) {}

    m Then "the listener hears it"
    assertEquals(listOf(PaERefusal(PaERefusalCode.ACCOUNT_RESTRICTED)), heard)
  }

  @Test
  fun `A failure that is not a refusal is not told`() = coScenario { scope ->
    m Given "someone listening to the refusals"
    val heard = mutableListOf<PaERefusal>()
    scope.backgroundScope.launch(UnconfinedTestDispatcher(scope.testScheduler)) {
      PaERefusalEvents.refusals.collect { heard += it }
    }

    m When "a network failure and a server error go through the filter"
    reportUnlessPaERefusal(IOException("offline")) {}
    reportUnlessPaERefusal(httpError(500, """{"detail":"Internal error"}""")) {}

    m Then "the listener hears nothing"
    assertEquals(emptyList<PaERefusal>(), heard)
  }

  private fun httpError(code: Int, body: String) = HttpException(
    Response.error<Any>(code, body.toResponseBody("application/json".toMediaType()))
  )
}
