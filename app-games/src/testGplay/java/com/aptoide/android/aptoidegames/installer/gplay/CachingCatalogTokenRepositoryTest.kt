package com.aptoide.android.aptoidegames.installer.gplay

import app.cash.turbine.test
import cm.aptoide.pt.test.gherkin.coScenario
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.IOException

@ExperimentalCoroutinesApi
internal class CachingCatalogTokenRepositoryTest {

  private val packageName = "com.example.game"

  private var nowMillis = 0L

  private fun TestScope.buildRepository(api: FakePlayInlineConfigApi) =
    CachingCatalogTokenRepository(
      origin = AptoideCatalogTokenRepository(api),
      scope = backgroundScope,
      now = { nowMillis },
    )

  @Test
  fun `Prefetch labels the app and the click path reuses the token`() = coScenario { scope ->
    m Given "an app with a catalog token behind the api"
    val api = FakePlayInlineConfigApi { PlayInlineConfigResponse("token-1", null) }
    val repository = scope.buildRepository(api)

    m When "the catalog status is prefetched"
    repository.observeIsPlayCatalog(packageName).test {
      assertFalse(awaitItem())
      repository.prefetch(packageName)
      scope.advanceUntilIdle()

      m Then "the app is labeled as Play catalog"
      assertTrue(awaitItem())

      m And "the click path returns the cached token without a second api call"
      assertEquals("token-1", repository.getCatalogToken(packageName))
      assertEquals(1, api.calls)
    }
  }

  @Test
  fun `Concurrent prefetch and click share a single fetch`() = coScenario { scope ->
    m Given "an app with a catalog token behind the api"
    val api = FakePlayInlineConfigApi { PlayInlineConfigResponse("token-1", null) }
    val repository = scope.buildRepository(api)

    m When "a prefetch is still in flight and the user clicks install"
    repository.prefetch(packageName)
    val token = repository.getCatalogToken(packageName)

    m Then "both share one api call and the click gets the token"
    assertEquals("token-1", token)
    assertEquals(1, api.calls)
  }

  @Test
  fun `Failed lookup is cached for labeling but ignored by the click path`() =
    coScenario { scope ->
      m Given "an api that fails"
      val api = FakePlayInlineConfigApi { throw IOException("network down") }
      val repository = scope.buildRepository(api)

      m When "the catalog status is prefetched twice"
      repository.prefetch(packageName)
      scope.advanceUntilIdle()
      repository.prefetch(packageName)
      scope.advanceUntilIdle()

      m Then "the negative result is cached and not refetched for labeling"
      repository.observeIsPlayCatalog(packageName).test { assertFalse(awaitItem()) }
      assertEquals(1, api.calls)

      m And "the click path still retries the fetch"
      assertNull(repository.getCatalogToken(packageName))
      assertEquals(2, api.calls)
    }

  @Test
  fun `Blank token labels the app as not in the catalog`() = coScenario { scope ->
    m Given "an api that returns a blank token"
    val api = FakePlayInlineConfigApi { PlayInlineConfigResponse(" ", null) }
    val repository = scope.buildRepository(api)

    m When "the catalog status is prefetched"
    repository.prefetch(packageName)
    scope.advanceUntilIdle()

    m Then "the app is not labeled as Play catalog"
    repository.observeIsPlayCatalog(packageName).test { assertFalse(awaitItem()) }
  }

  @Test
  fun `Expired token is refetched on click while the label is kept`() = coScenario { scope ->
    m Given "a token cached longer ago than the reuse TTL"
    val api = FakePlayInlineConfigApi { PlayInlineConfigResponse("token-1", null) }
    val repository = scope.buildRepository(api)
    repository.prefetch(packageName)
    scope.advanceUntilIdle()
    nowMillis += CachingCatalogTokenRepository.TOKEN_REUSE_TTL_MILLIS + 1

    m When "the user clicks install"
    api.response = { PlayInlineConfigResponse("token-2", null) }
    val token = repository.getCatalogToken(packageName)

    m Then "a fresh token is fetched"
    assertEquals("token-2", token)
    assertEquals(2, api.calls)

    m And "the app stays labeled as Play catalog"
    repository.observeIsPlayCatalog(packageName).test { assertTrue(awaitItem()) }
  }

  @Test
  fun `Overlay titles are Play catalog without any network`() = coScenario { scope ->
    m Given "the Roblox overlay-only title"
    val api = FakePlayInlineConfigApi { PlayInlineConfigResponse("token-1", null) }
    val repository = scope.buildRepository(api)

    m When "the catalog status is observed and prefetched"
    repository.prefetch("com.roblox.client")
    scope.advanceUntilIdle()

    m Then "it is labeled as Play catalog with zero api calls"
    repository.observeIsPlayCatalog("com.roblox.client").test { assertTrue(awaitItem()) }
    assertEquals(0, api.calls)
  }
}

private class FakePlayInlineConfigApi(
  var response: (String) -> PlayInlineConfigResponse,
) : PlayInlineConfigApi {

  var calls = 0
    private set

  override suspend fun getPlayInlineConfig(packageName: String): PlayInlineConfigResponse {
    calls++
    return response(packageName)
  }
}
