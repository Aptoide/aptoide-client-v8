package com.aptoide.android.aptoidegames.newservices

import cm.aptoide.pt.feature_categories.data.CategoriesRepository
import cm.aptoide.pt.feature_categories.data.deviceapi.CategoriesResponse
import cm.aptoide.pt.feature_categories.data.deviceapi.CategoryResponse
import cm.aptoide.pt.feature_categories.data.deviceapi.DeviceApiCategoriesDataSource
import cm.aptoide.pt.feature_categories.data.deviceapi.DeviceApiCategoriesService
import cm.aptoide.pt.feature_categories.domain.AppCategory
import cm.aptoide.pt.feature_categories.domain.Category
import cm.aptoide.pt.test.gherkin.coScenario
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

// Aptoide Games browses games. The catalog of the new services holds apps as well, so only
// its game categories are offered. What the new services cannot answer stays on v7.
@ExperimentalCoroutinesApi
internal class PlayCategoriesRepositoryTest {

  @Test
  fun `The categories are the game ones of the new services`() = coScenario { scope ->
    m Given "a catalog with app and game categories"
    val (repository, v7) = repository(scope)

    m When "the categories are read"
    val categories = repository.getCategoriesList(url = "")

    m Then "only the game ones come back, from the new services"
    assertEquals(listOf("game_action", "game_puzzle"), categories.map { it.name })
    assertTrue(v7.calls.isEmpty())
  }

  @Test
  fun `A v7 url is not followed and the same categories come back`() = coScenario { scope ->
    m Given "the v7 url shared code knows the categories by"
    val (repository, v7) = repository(scope)
    val url = "https://ws75.aptoide.com/api/7/store/groups/get/store_id=1/group_name=games"

    m When "the categories are read"
    val categories = repository.getCategoriesList(url)

    m Then "the url is not followed"
    assertEquals(listOf("game_action", "game_puzzle"), categories.map { it.name })
    assertTrue(v7.calls.isEmpty())
  }

  @Test
  fun `The global categories are read from v7`() = coScenario { scope ->
    m Given "the url of the global categories"
    val (repository, v7) = repository(scope)

    m When "they are read"
    repository.getGlobalCategoriesList(url = "an-url")

    m Then "v7 is asked"
    assertEquals(listOf("global:an-url"), v7.calls)
  }

  @Test
  fun `The categories of installed apps are read from v7`() = coScenario { scope ->
    m Given "the packages of two installed apps"
    val (repository, v7) = repository(scope)

    m When "their categories are read"
    repository.getAppsCategories(listOf("a.b", "c.d"))

    m Then "v7 is asked, as the new services cannot tell"
    assertEquals(listOf("apps:a.b,c.d"), v7.calls)
  }

  private fun repository(
    scope: TestScope,
  ): Pair<PlayCategoriesRepository, FakeV7CategoriesRepository> {
    val service = object : DeviceApiCategoriesService {
      override suspend fun getCategories(variant: String) = CategoriesResponse(
        listOf(
          CategoryResponse(slug = "business", title = "Business", parent = "apps", appCount = 9),
          CategoryResponse(slug = "game_action", title = "Action", parent = "games", appCount = 5),
          CategoryResponse(slug = "game_puzzle", title = "Puzzle", parent = "games", appCount = 3),
        )
      )
    }
    val dataSource = DeviceApiCategoriesDataSource(
      service = service,
      variant = "google-certified",
      dispatcher = StandardTestDispatcher(scope.testScheduler),
    )
    val v7 = FakeV7CategoriesRepository()
    return PlayCategoriesRepository(v7, dataSource) to v7
  }
}

private class FakeV7CategoriesRepository : CategoriesRepository {

  val calls = mutableListOf<String>()

  override suspend fun getCategoriesList(url: String): List<Category> {
    calls += "store:$url"
    return emptyList()
  }

  override suspend fun getGlobalCategoriesList(url: String): List<Category> {
    calls += "global:$url"
    return emptyList()
  }

  override suspend fun getAppsCategories(packageNames: List<String>): List<AppCategory> {
    calls += "apps:${packageNames.joinToString(",")}"
    return emptyList()
  }
}
