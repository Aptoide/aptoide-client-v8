package cm.aptoide.pt.feature_updates.data

import cm.aptoide.pt.feature_updates.domain.ApkData
import cm.aptoide.pt.test.gherkin.coScenario
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.IOException

// What the v7 updates did before they got an interface, pinned so the Play build's
// replacement can be told apart from a regression in what every other build keeps.
@ExperimentalCoroutinesApi
internal class AptoideUpdatesRepositoryTest {

  private val installed = ApkData(signature = "AB:CD", packageName = "a.b", versionCode = 1)

  @Test
  fun `Loading the updates returns and keeps those found`() = coScenario { scope ->
    m Given "a v7 that has an update for the installed app"
    val api = FakeUpdatesApi(listOf(Result.success(listOf(appJson("a.b", 2)))))
    val dao = FakeAppUpdateDao()
    val repository = repository(api, dao, scope)

    m When "the updates are loaded"
    val loaded = repository.loadUpdates(listOf(installed))

    m Then "the update is returned as an app and kept for the list"
    assertEquals(listOf("a.b" to 2), loaded.map { it.packageName to it.versionCode })
    val kept = repository.getUpdates().first()
    assertEquals(listOf("a.b" to 2), kept.map { it.packageName to it.versionCode })
  }

  @Test
  fun `The store this build reads is sent`() = coScenario { scope ->
    m Given "a v7 api"
    val api = FakeUpdatesApi(emptyList())
    val repository = repository(api, FakeAppUpdateDao(), scope)

    m When "the updates are loaded"
    repository.loadUpdates(listOf(installed))

    m Then "one request went out, with the installed app in it"
    assertEquals(listOf(listOf(installed)), api.requests.map { it.apksData })
  }

  @Test
  fun `Installed apps are asked about a hundred at a time`() = coScenario { scope ->
    m Given "two hundred and one installed apps"
    val api = FakeUpdatesApi(emptyList())
    val repository = repository(api, FakeAppUpdateDao(), scope)
    val many = List(201) { installed.copy(packageName = "app.$it") }

    m When "the updates are loaded"
    repository.loadUpdates(many)

    m Then "three requests went out"
    assertEquals(listOf(100, 100, 1), api.requests.map { it.apksData.size })
  }

  @Test
  fun `A failing chunk costs only its own updates`() = coScenario { scope ->
    m Given "a v7 that fails on the first chunk and answers the second"
    val api = FakeUpdatesApi(
      listOf(Result.failure(IOException("down")), Result.success(listOf(appJson("app.150", 2))))
    )
    val repository = repository(api, FakeAppUpdateDao(), scope)
    val many = List(150) { installed.copy(packageName = "app.$it") }

    m When "the updates are loaded"
    val loaded = repository.loadUpdates(many)

    m Then "the second chunk's update is still there, returned and kept"
    assertEquals(listOf("app.150"), loaded.map { it.packageName })
    assertEquals(listOf("app.150"), repository.getUpdates().first().map { it.packageName })
  }

  @Test
  fun `Removing an update forgets it by package`() = coScenario { scope ->
    m Given "two kept updates"
    val api = FakeUpdatesApi(
      listOf(Result.success(listOf(appJson("a.b", 2), appJson("c.d", 3))))
    )
    val repository = repository(api, FakeAppUpdateDao(), scope)
    repository.loadUpdates(listOf(installed))

    m When "one is removed"
    repository.remove(listOf("a.b"))

    m Then "only the other is kept"
    assertEquals(listOf("c.d"), repository.getUpdates().first().map { it.packageName })
  }

  private fun repository(api: FakeUpdatesApi, dao: FakeAppUpdateDao, scope: TestScope) =
    AptoideUpdatesRepository(
      appUpdateDao = dao,
      updatesApi = api,
      storeNameProvider = object : StoreNameProvider {
        override suspend fun getStoreName() = "a-store"
      },
      mapper = FakeAppsListMapper(),
      dispatcher = StandardTestDispatcher(scope.testScheduler),
    )
}
