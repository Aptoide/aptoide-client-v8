package cm.aptoide.pt.feature_apps.di

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppRepository
import cm.aptoide.pt.feature_apps.data.AppsListRepository
import cm.aptoide.pt.feature_apps.data.randomApp
import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import java.util.Optional

// Every build shares this module, and only one of them binds an override. The ones that do not
// must keep getting the very same v7 repository they always had.
internal class BackendSelectionTest {

  @Test
  fun `A build that binds no override keeps the v7 repository`() = scenario {
    m Given "the v7 repository and no override"
    val v7 = StubAppsListRepository()

    m When "the apps list repository is provided"
    val provided = RepositoryModule.providesAppsListRepository(
      override = Optional.empty(),
      v7 = { v7 },
    )

    m Then "it is the v7 one"
    assertSame(v7, provided)
  }

  @Test
  fun `A build that binds an override gets it`() = scenario {
    m Given "the v7 repository and an override"
    val v7 = StubAppsListRepository()
    val override = StubAppsListRepository()

    m When "the apps list repository is provided"
    val provided = RepositoryModule.providesAppsListRepository(
      override = Optional.of(override),
      v7 = { v7 },
    )

    m Then "it is the override"
    assertSame(override, provided)
  }

  @Test
  fun `A build that binds no app override keeps the v7 app repository`() = scenario {
    m Given "the v7 app repository and no override"
    val v7 = StubAppRepository()

    m When "the app repository is provided"
    val provided = RepositoryModule.providesAppRepository(override = Optional.empty(), v7 = { v7 })

    m Then "it is the v7 one"
    assertSame(v7, provided)
  }

  @Test
  fun `A build that binds an app override gets it`() = scenario {
    m Given "the v7 app repository and an override"
    val v7 = StubAppRepository()
    val override = StubAppRepository()

    m When "the app repository is provided"
    val provided = RepositoryModule.providesAppRepository(
      override = Optional.of(override),
      v7 = { v7 },
    )

    m Then "it is the override"
    assertSame(override, provided)
  }
}

private class StubAppRepository : AppRepository {
  override suspend fun getApp(packageName: String): App = randomApp

  override suspend fun getAppMeta(source: String): App = randomApp
}

private class StubAppsListRepository : AppsListRepository {
  override suspend fun getAppsList(url: String, bypassCache: Boolean): List<App> = emptyList()

  override suspend fun getAppsList(
    storeId: Long,
    groupId: Long,
    bypassCache: Boolean,
  ): List<App> = emptyList()

  override suspend fun getRecommended(path: String): List<App> = emptyList()

  override suspend fun getCategoryAppsList(categoryName: String): List<App> = emptyList()

  override suspend fun getAppVersions(packageName: String): List<App> = emptyList()

  override suspend fun getAppsList(packageNames: String): List<App> = emptyList()

  override suspend fun getSortedAppsList(sort: String, limit: Int): List<App> = emptyList()
}
