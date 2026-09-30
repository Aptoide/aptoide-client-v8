package cm.aptoide.pt.feature_updates.data.deviceapi

import cm.aptoide.pt.device_api.network.DeviceProfile
import cm.aptoide.pt.feature_apps.data.deviceapi.model.AppResponse
import cm.aptoide.pt.feature_apps.data.deviceapi.model.ReleaseResponse
import cm.aptoide.pt.feature_apps.domain.AppOrigin
import cm.aptoide.pt.feature_updates.data.deviceapi.database.DeviceAppUpdate
import cm.aptoide.pt.feature_updates.data.deviceapi.database.DeviceAppUpdateDao
import cm.aptoide.pt.feature_updates.domain.ApkData
import cm.aptoide.pt.test.gherkin.coScenario
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.IOException

// The Play build asks the new services what is outdated, for every installed app, and keeps
// only the updates it can deliver itself: those of apps flagged for Aptoide billing. Play
// updates the rest.
@ExperimentalCoroutinesApi
internal class DeviceApiUpdatesRepositoryTest {

  private val installed = ApkData(
    signature = "D9:31:3B:8F:02:4B:AE:7D:A5:92:EE:FD:7A:A7:B9:F6:B4:A5:52:EE",
    packageName = "com.my.defense",
    versionCode = 1,
  )

  @Test
  fun `The signer is sent as forty hex characters and no catalog is named`() = coScenario {
      scope ->
    m Given "an installed app whose signer is read with colons"
    val service = FakeUpdatesService()
    val repository = repository(service, scope)

    m When "the updates are loaded"
    repository.loadUpdates(listOf(installed))

    m Then "the signer goes without them, lowercase, with the device, and without a variant"
    val request = service.requests.single()
    assertEquals("d9313b8f024bae7da592eefd7aa7b9f6b4a552ee", request.body.apps.single().signerSha1)
    assertEquals("com.my.defense", request.body.apps.single().packageName)
    assertEquals(1L, request.body.apps.single().versionCode)
    assertEquals(listOf("arm64-v8a"), request.body.device?.abis)
    assertNull(request.variant)
  }

  @Test
  fun `An app whose signer cannot be sent is left out`() = coScenario { scope ->
    m Given "an installed app with an unusable signer among a good one"
    val service = FakeUpdatesService()
    val repository = repository(service, scope)

    m When "the updates are loaded"
    repository.loadUpdates(listOf(installed, installed.copy(packageName = "x.y", signature = "")))

    m Then "only the good one is asked about, as one bad item fails the whole request"
    val asked = service.requests.single().body.apps.map { it.packageName }
    assertEquals(listOf("com.my.defense"), asked)
  }

  @Test
  fun `Installed apps are asked about a hundred at a time`() = coScenario { scope ->
    m Given "two hundred and one installed apps"
    val service = FakeUpdatesService()
    val repository = repository(service, scope)
    val many = List(201) { installed.copy(packageName = "app.$it") }

    m When "the updates are loaded"
    repository.loadUpdates(many)

    m Then "three requests went out"
    assertEquals(listOf(100, 100, 1), service.requests.map { it.body.apps.size })
  }

  @Test
  fun `Only available updates of apps installed through Aptoide are kept`() = coScenario {
      scope ->
    m Given "answers for an Aptoide-billed app, a Play app and one with no update"
    val service = FakeUpdatesService(
      answers = listOf(
        verdict("com.my.defense", "update_available", billing = true, versionCode = 2),
        verdict("com.kiloo.subwaysurf", "update_available", billing = false, versionCode = 9),
        verdict("com.other", "no_update", billing = true, versionCode = null),
      )
    )
    val repository = repository(service, scope)

    m When "the updates are loaded"
    val loaded = repository.loadUpdates(listOf(installed))

    m Then "only the Aptoide-billed update is returned and kept, read from the device API"
    assertEquals(listOf("com.my.defense" to 2), loaded.map { it.packageName to it.versionCode })
    assertEquals(listOf(AppOrigin.DEVICE_API), loaded.map { it.origin })
    assertTrue(loaded.single().isAppCoins)
    assertEquals(listOf("com.my.defense"), repository.getUpdates().first().map { it.packageName })
  }

