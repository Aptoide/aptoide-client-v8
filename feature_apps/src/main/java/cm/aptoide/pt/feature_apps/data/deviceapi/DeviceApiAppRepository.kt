package cm.aptoide.pt.feature_apps.data.deviceapi

import cm.aptoide.pt.device_api.error.deviceApiCall
import cm.aptoide.pt.device_api.network.DeviceProfileProvider
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

/**
 * Device-API app-detail repository (aptoideGamesDev only). Distinct type from the
 * v7 [cm.aptoide.pt.feature_apps.data.AptoideAppRepository] so the `@BindsOptionalOf`
 * switch selects it without a duplicate binding.
 */
class DeviceApiAppRepository(
  private val service: DeviceApiAppsService,
  private val storeName: String,
  private val deviceProfileProvider: DeviceProfileProvider,
  private val scope: CoroutineScope,
) : AppRepository {

  override suspend fun getApp(packageName: String): App = withContext(scope.coroutineContext) {
    val p = deviceProfileProvider.get()
    deviceApiCall { service.getApp(packageName, p.sdk, p.abi, p.tv, p.density) }.toApp(storeName)
  }

  override suspend fun getAppMeta(source: String): App {
    // The common deep-link case carries `package_name=…`. Opaque ad/campaign source
    // tokens have no device-API resolution (leftover — see .context/leftover-inventory.md).
    val packageName = extractPackageName(source)
      ?: throw IllegalStateException("Unsupported device-API source: $source")
    return getApp(packageName)
  }
}

/** Pulls a package name out of a v7-style source string (`package_name=X/…`). */
internal fun extractPackageName(source: String): String? =
  Regex("package_name=([^/&]+)").find(source)?.groupValues?.getOrNull(1)
