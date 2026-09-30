package com.aptoide.android.aptoidegames.newservices

import cm.aptoide.pt.feature_categories.data.deviceapi.CategoriesResponse
import cm.aptoide.pt.feature_categories.data.deviceapi.CategoryResponse
import cm.aptoide.pt.feature_categories.data.deviceapi.DeviceApiCategoriesDataSource
import cm.aptoide.pt.feature_categories.data.deviceapi.DeviceApiCategoriesService
import cm.aptoide.pt.feature_home.domain.WidgetActionType
import cm.aptoide.pt.feature_home.domain.WidgetLayout
import cm.aptoide.pt.feature_home.domain.WidgetType
import cm.aptoide.pt.test.gherkin.coScenario
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

// The new services have no home for this build, so the Play build composes one: the AppCoins
// games v7 knows, the top games, one row per game genre, the categories and the user's games.
// Each row is a widget shared code turns into a bundle, so what is pinned here is what shared
// code reads: the order, the types, the urls, and the actions that offer to see all.
@ExperimentalCoroutinesApi
internal class PlayHomeWidgetsRepositoryTest {

  private val categories = listOf(
    CategoryResponse(slug = "business", title = "Business", parent = "apps", appCount = 9),
    CategoryResponse(slug = "game_action", title = "Action", parent = "games", appCount = 5),
    CategoryResponse(slug = "game_puzzle", title = "Puzzle", parent = "games", appCount = 3),
    CategoryResponse(slug = "game_card", title = "Card", parent = "games", appCount = 0),
  )

  @Test
  fun `The home is composed in a fixed order`() = coScenario { scope ->
    m Given "a catalog with two game genres"
    val repository = repository(scope, categories)

    m When "the home widgets are read"
    val widgets = repository.getStoreWidgets()

    m Then "the AppCoins games come first, then the top games, the genres, categories, my games"
    assertEquals(
      listOf(
        "apps-group-appcoins",
        "apps-group-top-games",
        "apps-group-genre-game_action",
        "apps-group-genre-game_puzzle",
        "categories",
        "my-games",
      ),
      widgets.map { it.tag }
    )
  }

  @Test
  fun `The AppCoins games are listed from the v7 store`() = coScenario { scope ->
    m Given "the store this build reads"
    val repository = repository(scope, categories)

    m When "the home widgets are read"
    val appcoins = repository.getStoreWidgets().first()

    m Then "the row is a carousel of the v7 store's apps, which carry their flags and files"
    assertEquals(WidgetType.APPS_GROUP, appcoins.type)
    assertEquals(WidgetLayout.CAROUSEL, appcoins.layout)
    assertEquals("listApps/store_name=a-store/limit=24", appcoins.view)
  }

  @Test
  fun `The top games are listed from the new services`() = coScenario { scope ->
    m Given "a catalog"
    val repository = repository(scope, categories)

    m When "the home widgets are read"
    val top = repository.getStoreWidgets()[1]

    m Then "the row is a carousel of the games by downloads"
    assertEquals(WidgetType.APPS_GROUP, top.type)
    assertEquals(WidgetLayout.CAROUSEL, top.layout)
    assertEquals("newservices/listApps/category=games/sort=downloads/limit=24", top.view)
  }

  @Test
  fun `Each game genre gets a grid of its apps titled as the service says`() = coScenario {
      scope ->
    m Given "a catalog with the action genre"
    val repository = repository(scope, categories)

    m When "the home widgets are read"
    val action = repository.getStoreWidgets().first { it.tag == "apps-group-genre-game_action" }

    m Then "the row is a grid of nine of its games, titled Action"
    assertEquals("Action", action.title)
    assertEquals(WidgetType.APPS_GROUP, action.type)
    assertEquals(WidgetLayout.GRID, action.layout)
    assertEquals("newservices/listApps/category=game_action/sort=downloads/limit=9", action.view)
  }

