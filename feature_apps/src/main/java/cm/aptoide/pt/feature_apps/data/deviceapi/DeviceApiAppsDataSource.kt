package cm.aptoide.pt.feature_apps.data.deviceapi

import cm.aptoide.pt.device_api.error.DeviceApiException
import cm.aptoide.pt.device_api.error.deviceApiCall
import cm.aptoide.pt.device_api.network.DeviceProfile
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.deviceapi.model.AppSummaryResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Reads apps from the device API, within the catalog [variant] and filtered by the device
 * profile. Failures of the service reach the caller as a
 * [cm.aptoide.pt.device_api.error.DeviceApiException], connectivity ones as they are.
 *
 * Every read returns the first page only, as every list in the app is loaded in one go.
 */
class DeviceApiAppsDataSource(
  private val service: DeviceApiAppsService,
  private val variant: String,
  private val storeName: String,
  private val deviceProfile: () -> DeviceProfile,
  private val dispatcher: CoroutineDispatcher,
) {

  /** The apps of [category], a slug of `GET categories` or one of its parents. */
  suspend fun browse(
    category: String,
    sort: String = SORT_DOWNLOADS,
    limit: Int? = null,
    refresh: Boolean = false,
  ): List<App> = getApps(
    q = null,
    category = category,
    sort = sort,
    limit = limit,
    refresh = refresh,
  )

  /** The apps matching [query], in the whole catalog: the service takes no category with it. */
  suspend fun search(query: String): List<App> = getApps(
    q = query,
    category = null,
    sort = null,
    limit = null,
    refresh = false,
  )

  /**
   * The details of [packageName] within the catalog. A
   * [cm.aptoide.pt.device_api.error.DeviceApiException.NotInVariant] when the catalog does not
   * hold it.
   */
  suspend fun detail(packageName: String): App = withContext(dispatcher) {
    val profile = deviceProfile()
    deviceApiCall {
      service.getApp(
        packageName = packageName,
        variant = variant,
        sdk = profile.sdk,
        abi = profile.abi,
        tv = profile.tv,
        density = profile.density,
      )
    }.toApp(storeName)
      ?: throw DeviceApiException.Generic(
        DeviceApiException.NO_STATUS,
        "The details of $packageName name no package",
      )
  }

  suspend fun related(packageName: String, limit: Int? = null): List<App> =
    withContext(dispatcher) {
      val profile = deviceProfile()
      deviceApiCall {
        service.getRelated(
          packageName = packageName,
          limit = limit?.coerceIn(1, MAX_RELATED),
          variant = variant,
          sdk = profile.sdk,
          abi = profile.abi,
          tv = profile.tv,
          density = profile.density,
        )
      }.items.toApps().distinctBy { it.packageName }
    }

  private suspend fun getApps(
    q: String?,
    category: String?,
    sort: String?,
    limit: Int?,
    refresh: Boolean,
  ): List<App> = withContext(dispatcher) {
    val profile = deviceProfile()
    deviceApiCall {
      service.getApps(
        q = q,
        category = category,
        sort = sort,
        limit = limit?.coerceIn(1, MAX_PAGE),
        variant = variant,
        sdk = profile.sdk,
        abi = profile.abi,
        tv = profile.tv,
        density = profile.density,
        refresh = if (refresh) 1 else null,
      )
    }.items.toApps()
  }

  private fun List<AppSummaryResponse>?.toApps(): List<App> =
    orEmpty().mapNotNull { it.toApp(storeName) }

  companion object {
    const val SORT_DOWNLOADS = "downloads"
    const val SORT_LATEST = "latest"
    const val SORT_TRENDING = "trending"
    const val SORT_ALPHA = "alpha"

    // The largest pages the service accepts
    private const val MAX_PAGE = 50
    private const val MAX_RELATED = 25
  }
}
