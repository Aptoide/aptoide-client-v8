package cm.aptoide.pt.feature_updates.data

import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7ListResponse
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppsListMapper
import cm.aptoide.pt.feature_apps.data.model.AppJSON
import cm.aptoide.pt.feature_apps.data.randomApp
import cm.aptoide.pt.feature_updates.data.database.AppUpdateDao
import cm.aptoide.pt.feature_updates.data.database.AppUpdateData
import cm.aptoide.pt.feature_updates.data.network.UpdatesApi
import cm.aptoide.pt.feature_updates.data.network.UpdatesRequest
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/** A v7 app payload with just what updates read from it. */
internal fun appJson(packageName: String, versionCode: Int): AppJSON = Gson().fromJson(
  """{"package": "$packageName", "file": {"vercode": $versionCode, "vername": "1"}}""",
  AppJSON::class.java,
)

/** Maps a v7 payload to an app by package and version, which is all these tests compare. */
internal class FakeAppsListMapper : AppsListMapper {
  override fun map(appJSONs: List<AppJSON>): List<App> = appJSONs.map {
    randomApp.copy(packageName = it.packageName.orEmpty(), versionCode = it.file.vercode)
  }
}

/** The v7 updates api, answering each chunk in turn and recording the chunks asked. */
internal class FakeUpdatesApi(
  private val answers: List<Result<List<AppJSON>>>,
) : UpdatesApi {

  val requests = mutableListOf<UpdatesRequest>()

  override suspend fun getAppsUpdates(
    storeName: String?,
    request: UpdatesRequest,
  ): BaseV7ListResponse<AppJSON> {
    requests += request
    val answer = answers.getOrElse(requests.size - 1) { Result.success(emptyList()) }
    return BaseV7ListResponse(list = answer.getOrThrow(), info = null, error = null)
  }
}

/** An in-memory stand-in for the Room table. */
internal class FakeAppUpdateDao : AppUpdateDao {

  private val rows = MutableStateFlow<Map<String, AppUpdateData>>(emptyMap())

  override fun getAll(): Flow<List<AppUpdateData>> = rows.map { it.values.toList() }

  override suspend fun save(data: List<AppUpdateData>) =
    rows.update { it + data.associateBy { row -> row.packageName } }

  override suspend fun remove(vararg data: AppUpdateData) =
    rows.update { it - data.map { row -> row.packageName }.toSet() }

  override suspend fun clear() = rows.update { emptyMap() }
}
