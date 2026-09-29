package com.aptoide.android.aptoidegames.newservices

import cm.aptoide.pt.feature_apps.domain.AppOrigin
import cm.aptoide.pt.test.gherkin.coScenario
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

// The Play build reads its catalog from the new services and keeps v7 for what only v7 serves:
// the AppCoins listings and whatever is addressed the v7 way. Each read goes to exactly one of
// the two, and which one is decided by what is asked, never by trying both.
@ExperimentalCoroutinesApi
internal class PlayAppsListRepositoryTest {

  @Test
  fun `A listing of the new services is read from them`() = coScenario { scope ->
    m Given "the url of a row of nine action games"
    val (repository, deviceApi, v7) = repository(scope)
    val url = "newservices/listApps/category=game_action/sort=downloads/limit=9"

    m When "its apps are read"
    val apps = repository.getAppsList(url)

    m Then "the new services are asked for that listing and v7 is not"
    assertEquals(
      listOf(ListingRequest(null, "game_action", "downloads", limit = 9, refresh = null)),
      deviceApi.listings
    )
    assertTrue(v7.calls.isEmpty())
    assertEquals(listOf(AppOrigin.DEVICE_API), apps.map { it.origin })
  }

  @Test
  fun `Bypassing the cache reaches the new services`() = coScenario { scope ->
    m Given "the url of a listing of the new services"
    val (repository, deviceApi, _) = repository(scope)

    m When "its apps are read bypassing the cache"
    repository.getAppsList("newservices/listApps/category=games", bypassCache = true)

    m Then "a fresh listing is asked for"
    assertEquals(1, deviceApi.listings.single().refresh)
  }

  @Test
  fun `A v7 url is read from v7`() = coScenario { scope ->
    m Given "the url of the v7 listing of a store"
    val (repository, deviceApi, v7) = repository(scope)
    val url = "https://ws75.aptoide.com/api/7/listApps/store_name=aptoidegames-play-us/limit=24"

    m When "its apps are read"
    val apps = repository.getAppsList(url)

    m Then "v7 is asked and the new services are not"
    assertEquals(listOf("url:$url:false"), v7.calls)
    assertTrue(deviceApi.listings.isEmpty())
    assertEquals(listOf(v7.app), apps)
  }

  @Test
  fun `A store group is read from v7`() = coScenario { scope ->
    m Given "a store and a group id, which only v7 knows"
    val (repository, deviceApi, v7) = repository(scope)

    m When "the apps of the group are read"
    repository.getAppsList(storeId = 15, groupId = 14169744)

    m Then "v7 is asked and the new services are not"
    assertEquals(listOf("group:15:14169744:false"), v7.calls)
    assertTrue(deviceApi.listings.isEmpty())
  }

  @Test
  fun `The apps of a category are read from the new services`() = coScenario { scope ->
    m Given "a category of the new services"
    val (repository, deviceApi, v7) = repository(scope)

    m When "its apps are read"
    repository.getCategoryAppsList("game_puzzle")

    m Then "a full page of it is asked for, by downloads"
    assertEquals(
      listOf(ListingRequest(null, "game_puzzle", "downloads", limit = 50, refresh = null)),
      deviceApi.listings
    )
    assertTrue(v7.calls.isEmpty())
  }

  @Test
  fun `The apps similar to one are read from the new services`() = coScenario { scope ->
    m Given "an app addressed by its package"
    val (repository, deviceApi, v7) = repository(scope)

    m When "the apps similar to it are read"
    repository.getRecommended("package_name=com.kiloo.subwaysurf")

    m Then "the new services are asked for its related apps"
    assertEquals(listOf("com.kiloo.subwaysurf"), deviceApi.related)
    assertTrue(v7.calls.isEmpty())
  }

  @Test
  fun `The AppCoins apps similar to one are read from v7`() = coScenario { scope ->
    m Given "the AppCoins section of the apps similar to one"
    val (repository, deviceApi, v7) = repository(scope)

    m When "they are read"
    repository.getRecommended("package_name=com.kiloo.subwaysurf/section=appc")

    m Then "v7 is asked, as only it knows that section"
    assertEquals(listOf("recommended:package_name=com.kiloo.subwaysurf/section=appc"), v7.calls)
    assertTrue(deviceApi.related.isEmpty())
  }

  @Test
  fun `An AppCoins sort is read from v7`() = coScenario { scope ->
    m Given "the sort of the bonus listing"
    val (repository, deviceApi, v7) = repository(scope)

    m When "the apps are read by it"
    repository.getSortedAppsList(sort = "appc_billing_pdownloads", limit = 20)

    m Then "v7 is asked, as only it sorts by AppCoins billing"
    assertEquals(listOf("sorted:appc_billing_pdownloads:20"), v7.calls)
    assertTrue(deviceApi.listings.isEmpty())
  }

  @Test
  fun `Any other sort is read from the games of the new services`() = coScenario { scope ->
    m Given "the v7 name of the trending sort"
    val (repository, deviceApi, v7) = repository(scope)

    m When "the apps are read by it"
    repository.getSortedAppsList(sort = "trending60d", limit = 9)

    m Then "the games are asked for by the matching sort of the new services"
    assertEquals(
      listOf(ListingRequest(null, "games", "trending", limit = 9, refresh = null)),
      deviceApi.listings
    )
    assertTrue(v7.calls.isEmpty())
  }

  @Test
  fun `The versions of an app are read from v7`() = coScenario { scope ->
    m Given "an app"
    val (repository, deviceApi, v7) = repository(scope)

    m When "its versions are read"
    repository.getAppVersions("com.kiloo.subwaysurf")

    m Then "v7 is asked"
    assertEquals(listOf("versions:com.kiloo.subwaysurf"), v7.calls)
    assertTrue(deviceApi.listings.isEmpty())
  }

  @Test
  fun `Apps named by package are read from v7`() = coScenario { scope ->
    m Given "a list of packages, as promotions name their apps"
    val (repository, deviceApi, v7) = repository(scope)

    m When "they are read"
    repository.getAppsList(packageNames = "a.b,c.d")

    m Then "v7 is asked"
    assertEquals(listOf("packages:a.b,c.d"), v7.calls)
    assertTrue(deviceApi.listings.isEmpty())
  }

  private fun repository(
    scope: TestScope,
  ): Triple<PlayAppsListRepository, FakeDeviceApi, FakeV7AppsListRepository> {
    val deviceApi = FakeDeviceApi()
    val v7 = FakeV7AppsListRepository()
    return Triple(PlayAppsListRepository(v7, deviceApi.dataSource(scope)), deviceApi, v7)
  }
}