  @Test
  fun `An update that names no package is neither returned nor kept`() = coScenario { scope ->
    m Given "an available Aptoide-billed update whose payload has no package name"
    val service = FakeUpdatesService(
      answers = listOf(
        verdict("com.my.defense", "update_available", billing = true, versionCode = 2)
          .let { it.copy(update = it.update?.copy(packageName = null)) },
        verdict("c.d", "update_available", billing = true, versionCode = 3),
      )
    )
    val dao = FakeDeviceAppUpdateDao()
    val repository = repository(service, scope, dao)

    m When "the updates are loaded"
    val loaded = repository.loadUpdates(listOf(installed))

    m Then "only the one that can be mapped is returned, and no other row is kept"
    assertEquals(listOf("c.d"), loaded.map { it.packageName })
    assertEquals(listOf("c.d"), dao.getAll().first().map { it.packageName })
  }

  @Test
  fun `A failing chunk costs only its own updates`() = coScenario { scope ->
    m Given "a service that fails on the first chunk and answers the second"
    val service = FakeUpdatesService(
      answers = listOf(verdict("app.150", "update_available", billing = true, versionCode = 2)),
      failOnRequest = 1,
    )
    val repository = repository(service, scope)
    val many = List(150) { installed.copy(packageName = "app.$it") }

    m When "the updates are loaded"
    val loaded = repository.loadUpdates(many)

    m Then "the second chunk's update is still there, returned and kept"
    assertEquals(listOf("app.150"), loaded.map { it.packageName })
    assertEquals(listOf("app.150"), repository.getUpdates().first().map { it.packageName })
  }

  @Test
  fun `An answer with no results keeps nothing`() = coScenario { scope ->
    m Given "a service answering without a results list"
    val service = FakeUpdatesService(answers = null)
    val repository = repository(service, scope)

    m When "the updates are loaded"
    val loaded = repository.loadUpdates(listOf(installed))

    m Then "there are none"
    assertTrue(loaded.isEmpty())
    assertTrue(repository.getUpdates().first().isEmpty())
  }

  @Test
  fun `Removing an update forgets it by package`() = coScenario { scope ->
    m Given "two kept updates"
    val service = FakeUpdatesService(
      answers = listOf(
        verdict("a.b", "update_available", billing = true, versionCode = 2),
        verdict("c.d", "update_available", billing = true, versionCode = 3),
      )
    )
    val repository = repository(service, scope)
    repository.loadUpdates(listOf(installed))

    m When "one is removed"
    repository.remove(listOf("a.b"))

    m Then "only the other is kept"
    assertEquals(listOf("c.d"), repository.getUpdates().first().map { it.packageName })
  }

  private fun verdict(packageName: String, status: String, billing: Boolean, versionCode: Int?) =
    UpdateVerdictResponse(
      packageName = packageName,
      status = status,
      update = versionCode?.let {
        AppResponse(
          packageName = packageName,
          name = packageName,
          aptoideBilling = billing,
          release = ReleaseResponse(versionName = "2", versionCode = it),
        )
      },
    )

  private fun repository(
    service: FakeUpdatesService,
    scope: TestScope,
    dao: FakeDeviceAppUpdateDao = FakeDeviceAppUpdateDao(),
  ) =
    DeviceApiUpdatesRepository(
      dao = dao,
      service = service,
      storeName = "a-store",
      deviceProfile = {
        DeviceProfile(sdk = 34, abis = listOf("arm64-v8a"), tv = false, density = 480)
      },
      dispatcher = StandardTestDispatcher(scope.testScheduler),
    )
}

private data class Request(val variant: String?, val body: UpdatesRequestBody)

private class FakeUpdatesService(
  private val answers: List<UpdateVerdictResponse>? = emptyList(),
  private val failOnRequest: Int? = null,
) : DeviceApiUpdatesService {

  val requests = mutableListOf<Request>()

  override suspend fun getUpdates(variant: String?, body: UpdatesRequestBody): UpdatesResponse {
    requests += Request(variant, body)
    if (requests.size == failOnRequest) throw IOException("down")
    return UpdatesResponse(results = answers)
  }
}

private class FakeDeviceAppUpdateDao : DeviceAppUpdateDao {

  private val rows = MutableStateFlow<Map<String, DeviceAppUpdate>>(emptyMap())

  override fun getAll(): Flow<List<DeviceAppUpdate>> = rows.map { it.values.toList() }

  override suspend fun save(updates: List<DeviceAppUpdate>) =
    rows.update { it + updates.associateBy { row -> row.packageName } }

  override suspend fun remove(packageNames: List<String>) =
    rows.update { it - packageNames.toSet() }
}
