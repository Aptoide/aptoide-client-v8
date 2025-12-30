package cm.aptoide.pt.feature_updates.data

import cm.aptoide.pt.feature_apps.data.model.AppJSON
import cm.aptoide.pt.feature_updates.data.database.AppUpdateDao
import cm.aptoide.pt.feature_updates.data.database.AppUpdateData
import cm.aptoide.pt.feature_updates.data.network.UpdatesApi
import cm.aptoide.pt.feature_updates.data.network.UpdatesRequest
import cm.aptoide.pt.feature_updates.domain.ApkData
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdatesRepository @Inject constructor(
  private val appUpdateDao: AppUpdateDao,
  private val updatesApi: UpdatesApi,
  private val storeNameProvider: StoreNameProvider,
  private val scope: CoroutineScope,
) {

  private val gson = Gson()

  suspend fun loadUpdates(apksData: List<ApkData>): List<AppJSON> =
    withContext(scope.coroutineContext) {
      val storeName = storeNameProvider.getStoreName()
      apksData.chunked(100)
        .map {
          async {
            runCatching {
              updatesApi.getAppsUpdates(
                storeName = storeName,
                request = UpdatesRequest(
                  apksData = it
                )
              )
            }
              .getOrNull()
              ?.list
              ?: emptyList() //Ignore errors from each chunk so we still have others
          }
        }
        .awaitAll()
        .flatten()
    }

  fun getUpdates(): Flow<List<AppJSON>> = appUpdateDao.getAll()
    .map { updatesList ->
      updatesList.map { gson.fromJson(it.data, AppJSON::class.java) }
    }

  suspend fun remove(vararg apps: AppJSON) = withContext(scope.coroutineContext) {
    appUpdateDao.remove(
      *apps
        .map { it.toAppUpdateData() }
        .toTypedArray()
    )
  }

  suspend fun saveOrReplace(vararg apps: AppJSON) = withContext(scope.coroutineContext) {
    appUpdateDao.save(apps.map { it.toAppUpdateData() })
  }

  private fun AppJSON.toAppUpdateData() = AppUpdateData(
    packageName = packageName!!,
    versionCode = file.vercode,
    data = gson.toJson(this)
  )
}
