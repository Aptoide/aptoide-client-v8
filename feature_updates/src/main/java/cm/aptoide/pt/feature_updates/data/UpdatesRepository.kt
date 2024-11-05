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
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdatesRepository @Inject constructor(
  private val appUpdateDao: AppUpdateDao,
  private val updatesApi: UpdatesApi,
  private val storeName: String,
  private val scope: CoroutineScope,
) {

  private val gson = Gson()

  suspend fun loadUpdates(apksData: List<ApkData>): List<AppJSON> =
    withContext(scope.coroutineContext) {
      apksData.chunked(100)
        .map {
          async {
            updatesApi.getAppsUpdates(
              storeName = storeName,
              request = UpdatesRequest(
                apksData = it
              )
            )
          }
        }
        .map {
          runCatching { it.await() }.getOrNull() // Ignore network errors
            ?.list
            ?: emptyList()
        }
        .ifEmpty { null }
        ?.reduce { acc, item -> acc + item }
        ?: emptyList()
    }

  suspend fun getUpdates(): List<AppJSON> = withContext(scope.coroutineContext) {
    appUpdateDao.getAll()
      .map { gson.fromJson(it.data, AppJSON::class.java) }
  }

  suspend fun replaceWith(vararg apps: AppJSON) = withContext(scope.coroutineContext) {
    appUpdateDao.clear()
    appUpdateDao.save(
      apps.map {
        AppUpdateData(
          packageName = it.packageName!!,
          versionCode = it.file.vercode,
          data = gson.toJson(it)
        )
      }
    )
  }
}