  @Test
  fun `App categories and empty genres get no row`() = coScenario { scope ->
    m Given "a catalog with an app category and an empty game genre"
    val repository = repository(scope, categories)

    m When "the home widgets are read"
    val tags = repository.getStoreWidgets().map { it.tag }

    m Then "neither gets a row"
    assertTrue(tags.none { "business" in it || "game_card" in it })
  }

  @Test
  fun `Every apps row offers to see all of the same listing`() = coScenario { scope ->
    m Given "a catalog"
    val repository = repository(scope, categories)

    m When "the home widgets are read"
    val appsRows = repository.getStoreWidgets().filter { it.type == WidgetType.APPS_GROUP }

    m Then "each has a see-all action, tagged as shared code expects, on the row's own listing"
    appsRows.forEach { row ->
      val more = row.action.orEmpty().single { it.type == WidgetActionType.BUTTON }
      assertEquals("${row.tag}-more", more.tag)
      assertEquals(row.view, more.url)
      assertTrue("listApps/" in more.url)
    }
  }

  @Test
  fun `The categories row lists the game genres`() = coScenario { scope ->
    m Given "a catalog"
    val repository = repository(scope, categories)

    m When "the home widgets are read"
    val row = repository.getStoreWidgets().first { it.tag == "categories" }

    m Then "it is a grid of store groups, whose url names the games"
    assertEquals(WidgetType.STORE_GROUPS, row.type)
    assertEquals(WidgetLayout.GRID, row.layout)
    assertEquals("newservices/categories/parent=games", row.view)
  }

  @Test
  fun `My games needs nothing from any backend`() = coScenario { scope ->
    m Given "a catalog"
    val repository = repository(scope, categories)

    m When "the home widgets are read"
    val myGames = repository.getStoreWidgets().last()

    m Then "it is the local row"
    assertEquals(WidgetType.MY_GAMES, myGames.type)
    assertEquals(null, myGames.view)
  }

  @Test
  fun `Without a connection the home cannot be read`() = coScenario { scope ->
    m Given "a catalog that cannot be reached"
    val repository = repository(scope, failure = IOException("timeout"))

    m When "the home widgets are read"
    val failure = runCatching { repository.getStoreWidgets() }.exceptionOrNull()

    m Then "the failure reaches the caller, so the no-connection view shows"
    assertInstanceOf(IOException::class.java, failure)
  }

  @Test
  fun `When the genres cannot be read the home keeps its fixed rows`() = coScenario { scope ->
    m Given "a catalog whose source is down"
    val failure = HttpException(
      Response.error<Any>(502, "{}".toResponseBody("application/problem+json".toMediaType()))
    )
    val repository = repository(scope, failure = failure)

    m When "the home widgets are read"
    val tags = repository.getStoreWidgets().map { it.tag }

    m Then "the rows that need no genre are still there"
    assertEquals(
      listOf("apps-group-appcoins", "apps-group-top-games", "categories", "my-games"),
      tags
    )
  }

  @Test
  fun `Row tags are stable across reads`() = coScenario { scope ->
    m Given "a catalog"
    val repository = repository(scope, categories)

    m When "the home widgets are read twice"
    val first = repository.getStoreWidgets().map { it.tag }
    val second = repository.getStoreWidgets().map { it.tag }

    m Then "the tags are the same, as shared code caches urls by them"
    assertEquals(first, second)
  }

  private fun repository(
    scope: TestScope,
    categories: List<CategoryResponse> = emptyList(),
    failure: Throwable? = null,
  ): PlayHomeWidgetsRepository {
    val service = object : DeviceApiCategoriesService {
      override suspend fun getCategories(variant: String): CategoriesResponse {
        failure?.let { throw it }
        return CategoriesResponse(categories)
      }
    }
    return PlayHomeWidgetsRepository(
      storeName = "a-store",
      categories = DeviceApiCategoriesDataSource(
        service = service,
        variant = "google-certified",
        dispatcher = StandardTestDispatcher(scope.testScheduler),
      ),
    )
  }
}
