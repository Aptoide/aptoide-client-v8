package cm.aptoide.pt.feature_categories.data.deviceapi

import cm.aptoide.pt.device_api.error.DeviceApiException
import cm.aptoide.pt.test.gherkin.coScenario
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response

// The device API answers with every category of the catalog at once, apps and games alike. A
// category is browsed by its slug, so the slug is what has to end up as the category's name.
@ExperimentalCoroutinesApi
internal class DeviceApiCategoriesDataSourceTest {

  private val all = listOf(
    CategoryResponse(slug = "business", title = "Business", parent = "apps", appCount = 9),
    CategoryResponse(slug = "game_action", title = "Action", parent = "games", appCount = 5),
    CategoryResponse(slug = "game_puzzle", title = "Puzzle", parent = "games", appCount = 3),
  )

  @Test
  fun `Only the categories of the asked parent are returned`() = coScenario { scope ->
    m Given "a catalog with app and game categories"
    val dataSource = dataSource(FakeCategoriesService(all), scope)

    m When "the game categories are read"
    val categories = dataSource.categories(parent = "games")

    m Then "only those come back, in the order the service sent them"
    assertEquals(listOf("game_action", "game_puzzle"), categories.map { it.name })
  }

  @Test
  fun `A category is named by its slug and titled as the service says`() = coScenario { scope ->
    m Given "a catalog with the action category"
    val dataSource = dataSource(FakeCategoriesService(all), scope)

    m When "the game categories are read"
    val action = dataSource.categories(parent = "games").first()

    m Then "the slug is the name it is browsed by, and the title is what is shown"
    assertEquals("game_action", action.name)
    assertEquals("Action", action.title)
  }

  @Test
  fun `Categories get distinct ids`() = coScenario { scope ->
    m Given "a catalog with two game categories"
    val dataSource = dataSource(FakeCategoriesService(all), scope)

    m When "the game categories are read"
    val ids = dataSource.categories(parent = "games").map { it.id }

    m Then "their ids differ, as lists key on them"
    assertNotEquals(ids[0], ids[1])
  }

  @Test
  fun `A category known to be empty is left out`() = coScenario { scope ->
    m Given "a catalog where one game category has no apps and another does not say"
    val service = FakeCategoriesService(
      listOf(
        CategoryResponse(slug = "game_card", title = "Card", parent = "games", appCount = 0),
        CategoryResponse(slug = "game_word", title = "Word", parent = "games", appCount = null),
      )
    )
    val dataSource = dataSource(service, scope)

    m When "the game categories are read"
    val categories = dataSource.categories(parent = "games")

    m Then "the empty one is left out and the unknown one is kept"
    assertEquals(listOf("game_word"), categories.map { it.name })
  }

  @Test
  fun `A category without a slug is left out`() = coScenario { scope ->
    m Given "a catalog where one game category has no slug"
    val service = FakeCategoriesService(
      listOf(CategoryResponse(slug = null, title = "Nameless", parent = "games", appCount = 4))
    )
    val dataSource = dataSource(service, scope)

    m When "the game categories are read"
    val categories = dataSource.categories(parent = "games")

    m Then "it is left out, as it could not be browsed"
    assertEquals(emptyList<String>(), categories.map { it.name })
  }

  @Test
  fun `A slug that is not a plain identifier is left out`() = coScenario { scope ->
    m Given "a catalog where one game category's slug could not be used as an identifier"
    val service = FakeCategoriesService(
      listOf(
        CategoryResponse(slug = "game/action=1", title = "Odd", parent = "games", appCount = 4),
        CategoryResponse(slug = "game_word", title = "Word", parent = "games", appCount = 4),
      )
    )
    val dataSource = dataSource(service, scope)

    m When "the game categories are read"
    val categories = dataSource.categories(parent = "games")

    m Then "only the plain one comes back, as slugs become urls and tags"
    assertEquals(listOf("game_word"), categories.map { it.name })
  }

  @Test
  fun `A slug listed twice comes back once`() = coScenario { scope ->
    m Given "a catalog listing the same game category twice"
    val service = FakeCategoriesService(
      listOf(
        CategoryResponse(slug = "game_word", title = "Word", parent = "games", appCount = 4),
        CategoryResponse(slug = "game_word", title = "Word", parent = "games", appCount = 4),
      )
    )
    val dataSource = dataSource(service, scope)

    m When "the game categories are read"
    val categories = dataSource.categories(parent = "games")

    m Then "it comes back once, as rows key on it"
    assertEquals(listOf("game_word"), categories.map { it.name })
  }

  @Test
  fun `A category without a title is titled by its slug`() = coScenario { scope ->
    m Given "a catalog where one game category has a blank title"
    val service = FakeCategoriesService(
      listOf(CategoryResponse(slug = "game_word", title = " ", parent = "games", appCount = 4))
    )
    val dataSource = dataSource(service, scope)

    m When "the game categories are read"
    val category = dataSource.categories(parent = "games").single()

    m Then "the slug stands in for the title"
    assertEquals("game_word", category.title)
  }

  @Test
  fun `An answer without categories reads as none`() = coScenario { scope ->
    m Given "a service answering with no list at all"
    val dataSource = dataSource(FakeCategoriesService(categories = null), scope)

    m When "the game categories are read"
    val categories = dataSource.categories(parent = "games")

    m Then "there are none"
    assertEquals(emptyList<String>(), categories.map { it.name })
  }

  @Test
  fun `The categories are read within the catalog`() = coScenario { scope ->
    m Given "a data source for the google-certified catalog"
    val service = FakeCategoriesService(all)
    val dataSource = dataSource(service, scope)

    m When "the game categories are read"
    dataSource.categories(parent = "games")

    m Then "the catalog is sent"
    assertEquals(listOf("google-certified"), service.variants)
  }

  @Test
  fun `A service failure reaches the caller typed`() = coScenario { scope ->
    m Given "a service whose source is down"
    val failure = HttpException(
      Response.error<Any>(502, "{}".toResponseBody("application/problem+json".toMediaType()))
    )
    val dataSource = dataSource(FakeCategoriesService(all, failure), scope)

    m When "the game categories are read"
    val thrown = runCatching { dataSource.categories(parent = "games") }.exceptionOrNull()

    m Then "the failure says the source is down"
    assertInstanceOf(DeviceApiException.Upstream::class.java, thrown)
  }

  private fun dataSource(service: DeviceApiCategoriesService, scope: TestScope) =
    DeviceApiCategoriesDataSource(
      service = service,
      variant = "google-certified",
      dispatcher = StandardTestDispatcher(scope.testScheduler),
    )
}

private class FakeCategoriesService(
  private val categories: List<CategoryResponse>?,
  private val failure: Throwable? = null,
) : DeviceApiCategoriesService {

  val variants = mutableListOf<String>()

  override suspend fun getCategories(variant: String): CategoriesResponse {
    variants += variant
    failure?.let { throw it }
    return CategoriesResponse(categories)
  }
}
