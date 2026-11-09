package com.aptoide.android.aptoidegames.ads

import com.aptoide.android.aptoidegames.device_info.DeviceCountryProvider
import javax.inject.Inject

/** Resolves the device geo used by the ad geo blocklists. Abstracted so controllers are testable. */
fun interface AdGeoProvider {
  fun geo(): String

  companion object {
    /** Returned when the country cannot be resolved; never eligible while a blocklist is active. */
    const val UNKNOWN_GEO = "UNKNOWN"
  }
}

class DeviceAdGeoProvider @Inject constructor(
  private val countryProvider: DeviceCountryProvider,
) : AdGeoProvider {
  override fun geo(): String = countryProvider.getCountry() ?: AdGeoProvider.UNKNOWN_GEO
}
