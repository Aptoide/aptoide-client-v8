package com.aptoide.android.aptoidegames.device_info

import android.content.Context
import android.telephony.TelephonyManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Resolves the device's country as an uppercase ISO 3166-1 alpha-2 code, preferring signals that
 * reflect physical location: the current network country (MCC-based, hard to spoof), then the SIM
 * country, then the device locale. Returns null when none is available (e.g. a WiFi-only device
 * with a region-less locale).
 */
@Singleton
class DeviceCountryProvider @Inject constructor(
  @ApplicationContext private val context: Context,
) {

  fun getCountry(): String? {
    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
    return listOf(
      telephonyManager?.networkCountryIso,
      telephonyManager?.simCountryIso,
      context.resources.configuration.locales[0]?.country,
    )
      .firstOrNull { !it.isNullOrBlank() }
      ?.uppercase(Locale.US)
  }
}
