package cm.aptoide.pt.feature_search.data.deviceapi

import cm.aptoide.pt.test.gherkin.coScenario
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

// Suggestions are a convenience while typing. Not having them must never get in the way of
// the search itself, so any failure to read them reads as having none.
@ExperimentalCoroutinesApi
internal class DeviceApiSuggestDataSourceTest {

  @Test
  fun `Suggestions come back as terms, in order`() = coScenario { scope ->
    m Given "a service suggesting two terms"
    val service = FakeSuggestService(terms = listOf("subway surfers", "subway princess"))
    val dataSource = dataSource(service, scope)

    m When "suggestions are read for what was typed"
    val terms = dataSource.suggest(query = "subw")

    m Then "both terms come back, in order"
    assertEquals(listOf("subway surfers", "subway princess"), terms)
  }

  @Test
  fun `Suggestions are read within the catalog`() = coScenario { scope ->
    m Given "a data source for the google-certified catalog"
    val service = FakeSuggestService()
    val dataSource = dataSource(service, scope)

    m When "suggestions are read"
    dataSource.suggest(query = "subw")

    m Then "what was typed and the catalog are sent"
    assertEquals(listOf("subw" to "google-certified"), service.calls)
  }

  @Test
  fun `Blank terms are left out`() = coScenario { scope ->
    m Given "a service suggesting a blank term among real ones"
    val service = FakeSuggestService(terms = listOf("subway surfers", " ", null))
    val dataSource = dataSource(service, scope)

    m When "suggestions are read"
    val terms = dataSource.suggest(query = "subw")

    m Then "only the real one comes back"
    assertEquals(listOf("subway surfers"), terms)
  }

  @Test
  fun `A service failure reads as no suggestions`() = coScenario { scope ->
    m Given "a service that fails"
    val failure = HttpException(
      Response.error<Any>(502, "{}".toResponseBody("application/problem+json".toMediaType()))
    )
    val dataSource = dataSource(FakeSuggestService(failure = failure), scope)

    m When "suggestions are read"
    val terms = dataSource.suggest(query = "subw")

    m Then "there are none"
    assertEquals(emptyList<String>(), terms)
  }

  @Test
  fun `A connectivity failure reads as no suggestions`() = coScenario { scope ->
    m Given "a service that cannot be reached"
    val dataSource = dataSource(FakeSuggestService(failure = IOException("timeout")), scope)

    m When "suggestions are read"
    val terms = dataSource.suggest(query = "subw")

    m Then "there are none"
    assertEquals(emptyList<String>(), terms)
  }

  @Test
  fun `A cancellation is not swallowed`() = coScenario { scope ->
    m Given "a read that gets cancelled"
    val cause = CancellationException("typed another letter")
    val dataSource = dataSource(FakeSuggestService(failure = cause), scope)

    m When "suggestions are read"
    val thrown = runCatching { dataSource.suggest(query = "subw") }.exceptionOrNull()

    m Then "the cancellation reaches the caller"
    assertSame(cause, thrown)
  }

  private fun dataSource(service: DeviceApiSuggestService, scope: TestScope) =
    DeviceApiSuggestDataSource(
      service = service,
      variant = "google-certified",
      dispatcher = StandardTestDispatcher(scope.testScheduler),
    )
}

private class FakeSuggestService(
  private val terms: List<String?> = emptyList(),
  private val failure: Throwable? = null,
) : DeviceApiSuggestService {

  val calls = mutableListOf<Pair<String, String>>()

  override suspend fun suggest(q: String, limit: Int?, variant: String): SuggestResponse {
    calls += q to variant
    failure?.let { throw it }
    return SuggestResponse(terms.map { SuggestionResponse(it) })
  }
}
