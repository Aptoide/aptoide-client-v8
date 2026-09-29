package cm.aptoide.pt.feature_apps.data.deviceapi

import cm.aptoide.pt.device_api.error.DeviceApiException
import cm.aptoide.pt.device_api.network.DeviceProfile
import cm.aptoide.pt.feature_apps.data.deviceapi.model.AppSummaryResponse
import cm.aptoide.pt.feature_apps.data.deviceapi.model.AppsPageResponse
import cm.aptoide.pt.feature_apps.data.deviceapi.model.RelatedAppsResponse
import cm.aptoide.pt.test.gherkin.coScenario
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response

// What goes on the wire for each kind of read. The variant decides the catalog and the device
// profile filters it, so both must go on every read; a query and a category cannot be combined,
// the service rejects that.
@ExperimentalCoroutinesApi
internal class DeviceApiAppsDataSourceTest {

  private val profile = DeviceProfile(
    sdk = 34,
    abis = listOf("arm64-v8a", "armeabi-v7a"),
    tv = false,
    density = 480,
  )

  @Test
  fun `Browsing a category sends the category, the sort and the catalog`() = coScenario { scope ->
    m Given "a data source for the google-certified catalog"
    val service = FakeAppsService()
    val dataSource = dataSource(service, scope)

    m When "a category is browsed by downloads, nine at a time"
    dataSource.browse(category = "game_action", sort = "downloads", limit = 9)

    m Then "the listing is asked for exactly that, with the device profile and no query"
    assertEquals(
      ListingCall(
        q = null,
        category = "game_action",
        sort = "downloads",
        limit = 9,
        variant = "google-certified",
        sdk = 34,
        abi = "arm64-v8a,armeabi-v7a",
        tv = false,
        density = 480,
        refresh = null,
      ),
      service.listingCalls.single()
    )
  }

  @Test
  fun `Browsing returns the apps in the order the service sent them`() = coScenario { scope ->
    m Given "a listing of two apps"
    val service = FakeAppsService(listing = listOf(summary("a.first"), summary("b.second")))
    val dataSource = dataSource(service, scope)

    m When "the category is browsed"
    val apps = dataSource.browse(category = "games")

    m Then "both come back, in order, from the store this build reads"
    assertEquals(listOf("a.first", "b.second"), apps.map { it.packageName })
    assertEquals(listOf("a-store", "a-store"), apps.map { it.store.storeName })
  }

  @Test
  fun `An item that cannot become an app is skipped`() = coScenario { scope ->
    m Given "a listing where one item has no package"
    val service = FakeAppsService(listing = listOf(summary(null), summary("b.second")))
    val dataSource = dataSource(service, scope)

    m When "the category is browsed"
    val apps = dataSource.browse(category = "games")

    m Then "only the usable one comes back"
    assertEquals(listOf("b.second"), apps.map { it.packageName })
  }

  @Test
  fun `Bypassing the cache asks the service for a fresh listing`() = coScenario { scope ->
    m Given "a data source"
    val service = FakeAppsService()
    val dataSource = dataSource(service, scope)

    m When "a category is browsed bypassing the cache"
    dataSource.browse(category = "games", refresh = true)

    m Then "the refresh flag is sent"
    assertEquals(1, service.listingCalls.single().refresh)
  }

  @Test
  fun `A page size above what the service accepts is capped`() = coScenario { scope ->
    m Given "a data source"
    val service = FakeAppsService()
    val dataSource = dataSource(service, scope)

    m When "more apps are asked for than a page can hold"
    dataSource.browse(category = "games", limit = 200)

    m Then "the largest page is asked for instead of a request the service rejects"
    assertEquals(50, service.listingCalls.single().limit)
  }

  @Test
  fun `Searching sends the query and never a category nor a sort`() = coScenario { scope ->
    m Given "a data source"
    val service = FakeAppsService()
    val dataSource = dataSource(service, scope)

    m When "apps are searched"
    dataSource.search(query = "subway")

    m Then "only the query goes with the catalog and the device profile"
    val call = service.listingCalls.single()
    assertEquals("subway", call.q)
    assertNull(call.category)
    assertNull(call.sort)
    assertEquals("google-certified", call.variant)
    assertEquals("arm64-v8a,armeabi-v7a", call.abi)
  }

  @Test
  fun `Related apps are asked for within the same catalog`() = coScenario { scope ->
    m Given "a service with one related app"
    val service = FakeAppsService(related = listOf(summary("c.related")))
    val dataSource = dataSource(service, scope)

    m When "the apps related to a package are read"
    val apps = dataSource.related(packageName = "com.kiloo.subwaysurf")

    m Then "the package and the catalog are sent, and the app comes back"
    assertEquals(RelatedCall("com.kiloo.subwaysurf", "google-certified"), service.relatedCalls.single())
    assertEquals(listOf("c.related"), apps.map { it.packageName })
  }

  @Test
  fun `A service failure reaches the caller typed`() = coScenario { scope ->
    m Given "a service answering that the category is unknown"
    val service = FakeAppsService(failure = httpError(404))
    val dataSource = dataSource(service, scope)

    m When "the category is browsed"
    val failure = runCatching { dataSource.browse(category = "nope") }.exceptionOrNull()

    m Then "the failure says it is not in the catalog"
    assertInstanceOf(DeviceApiException.NotInVariant::class.java, failure)
  }

  private fun dataSource(
    service: FakeAppsService,
    scope: kotlinx.coroutines.test.TestScope,
  ) = DeviceApiAppsDataSource(
    service = service,
    variant = "google-certified",
    storeName = "a-store",
    deviceProfile = { profile },
    dispatcher = StandardTestDispatcher(scope.testScheduler),
  )
}

internal data class ListingCall(
  val q: String?,
  val category: String?,
  val sort: String?,
  val limit: Int?,
  val variant: String,
  val sdk: Int?,
  val abi: String?,
  val tv: Boolean?,
  val density: Int?,
  val refresh: Int?,
)

internal data class RelatedCall(val packageName: String, val variant: String)

internal class FakeAppsService(
  private val listing: List<AppSummaryResponse> = emptyList(),
  private val related: List<AppSummaryResponse> = emptyList(),
  private val failure: Throwable? = null,
) : DeviceApiAppsService {

  val listingCalls = mutableListOf<ListingCall>()
  val relatedCalls = mutableListOf<RelatedCall>()

  override suspend fun getApps(
    q: String?,
    category: String?,
    sort: String?,
    limit: Int?,
    variant: String,
    sdk: Int?,
    abi: String?,
    tv: Boolean?,
    density: Int?,
    refresh: Int?,
  ): AppsPageResponse {
    listingCalls += ListingCall(q, category, sort, limit, variant, sdk, abi, tv, density, refresh)
    failure?.let { throw it }
    return AppsPageResponse(items = listing, nextCursor = null)
  }

  override suspend fun getRelated(
    packageName: String,
    limit: Int?,
    variant: String,
    sdk: Int?,
    abi: String?,
    tv: Boolean?,
    density: Int?,
  ): RelatedAppsResponse {
    relatedCalls += RelatedCall(packageName, variant)
    failure?.let { throw it }
    return RelatedAppsResponse(items = related)
  }
}

internal fun summary(packageName: String?) = AppSummaryResponse(
  packageName = packageName,
  name = "App $packageName",
)

internal fun httpError(code: Int): HttpException = HttpException(
  Response.error<Any>(code, "{}".toResponseBody("application/problem+json".toMediaType()))
)
