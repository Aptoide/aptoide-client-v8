package com.aptoide.android.aptoidegames.newservices

import app.cash.turbine.test
import cm.aptoide.pt.device_api.error.DeviceApiException
import cm.aptoide.pt.feature_search.data.deviceapi.DeviceApiSuggestDataSource
import cm.aptoide.pt.feature_search.data.deviceapi.DeviceApiSuggestService
import cm.aptoide.pt.feature_search.data.deviceapi.SuggestResponse
import cm.aptoide.pt.feature_search.data.deviceapi.SuggestionResponse
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository.AutoCompleteResult
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository.PopularAppSearchResult
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository.SearchAppResult
import cm.aptoide.pt.test.gherkin.coScenario
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

// Searching and its suggestions move to the new services. What was searched before is kept on
// the device, and the popular searches only v7 knows, so both stay where they were.
@ExperimentalCoroutinesApi
internal class PlaySearchRepositoryTest {

  @Test
  fun `A search is answered by the new services`() = coScenario { scope ->
    m Given "new services that find two apps"
    val deviceApi = FakeDeviceApi(listing = listOf("a.first", "b.second"))
    val (repository, v7) = repository(scope, deviceApi)

    m When "apps are searched"
    val result = repository.searchApp("subway").first(scope)

    m Then "both come back, and the whole catalog was searched"
    val success = assertInstanceOf(SearchAppResult.Success::class.java, result)
    assertEquals(listOf("a.first", "b.second"), success.data.map { it.packageName })
    assertEquals(
      listOf(ListingRequest("subway", category = null, sort = null, limit = null, refresh = null)),
      deviceApi.listings
    )
    assertTrue(v7.calls.isEmpty())
  }

  @Test
  fun `A search the service fails is an error`() = coScenario { scope ->
    m Given "new services whose source is down"
    val failure = HttpException(
      Response.error<Any>(502, "{}".toResponseBody("application/problem+json".toMediaType()))
    )
    val (repository, _) = repository(scope, FakeDeviceApi(failure = failure))

    m When "apps are searched"
    val result = repository.searchApp("subway").first(scope)

    m Then "the result is the typed failure"
    val error = assertInstanceOf(SearchAppResult.Error::class.java, result)
    assertInstanceOf(DeviceApiException.Upstream::class.java, error.error)
  }

  @Test
  fun `A search without a connection is an error`() = coScenario { scope ->
    m Given "new services that cannot be reached"
    val failure = IOException("timeout")
    val (repository, _) = repository(scope, FakeDeviceApi(failure = failure))

    m When "apps are searched"
    val result = repository.searchApp("subway").first(scope)

    m Then "the result is the connectivity error, so that it can be told apart"
    val error = assertInstanceOf(SearchAppResult.Error::class.java, result)
    assertInstanceOf(IOException::class.java, error.error)
  }

  @Test
  fun `Suggestions are read from the new services`() = coScenario { scope ->
    m Given "new services suggesting a term"
    val (repository, v7) = repository(scope, suggestions = listOf("subway surfers"))

    m When "suggestions are read for what was typed"
    val result = repository.getAutoCompleteSuggestions("subw").first(scope)

    m Then "the term comes back"
    assertEquals(AutoCompleteResult.Success(listOf("subway surfers")), result)
    assertTrue(v7.calls.isEmpty())
  }

  @Test
  fun `The popular searches are read from v7`() = coScenario { scope ->
    m Given "a v7 that knows the popular searches"
    val (repository, v7) = repository(scope)

    m When "they are read"
    val result = repository.getTopSearchedApps().first(scope)

    m Then "v7 answers"
    assertEquals(PopularAppSearchResult.Success(listOf("popular")), result)
    assertEquals(listOf("top"), v7.calls)
  }

  @Test
  fun `The search history stays where it was`() = coScenario { scope ->
    m Given "a history kept on the device"
    val (repository, v7) = repository(scope)

    m When "it is read, added to and removed from"
    val history = repository.getSearchHistory().first(scope)
    repository.addAppToSearchHistory("subway")
    repository.removeAppFromSearchHistory("candy")

    m Then "the same history is used"
    assertEquals(listOf("searched before"), history)
    assertEquals(listOf("history", "add:subway", "remove:candy"), v7.calls)
  }

  private suspend fun <T> Flow<T>.first(scope: TestScope): T {
    var first: T? = null
    test {
      scope.testScheduler.advanceUntilIdle()
      first = awaitItem()
      cancelAndIgnoreRemainingEvents()
    }
    return first!!
  }

  private fun repository(
    scope: TestScope,
    deviceApi: FakeDeviceApi = FakeDeviceApi(),
    suggestions: List<String> = emptyList(),
  ): Pair<PlaySearchRepository, FakeV7SearchRepository> {
    val suggestService = object : DeviceApiSuggestService {
      override suspend fun suggest(q: String, limit: Int?, variant: String) =
        SuggestResponse(suggestions.map { SuggestionResponse(it) })
    }
    val v7 = FakeV7SearchRepository()
    val repository = PlaySearchRepository(
      v7 = v7,
      apps = deviceApi.dataSource(scope),
      suggestions = DeviceApiSuggestDataSource(
        service = suggestService,
        variant = "google-certified",
        dispatcher = StandardTestDispatcher(scope.testScheduler),
      ),
    )
    return repository to v7
  }
}

private class FakeV7SearchRepository : SearchRepository {

  val calls = mutableListOf<String>()

  override fun searchApp(keyword: String): Flow<SearchAppResult> {
    calls += "search:$keyword"
    return flowOf(SearchAppResult.Success(emptyList()))
  }

  override fun getSearchHistory(): Flow<List<String>> {
    calls += "history"
    return flowOf(listOf("searched before"))
  }

  override suspend fun addAppToSearchHistory(appName: String) {
    calls += "add:$appName"
  }

  override suspend fun removeAppFromSearchHistory(appName: String) {
    calls += "remove:$appName"
  }

  override fun getAutoCompleteSuggestions(keyword: String): Flow<AutoCompleteResult> {
    calls += "suggest:$keyword"
    return flowOf(AutoCompleteResult.Success(emptyList()))
  }

  override fun getTopSearchedApps(): Flow<PopularAppSearchResult> {
    calls += "top"
    return flowOf(PopularAppSearchResult.Success(listOf("popular")))
  }
}
