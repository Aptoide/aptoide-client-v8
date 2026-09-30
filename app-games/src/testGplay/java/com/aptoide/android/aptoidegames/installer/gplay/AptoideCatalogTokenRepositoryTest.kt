package com.aptoide.android.aptoidegames.installer.gplay

import cm.aptoide.pt.test.gherkin.coScenario
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

// Whether an app has a Play catalog token decides whether it is offered at all on this build,
// so "not in the catalog" and "could not tell" must never be confused: one hides the install,
// the other must not.
@ExperimentalCoroutinesApi
internal class AptoideCatalogTokenRepositoryTest {

  private val packageName = "com.example.game"

  @Test
  fun `A token answers as a token`() = coScenario {
    m Given "an api answering with a token"
    val repository =
      AptoideCatalogTokenRepository(api { PlayInlineConfigResponse("token-1", null) })

    m When "the catalog is looked up"
    val lookup = repository.lookup(packageName)

    m Then "the token is the answer"
    assertEquals(CatalogLookup.Token("token-1"), lookup)
  }

  @Test
  fun `An unknown app is not in the catalog`() = coScenario {
    m Given "an api answering that the app is unknown"
    val repository = AptoideCatalogTokenRepository(api { throw httpError(404) })

    m When "the catalog is looked up"
    val lookup = repository.lookup(packageName)

    m Then "the app is not in the catalog"
    assertEquals(CatalogLookup.NotInCatalog, lookup)
  }

  @Test
  fun `A blank token is not in the catalog`() = coScenario {
    m Given "an api answering with a blank token"
    val repository = AptoideCatalogTokenRepository(api { PlayInlineConfigResponse(" ", null) })

    m When "the catalog is looked up"
    val lookup = repository.lookup(packageName)

    m Then "the app is not in the catalog, as there is nothing to install it with"
    assertEquals(CatalogLookup.NotInCatalog, lookup)
  }

  @Test
  fun `A connectivity error could not tell`() = coScenario {
    m Given "an api that cannot be reached"
    val repository = AptoideCatalogTokenRepository(api { throw IOException("network down") })

    m When "the catalog is looked up"
    val lookup = repository.lookup(packageName)

    m Then "the lookup failed, which is not the same as not being in the catalog"
    assertEquals(CatalogLookup.Failed, lookup)
  }

  @Test
  fun `A service error could not tell`() = coScenario {
    m Given "an api that fails on its side"
    val repository = AptoideCatalogTokenRepository(api { throw httpError(500) })

    m When "the catalog is looked up"
    val lookup = repository.lookup(packageName)

    m Then "the lookup failed"
    assertEquals(CatalogLookup.Failed, lookup)
  }

  @Test
  fun `A slow api could not tell`() = coScenario {
    m Given "an api slower than the fetch timeout"
    val repository = AptoideCatalogTokenRepository(
      api {
        delay(6_000)
        PlayInlineConfigResponse("token-1", null)
      }
    )

    m When "the catalog is looked up"
    val lookup = repository.lookup(packageName)

    m Then "the lookup failed"
    assertEquals(CatalogLookup.Failed, lookup)
  }

  private fun api(response: suspend (String) -> PlayInlineConfigResponse) =
    object : PlayInlineConfigApi {
      override suspend fun getPlayInlineConfig(packageName: String) = response(packageName)
    }

  private fun httpError(code: Int) = HttpException(
    Response.error<Any>(code, "{}".toResponseBody("application/problem+json".toMediaType()))
  )
}
