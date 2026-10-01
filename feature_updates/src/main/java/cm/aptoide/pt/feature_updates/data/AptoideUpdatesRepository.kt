package cm.aptoide.pt.feature_updates.data

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppsListMapper
import cm.aptoide.pt.feature_apps.data.model.AppJSON
import cm.aptoide.pt.feature_updates.data.database.AppUpdateDao
import cm.aptoide.pt.feature_updates.data.database.AppUpdateData
import cm.aptoide.pt.feature_updates.data.network.UpdatesApi
import cm.aptoide.pt.feature_updates.data.network.UpdatesRequest
import cm.aptoide.pt.feature_updates.domain.ApkData
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/** The updates as v7 answers them, kept as the payloads they came in. */
class AptoideUpdatesRepository(
  private val appUpdateDao: AppUpdateDao,
  private val updatesApi: UpdatesApi,
  private val storeNameProvider: StoreNameProvider,
  private val mapper: AppsListMapper,
  private val dispatcher: CoroutineDispatcher,
) : UpdatesRepository {

  private val gson = Gson()

  override suspend fun loadUpdates(apksData: List<ApkData>): List<App> =
    withContext(dispatcher) {
      val storeName = storeNameProvider.getStoreName()
      val updates = coroutineScope {
        apksData.chunked(CHUNK_SIZE)
          .map {
            async {
              runCatching {
                updatesApi.getAppsUpdates(
                  storeName = storeName,
                  request = UpdatesRequest(apksData = it),
                )
              }
                .getOrNull()
                ?.list
                ?: emptyList() // A failing chunk costs only its own updates
            }
          }
          .map { it.await() }
          .flatten()
      }
      appUpdateDao.save(updates.map { it.toAppUpdateData() })
      mapper.map(updates)
    }

  override fun getUpdates(): Flow<List<App>> = appUpdateDao.getAll()
    .map { rows -> mapper.map(rows.map { gson.fromJson(it.data, AppJSON::class.java) }) }

  // The table keys on the package, which is all a removal needs
  override suspend fun remove(packageNames: List<String>) = withContext(dispatcher) {
    appUpdateDao.remove(
      *packageNames
        .map { AppUpdateData(packageName = it, versionCode = 0, data = "") }
        .toTypedArray()
    )
  }

  private fun AppJSON.toAppUpdateData() = AppUpdateData(
    packageName = packageName!!,
    versionCode = file.vercode,
    data = gson.toJson(this)
  )

  private companion object {
    const val CHUNK_SIZE = 100
  }
}
