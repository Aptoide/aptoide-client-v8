package com.aptoide.android.aptoidegames.newservices

import cm.aptoide.pt.device_api.error.DeviceApiException
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppRepository
import cm.aptoide.pt.feature_apps.data.randomApp
import cm.aptoide.pt.feature_apps.domain.AppOrigin
import cm.aptoide.pt.test.gherkin.coScenario
import com.aptoide.android.aptoidegames.apkfy.FREE_FIRE_MAX_PACKAGE
import com.aptoide.android.aptoidegames.apkfy.FREE_FIRE_PACKAGE
import com.aptoide.android.aptoidegames.apkfy.ROBLOX_PACKAGE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

// An app's details come from the new services, except where only v7 can serve them: an app
// named by its v7 id, the titles that install through Play's overlay, the wallet, and any
// app that installs through Aptoide's installer, which needs v7's download location.
@ExperimentalCoroutinesApi
internal class PlayAppRepositoryTest {

  @Test
  fun `An app named by package is read from the new services`() = coScenario { scope ->
    m Given "the source of an app as the app view builds it"
    val (repository, deviceApi, v7) = repository(scope)

    m When "its details are read"
    val app = repository.getAppMeta("package_name=com.kiloo.subwaysurf/store_name=a-store")

    m Then "the new services are asked by package and v7 is not"
    assertEquals(listOf("com.kiloo.subwaysurf"), deviceApi.details)
    assertTrue(v7.calls.isEmpty())
    assertEquals(AppOrigin.DEVICE_API, app.origin)
  }

  @Test
  fun `An app named by its v7 id is read from v7`() = coScenario { scope ->
    m Given "a source naming a v7 id, which only v7 knows"
    val (repository, deviceApi, v7) = repository(scope)

    m When "its details are read"
    val app = repository.getAppMeta("app_id=123456")

    m Then "v7 is asked and the new services are not"
    assertEquals(listOf("meta:app_id=123456"), v7.calls)
    assertTrue(deviceApi.details.isEmpty())
    assertSame(v7.app, app)
  }

  @ParameterizedTest(name = "{0}")
  @ValueSource(
    strings = [ROBLOX_PACKAGE, FREE_FIRE_PACKAGE, FREE_FIRE_MAX_PACKAGE, "com.appcoins.wallet"]
  )
  fun `A title that stays on v7 is read from v7`(packageName: String) = coScenario { scope ->
    m Given "the source of a title that keeps its v7 flow"
    val (repository, deviceApi, v7) = repository(scope)

    m When "its details are read"
    repository.getAppMeta("package_name=$packageName/store_name=a-store")

    m Then "v7 is asked as before and the new services are not"
    assertEquals(listOf("meta:package_name=$packageName/store_name=a-store"), v7.calls)
    assertTrue(deviceApi.details.isEmpty())
  }

  @Test
  fun `An app flagged for Aptoide billing is read from v7 for its download`() = coScenario {
      scope ->
    m Given "new services that flag the app for Aptoide billing"
    val (repository, deviceApi, v7) = repository(scope, FakeDeviceApi(billing = true))

    m When "its details are read"
    val app = repository.getAppMeta("package_name=com.my.defense/store_name=a-store")

    m Then "the new services are asked first, then v7 for the same app in this store"
    assertEquals(listOf("com.my.defense"), deviceApi.details)
    assertEquals(listOf("meta:package_name=com.my.defense/store_name=a-store"), v7.calls)
    assertSame(v7.app, app)
  }

  @Test
  fun `A flagged app named in another store is read from v7 in this store`() = coScenario {
      scope ->
    m Given "new services that flag the app, and a source naming another store"
    val (repository, _, v7) = repository(scope, FakeDeviceApi(billing = true))

    m When "its details are read"
    repository.getAppMeta("package_name=com.my.defense/store_name=someone-elses-store")

    m Then "v7 is asked for the app in the store this build reads, not the one named"
    assertEquals(listOf("meta:package_name=com.my.defense/store_name=a-store"), v7.calls)
  }

  @Test
  fun `A flagged app named without a store is read from v7 in this store`() = coScenario { scope ->
    m Given "new services that flag the app, and a source naming no store"
    val (repository, _, v7) = repository(scope, FakeDeviceApi(billing = true))

    m When "its details are read"
    repository.getAppMeta("package_name=com.my.defense")

    m Then "v7 is asked for the app in the store this build reads"
    assertEquals(listOf("meta:package_name=com.my.defense/store_name=a-store"), v7.calls)
  }

  @Test
  fun `An app outside the catalog that installs through Aptoide is read from v7`() = coScenario {
      scope ->
    m Given "new services that do not know the app, and a v7 that flags it as a store app"
    val v7 = FakeV7AppRepository(app = randomApp.copy(bdsFlags = listOf("STORE_BDS")))
    val (repository, _, _) = repository(scope, FakeDeviceApi(failure = httpError(404)), v7)

    m When "its details are read"
    val app = repository.getAppMeta("package_name=com.arkgames.sjzt.aptoide/store_name=a-store")

    m Then "v7's app is the answer"
    assertSame(v7.app, app)
  }

