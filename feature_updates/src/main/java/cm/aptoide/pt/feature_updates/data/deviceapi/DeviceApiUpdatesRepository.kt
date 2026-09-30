package cm.aptoide.pt.feature_updates.data.deviceapi

import cm.aptoide.pt.device_api.error.deviceApiCall
import cm.aptoide.pt.device_api.network.DeviceProfile
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.deviceapi.model.AppResponse
import cm.aptoide.pt.feature_apps.data.deviceapi.toApp
import cm.aptoide.pt.feature_updates.data.UpdatesRepository
import cm.aptoide.pt.feature_updates.data.deviceapi.database.DeviceAppUpdate
import cm.aptoide.pt.feature_updates.data.deviceapi.database.DeviceAppUpdateDao
import cm.aptoide.pt.feature_updates.domain.ApkData
import com.google.gson.Gson
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * The updates as the device API answers them. Only those of apps that install through
 * Aptoide - the ones flagged for Aptoide billing - are kept: Play updates the rest, and this
 * build has no download of its own for them.
 */
class DeviceApiUpdatesRepository(
  private val dao: DeviceAppUpdateDao,
  private val service: DeviceApiUpdatesService,
  private val storeName: String,
  private val deviceProfile: () -> DeviceProfile,
  private val dispatcher: CoroutineDispatcher,
) : UpdatesRepository {

  private val gson = Gson()

  override suspend fun loadUpdates(apksData: List<ApkData>): List<App> =
    withContext(dispatcher) {
      val device = deviceProfile().toRequest()
      val updates = coroutineScope {
        apksData.mapNotNull { it.toRequest() }
          .chunked(CHUNK_SIZE)
          .map { chunk ->
            async {
              try {
                deviceApiCall {
                  service.getUpdates(
                    variant = null,
                    body = UpdatesRequestBody(apps = chunk, device = device),
                  )
                }.results.orEmpty()
              } catch (e: CancellationException) {
                throw e
              } catch (_: Exception) {
                emptyList() // A failing chunk costs only its own updates
              }
            }
          }
          .map { it.await() }
          .flatten()
      }
      // Kept and returned are the same set: an answer that does not map to an app is dropped
      val available = updates
        .filter { it.status == UPDATE_AVAILABLE }
        .mapNotNull { it.update }
        .filter { it.aptoideBilling == true }
        .mapNotNull { response -> response.toApp(storeName)?.let { app -> response to app } }
      dao.save(available.map { (response, app) -> response.toRow(app) })
      available.map { (_, app) -> app }
    }

  override fun getUpdates(): Flow<List<App>> = dao.getAll()
    .map { rows ->
      rows.mapNotNull { gson.fromJson(it.data, AppResponse::class.java).toApp(storeName) }
    }

  override suspend fun remove(packageNames: List<String>) = withContext(dispatcher) {
    dao.remove(packageNames)
  }

  // The signer is read with colons between its bytes; the service takes the forty hex
  // characters alone, and one item it cannot read fails the whole request
  private fun ApkData.toRequest(): InstalledAppRequest? {
    val signer = signature.replace(":", "").lowercase()
    if (!SIGNER.matches(signer)) return null
    return InstalledAppRequest(
      packageName = packageName,
      versionCode = versionCode,
      signerSha1 = signer,
    )
  }

  private fun DeviceProfile.toRequest() = DeviceRequest(
    sdk = sdk,
    abis = abis.takeIf { it.isNotEmpty() },
    tv = tv,
    density = density,
  )

  private fun AppResponse.toRow(app: App) = DeviceAppUpdate(
    packageName = app.packageName,
    versionCode = app.versionCode,
    data = gson.toJson(this),
  )

  private companion object {
    const val CHUNK_SIZE = 100
    const val UPDATE_AVAILABLE = "update_available"
    val SIGNER = Regex("[0-9a-f]{40}")
  }
}
