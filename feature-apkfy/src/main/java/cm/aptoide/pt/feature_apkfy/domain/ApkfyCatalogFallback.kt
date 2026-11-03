package cm.aptoide.pt.feature_apkfy.domain

import cm.aptoide.pt.feature_apps.data.App

/**
 * A second catalog apkfy asks for a title v7 could not resolve. Only the builds that bind one
 * have it; every other build resolves through v7 alone - see [ApkfyAppResolver].
 */
fun interface ApkfyCatalogFallback {

  /**
   * The details of [packageName]. A
   * [cm.aptoide.pt.device_api.error.DeviceApiException.NotInVariant] when the catalog does not
   * hold it.
   */
  suspend fun detail(packageName: String): App
}
