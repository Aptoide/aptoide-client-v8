package cm.aptoide.pt.feature_search.di

import cm.aptoide.pt.feature_search.domain.repository.SearchRepository
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository.AutoCompleteResult
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository.PopularAppSearchResult
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository.SearchAppResult
import cm.aptoide.pt.test.gherkin.scenario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import java.util.Optional

// Every build shares this module, and only one of them binds an override. The ones that do not
// must keep getting the very same v7 repository they always had.
internal class BackendSelectionTest {

  @Test
  fun `A build that binds no override keeps the v7 repository`() = scenario {
    m Given "the v7 repository and no override"
    val v7 = StubSearchRepository()

    m When "the search repository is provided"
    val provided = RepositoryModule.provideSearchRepository(
      override = Optional.empty(),
      v7 = { v7 },
    )

    m Then "it is the v7 one"
    assertSame(v7, provided)
  }

  @Test
  fun `A build that binds an override gets it`() = scenario {
    m Given "the v7 repository and an override"
    val v7 = StubSearchRepository()
    val override = StubSearchRepository()

    m When "the search repository is provided"
    val provided = RepositoryModule.provideSearchRepository(
      override = Optional.of(override),
      v7 = { v7 },
    )

    m Then "it is the override"
    assertSame(override, provided)
  }
}

private class StubSearchRepository : SearchRepository {
  override fun searchApp(keyword: String): Flow<SearchAppResult> = emptyFlow()

  override fun getSearchHistory(): Flow<List<String>> = emptyFlow()

  override suspend fun addAppToSearchHistory(appName: String) = Unit

  override suspend fun removeAppFromSearchHistory(appName: String) = Unit

  override fun getAutoCompleteSuggestions(keyword: String): Flow<AutoCompleteResult> =
    emptyFlow()

  override fun getTopSearchedApps(): Flow<PopularAppSearchResult> = emptyFlow()
}