  @Test
  fun `An app outside the catalog that v7 cannot install stays unknown`() = coScenario { scope ->
    m Given "new services that do not know the app, and a v7 that knows it without store flags"
    val v7 = FakeV7AppRepository(app = randomApp.copy(bdsFlags = null))
    val (repository, _, _) = repository(scope, FakeDeviceApi(failure = httpError(404)), v7)

    m When "its details are read"
    val failure = runCatching {
      repository.getAppMeta("package_name=com.example.unknown/store_name=a-store")
    }.exceptionOrNull()

    m Then "the app is reported as not in the catalog, so it is not offered from v7"
    assertInstanceOf(DeviceApiException.NotInVariant::class.java, failure)
  }

  @Test
  fun `An app outside the catalog that v7 does not know either stays unknown`() = coScenario {
      scope ->
    m Given "new services that do not know the app, and a v7 that fails on it too"
    val v7 = FakeV7AppRepository(failure = IOException("404 from v7"))
    val (repository, _, _) = repository(scope, FakeDeviceApi(failure = httpError(404)), v7)

    m When "its details are read"
    val failure = runCatching {
      repository.getAppMeta("package_name=com.example.unknown/store_name=a-store")
    }.exceptionOrNull()

    m Then "the app is reported as not in the catalog"
    assertInstanceOf(DeviceApiException.NotInVariant::class.java, failure)
  }

  @Test
  fun `An app removed from the catalog is not fetched from v7 instead`() = coScenario { scope ->
    m Given "new services that answer the app was removed"
    val (repository, _, v7) = repository(scope, FakeDeviceApi(failure = httpError(410)))

    m When "its details are read"
    val failure = runCatching {
      repository.getAppMeta("package_name=com.example.pulled/store_name=a-store")
    }.exceptionOrNull()

    m Then "the removal reaches the caller and v7 is not asked, as the app was pulled on purpose"
    assertInstanceOf(DeviceApiException.Removed::class.java, failure)
    assertTrue(v7.calls.isEmpty())
  }

  @Test
  fun `A connectivity failure of the new services reaches the caller`() = coScenario { scope ->
    m Given "new services that cannot be reached"
    val (repository, _, v7) = repository(scope, FakeDeviceApi(failure = IOException("timeout")))

    m When "its details are read"
    val failure = runCatching {
      repository.getAppMeta("package_name=com.kiloo.subwaysurf/store_name=a-store")
    }.exceptionOrNull()

    m Then "the failure reaches the caller as is, and v7 is not tried instead"
    assertInstanceOf(IOException::class.java, failure)
    assertTrue(v7.calls.isEmpty())
  }

  @Test
  fun `Reading an app by package follows the same routes`() = coScenario { scope ->
    m Given "a repository"
    val (repository, deviceApi, v7) = repository(scope)

    m When "a regular app and Roblox are read by package"
    repository.getApp("com.kiloo.subwaysurf")
    repository.getApp(ROBLOX_PACKAGE)

    m Then "the regular app comes from the new services and Roblox from v7"
    assertEquals(listOf("com.kiloo.subwaysurf"), deviceApi.details)
    assertEquals(listOf("app:$ROBLOX_PACKAGE"), v7.calls)
  }

  @Test
  fun `A flagged app read by package is read from v7`() = coScenario { scope ->
    m Given "new services that flag the app"
    val (repository, _, v7) = repository(scope, FakeDeviceApi(billing = true))

    m When "it is read by package"
    val app = repository.getApp("com.my.defense")

    m Then "v7's app is the answer"
    assertEquals(listOf("app:com.my.defense"), v7.calls)
    assertSame(v7.app, app)
  }

  private fun repository(
    scope: TestScope,
    deviceApi: FakeDeviceApi = FakeDeviceApi(),
    v7: FakeV7AppRepository = FakeV7AppRepository(),
  ): Triple<PlayAppRepository, FakeDeviceApi, FakeV7AppRepository> = Triple(
    PlayAppRepository(v7, deviceApi.dataSource(scope), storeName = "a-store"),
    deviceApi,
    v7,
  )

  private fun httpError(code: Int) = HttpException(
    Response.error<Any>(code, "{}".toResponseBody("application/problem+json".toMediaType()))
  )
}

internal class FakeV7AppRepository(
  val app: App = randomApp.copy(packageName = "v7.app"),
  private val failure: Throwable? = null,
) : AppRepository {

  val calls = mutableListOf<String>()

  override suspend fun getApp(packageName: String): App {
    calls += "app:$packageName"
    failure?.let { throw it }
    return app
  }

  override suspend fun getAppMeta(source: String): App {
    calls += "meta:$source"
    failure?.let { throw it }
    return app
  }
}
