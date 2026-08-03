package cm.aptoide.pt.feature_apps.data.deviceapi

import cm.aptoide.pt.device_api.error.deviceApiCall
import cm.aptoide.pt.device_api.network.DeviceProfileProvider
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppsListRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

/**
 * Device-API listing repository (aptoideGamesDev only) — See-All / sorted /
 * trending / category / related / versions. The current UIs are single-shot, so
 * these return the first cursor page (parity with v7, which also returns a fixed
 * list). Leftover interface methods with no device-API route stay best-effort:
 * store/group listing → empty (device home doesn't use it); batch-by-packages →
 * per-package detail (no batch endpoint yet — see leftover inventory).
 */
class DeviceApiAppsListRepository(
  private val service: DeviceApiAppsService,
  private val storeName: String,
  private val variant: String,
  private val deviceProfileProvider: DeviceProfileProvider,
  private val homeAppsCache: DeviceHomeAppsCache,
  private val scope: CoroutineScope,
) : AppsListRepository {

  override suspend fun getAppsList(url: String, bypassCache: Boolean): List<App> =
    withContext(scope.coroutineContext) {
      // Home sections carry their apps inline via /home — served from the bridge cache.
      if (DeviceHomeAppsCache.isHomeKey(url)) {
        return@withContext homeAppsCache.get(url).orEmpty()
      }
      val (sort, category) = parseListUrl(url)
      fetchBrowse(sort = sort, category = category, refresh = bypassCache)
    }

  // v7 store/group widget listing — no device-API equivalent; device home is /home.
  override suspend fun getAppsList(storeId: Long, groupId: Long, bypassCache: Boolean): List<App> =
    emptyList()

  override suspend fun getRecommended(path: String): List<App> = withContext(scope.coroutineContext) {
    val pkg = extractPackageName(path) ?: return@withContext emptyList()
    val p = deviceProfileProvider.get()
    deviceApiCall { service.getRelated(pkg, sdk = p.sdk, abi = p.abi, tv = p.tv, density = p.density) }
      .items.orEmpty().map { it.toApp(storeName) }
  }

  override suspend fun getCategoryAppsList(categoryName: String): List<App> =
    withContext(scope.coroutineContext) {
      fetchBrowse(category = categoryName, sort = "downloads", refresh = false)
    }

  override suspend fun getAppVersions(packageName: String): List<App> =
    withContext(scope.coroutineContext) {
      val p = deviceProfileProvider.get()
      val base = deviceApiCall { service.getApp(packageName, p.sdk, p.abi, p.tv, p.density) }
        .toApp(storeName)
      deviceApiCall {
        service.getReleases(packageName, sdk = p.sdk, abi = p.abi, tv = p.tv, density = p.density)
      }.items.orEmpty().mapNotNull { it.release?.toVersionApp(base, it.trust) }
    }

  // Leftover: batch app summaries by package list (My Games hydration). No batch
  // device endpoint yet — fan out to per-package detail in parallel.
  override suspend fun getAppsList(packageNames: String): List<App> =
    withContext(scope.coroutineContext) {
      val p = deviceProfileProvider.get()
      packageNames.split(",").mapNotNull { it.trim().takeIf(String::isNotEmpty) }
        .map { pkg ->
          async {
            runCatching {
              deviceApiCall { service.getApp(pkg, p.sdk, p.abi, p.tv, p.density) }.toApp(storeName)
            }.getOrNull()
          }
        }
        .awaitAll()
        .filterNotNull()
    }

  override suspend fun getSortedAppsList(sort: String, limit: Int): List<App> =
    withContext(scope.coroutineContext) {
      fetchBrowse(sort = mapSort(sort), limit = limit, refresh = false)
    }

  private suspend fun fetchBrowse(
    q: String? = null,
    category: String? = null,
    sort: String? = null,
    limit: Int? = null,
    refresh: Boolean,
  ): List<App> {
    val p = deviceProfileProvider.get()
    return deviceApiCall {
      service.searchOrBrowse(
        q = q, category = category, sort = sort, cursor = null, limit = limit,
        variant = variant, sdk = p.sdk, abi = p.abi, tv = p.tv, density = p.density,
        refresh = if (refresh) 1 else null,
      )
    }.items.orEmpty().map { it.toApp(storeName) }
  }

  private fun parseListUrl(url: String): Pair<String?, String?> {
    val sort = Regex("[?&]sort=([^&/]+)").find(url)?.groupValues?.getOrNull(1)
    val category = Regex("[?&]category=([^&/]+)").find(url)?.groupValues?.getOrNull(1)
      ?: Regex("group_name=([^&/]+)").find(url)?.groupValues?.getOrNull(1)
    return sort?.let(::mapSort) to category
  }
}

/** Maps v7 sort tokens (`trending60d`, `pdownloads`, `updated`, …) to the new `ListingSort`. */
internal fun mapSort(v7Sort: String): String = when {
  v7Sort.contains("trending", ignoreCase = true) -> "trending"
  v7Sort.contains("download", ignoreCase = true) -> "downloads"
  v7Sort.contains("alpha", ignoreCase = true) -> "alpha"
  v7Sort.contains("latest", ignoreCase = true) ||
    v7Sort.contains("updated", ignoreCase = true) ||
    v7Sort.contains("added", ignoreCase = true) ||
    v7Sort.contains("new", ignoreCase = true) -> "latest"
  else -> "downloads"
}
