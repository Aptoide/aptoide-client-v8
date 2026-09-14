package com.aptoide.android.aptoidegames.installer.gplay

import app.cash.turbine.test
import cm.aptoide.pt.test.gherkin.coScenario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
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

  // A standalone scope on the test scheduler, mirroring the production shape
  // (CoroutineScope(SupervisorJob() + Dispatchers.IO)); empirically, backgroundScope
  // tasks were not executed by advanceUntilIdle here, only while the body was suspended
  private fun TestScope.buildRepository(api: FakePlayInlineConfigApi) =
    buildRepository(AptoideCatalogTokenRepository(api))

  private fun TestScope.buildRepository(origin: CatalogTokenRepository) =
    CachingCatalogTokenRepository(
      origin = origin,
      scope = CoroutineScope(StandardTestDispatcher(testScheduler) + SupervisorJob()),
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
  fun `Prefetch and click share the in-flight fetch`() = coScenario { scope ->
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

    m And "the click path treats it as a negative and retries the fetch"
    assertNull(repository.getCatalogToken(packageName))
    assertEquals(2, api.calls)
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

    m But "the fresh token is cached for the next click"
    assertEquals("token-2", repository.getCatalogToken(packageName))
    assertEquals(2, api.calls)
  }

  @Test
  fun `Overlay titles are Play catalog without any network`() = coScenario { scope ->
    m Given "all overlay-only titles"
    val overlayPackages =
      listOf("com.roblox.client", "com.dts.freefireth", "com.dts.freefiremax")
    val api = FakePlayInlineConfigApi { PlayInlineConfigResponse("token-1", null) }
    val repository = scope.buildRepository(api)

    m When "each catalog status is observed and prefetched"
    overlayPackages.forEach { repository.prefetch(it) }
    scope.advanceUntilIdle()

    m Then "each is labeled as Play catalog with zero api calls"
    overlayPackages.forEach { overlayPackage ->
      repository.observeIsPlayCatalog(overlayPackage).test {
        assertTrue(awaitItem())
        awaitComplete()
      }
    }
    assertEquals(0, api.calls)
  }

  @Test
  fun `Successful click after a failed prefetch flips the label`() = coScenario { scope ->
    m Given "a negative entry cached by a failed prefetch"
    val api = FakePlayInlineConfigApi { throw IOException("network down") }
    val repository = scope.buildRepository(api)
    repository.prefetch(packageName)
    scope.advanceUntilIdle()

    m When "the api recovers and the user clicks install"
    api.response = { PlayInlineConfigResponse("token-1", null) }
    val token = repository.getCatalogToken(packageName)

    m Then "the click gets the token and the label flips to Play catalog"
    assertEquals("token-1", token)
    repository.observeIsPlayCatalog(packageName).test { assertTrue(awaitItem()) }

    m And "the next click reuses the cached token"
    assertEquals("token-1", repository.getCatalogToken(packageName))
    assertEquals(2, api.calls)
  }

  @Test
  fun `Stale entries are refetched by prefetch`() = coScenario { scope ->
    m Given "a negative entry older than the reuse TTL"
    val api = FakePlayInlineConfigApi { throw IOException("network down") }
    val repository = scope.buildRepository(api)
    repository.prefetch(packageName)
    scope.advanceUntilIdle()
    nowMillis += CachingCatalogTokenRepository.TOKEN_REUSE_TTL_MILLIS + 1

    m When "the api recovers and a surface prefetches again"
    api.response = { PlayInlineConfigResponse("token-1", null) }
    repository.prefetch(packageName)
    scope.advanceUntilIdle()

    m Then "the label recovers without any install click"
    repository.observeIsPlayCatalog(packageName).test { assertTrue(awaitItem()) }
    assertEquals(2, api.calls)

    m But "a fresh entry is not refetched by prefetch"
    repository.prefetch(packageName)
    scope.advanceUntilIdle()
    assertEquals(2, api.calls)
  }

  @Test
  fun `A throwing origin is treated as a negative result`() = coScenario { scope ->
    m Given "an origin that throws instead of returning null"
    val origin = object : CatalogTokenRepository {
      override suspend fun getCatalogToken(packageName: String): String? =
        error("misbehaving origin")
    }
    val repository = scope.buildRepository(origin)

    m When "the user clicks install"
    val token = repository.getCatalogToken(packageName)

    m Then "the click falls back to the regular path instead of crashing"
    assertNull(token)

    m And "the failure is cached as a negative for labeling"
    repository.observeIsPlayCatalog(packageName).test { assertFalse(awaitItem()) }
  }

  @Test
  fun `Token reuse stops exactly at the TTL boundary`() = coScenario { scope ->
    m Given "a token cached exactly TTL milliseconds ago"
    val api = FakePlayInlineConfigApi { PlayInlineConfigResponse("token-1", null) }
    val repository = scope.buildRepository(api)
    repository.prefetch(packageName)
    scope.advanceUntilIdle()

    m When "clicks happen just inside and exactly at the boundary"
    nowMillis += CachingCatalogTokenRepository.TOKEN_REUSE_TTL_MILLIS - 1
    val insideToken = repository.getCatalogToken(packageName)
    val callsInside = api.calls
    nowMillis += 1
    val boundaryToken = repository.getCatalogToken(packageName)

    m Then "the click inside the window reuses and the boundary click refetches"
    assertEquals("token-1", insideToken)
    assertEquals(1, callsInside)
    assertEquals("token-1", boundaryToken)
    assertEquals(2, api.calls)
  }

  @Test
  fun `Slow fetches time out into the regular path`() = coScenario { scope ->
    m Given "an api slower than the fetch timeout"
    val api = FakePlayInlineConfigApi {
      delay(6_000)
      PlayInlineConfigResponse("token-1", null)
    }
    val repository = scope.buildRepository(api)

    m When "the user clicks install"
    val token = repository.getCatalogToken(packageName)

    m Then "the click falls back to the regular path"
    assertNull(token)

    m And "the timeout is cached as a negative for labeling"
    repository.observeIsPlayCatalog(packageName).test { assertFalse(awaitItem()) }
  }
}

private class FakePlayInlineConfigApi(
  var response: suspend (String) -> PlayInlineConfigResponse,
) : PlayInlineConfigApi {

  var calls = 0
    private set

  override suspend fun getPlayInlineConfig(packageName: String): PlayInlineConfigResponse {
    calls++
    return response(packageName)
  }
}
