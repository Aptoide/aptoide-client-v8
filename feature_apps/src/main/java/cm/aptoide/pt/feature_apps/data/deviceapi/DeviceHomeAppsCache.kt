package cm.aptoide.pt.feature_apps.data.deviceapi

import cm.aptoide.pt.feature_apps.data.App
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Bridges the device `/home` inline-apps model onto the client's lazy
 * fetch-by-`view`-URL bundle renderer. `DeviceApiWidgetsRepository` stashes each
 * home section's apps here under a synthetic `device-home:` key it also sets as the
 * widget's `view`; `DeviceApiAppsListRepository.getAppsList(view)` reads them back.
 * aptoideGamesDev only.
 */
@Singleton
class DeviceHomeAppsCache @Inject constructor() {

  private val sections = ConcurrentHashMap<String, List<App>>()

  fun put(key: String, apps: List<App>) {
    sections[key] = apps
  }

  fun get(key: String): List<App>? = sections[key]

  companion object {
    const val KEY_PREFIX = "device-home:"
    fun isHomeKey(url: String) = url.startsWith(KEY_PREFIX)
  }
}
