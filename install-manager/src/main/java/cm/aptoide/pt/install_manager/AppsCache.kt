package cm.aptoide.pt.install_manager

import java.lang.ref.WeakReference

internal class AppsCache {

  private val cachedApps = HashMap<String, WeakReference<RealApp>>()
  internal val busyApps = HashMap<String, RealApp?>()

  internal operator fun get(packageName: String): RealApp? =
    busyApps[packageName] ?: cachedApps[packageName]?.get()

  internal operator fun set(packageName: String, app: RealApp) {
    cachedApps[packageName] = WeakReference(app)
  }

  internal fun setBusy(packageName: String, busy: Boolean) {
    if (busy) {
      busyApps[packageName] = cachedApps[packageName]?.get()
    } else {
      busyApps.remove(packageName)
    }
  }
}
